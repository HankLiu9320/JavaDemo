package com.jd.myutils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单元测试断言扫描
 */
public class TestScanner {
    private static final Pattern TEST_METHOD_PATTERN = Pattern.compile("@Test\\s*(\\(\\s*expected\\s*=\\s*[^)]*\\))?\\s*public\\s*void\\s*(\\w+)\\s*\\(\\s*\\)\\s*(throws\\s+[\\w\\s,]+)?\\s*\\{");
    private static final Pattern ASSERT_OR_VERIFY_PATTERN = Pattern.compile("(assert\\s*[^;]*;|assert\\w*\\s*\\(|verify\\s*\\(|assertThat\\s*\\()");
    private static final Pattern COMMENT_ASSERT_PATTERN = Pattern.compile("//.*assert.*");
    private static final Pattern TRY_PATTERN = Pattern.compile("\\btry\\b");
    private static final Pattern CATCH_PATTERN = Pattern.compile("\\bcatch\\b");
    //缺失断言个数
    private static Long invalidAssertionsCount = 0L;
    //包含try-catch的用例个数
    private static Long containTryCatchCount = 0L;
    //用例总个数path
    private static Long unitCount = 0L;
    //用例缺失断言率
    private static Double invalidAssertionsRate = 0D;
    private static Double containTryCatchRate = 0D;

    public static void main(String[] args) {
        String testDirectoryPath = "/Users/liujianjia/1_code/code/jd/bdp_code_idea/jbdp_urm";
        File testDirectory = new File(testDirectoryPath);
        if(!testDirectory.exists() || !testDirectory.isDirectory()) {
            System.err.println("目录不存在");
            return;
        }
        Collection<File> testFiles = FileUtils.listFiles(testDirectory, new String[]{"java"}, true);
        if(CollectionUtils.isEmpty(testFiles)) {
            System.err.println("目录下没有找到java文件");
            return;
        }
        System.err.println("经检测，以下用例缺少断言或者包含try-catch:");
        // 过滤文件名不匹配*Test*.java的文件
        testFiles.removeIf(file -> !file.getName().matches(".*Test.*\\.java"));
        for(File testFile : testFiles) {
            try {
                String content = FileUtils.readFileToString(testFile, "UTF-8");
                scanTestFile(content, testFile.getName());
            }
            catch(IOException e) {
                System.err.println("Error reading file: " + testFile.getName());
                e.printStackTrace();
            }
        }
        String formattedRate = null;
        String formattedTryCatchRate = null;
        if(unitCount == 0) {
            System.out.println(testDirectoryPath + " has no units");
        }
        else {
            invalidAssertionsRate = (invalidAssertionsCount.doubleValue() / unitCount.doubleValue()) * 100;
            containTryCatchRate = (containTryCatchCount.doubleValue() / unitCount.doubleValue()) * 100;
            formattedRate = String.format("%.2f%%", invalidAssertionsRate);
            formattedTryCatchRate = String.format("%.2f%%", containTryCatchRate);

        }
        System.err.println("检测目录: " + testDirectoryPath);
        System.err.println("缺失断言个数: " + invalidAssertionsCount + " ，包含try-catch的用例个数： " + containTryCatchCount + ", 用例总个数: " + unitCount);
        System.err.println("断言缺失率: " + formattedRate + " , 包含try-catch的用例率: " + formattedTryCatchRate);
    }

    private static void scanTestFile(String content, String fileName) {
        Matcher testMethodMatcher = TEST_METHOD_PATTERN.matcher(content);
        while(testMethodMatcher.find()) {
            unitCount++;
            String expected = testMethodMatcher.group(1);
            String testMethod = testMethodMatcher.group(2);
            int testMethodStart = testMethodMatcher.end(); // 方法体开始的位置
            int testMethodEnd = findMethodEnd(content, testMethodStart);
            String methodBody = content.substring(testMethodStart, testMethodEnd);

            if(expected != null && !expected.isEmpty()) {
                continue;
            }

            boolean hasAssertion = false;
            boolean hasTry = false;
            boolean hasCatch = false;
            String[] lines = methodBody.split("\\n");
            for(String line : lines) {
                if(COMMENT_ASSERT_PATTERN.matcher(line).find()) {
                    continue; // 忽略注释掉的断言行
                }
                if(ASSERT_OR_VERIFY_PATTERN.matcher(line).find()) {
                    hasAssertion = true;
//                    break;
                }
                if(TRY_PATTERN.matcher(line).find()) {
                    hasTry = true;
                }
                if(CATCH_PATTERN.matcher(line).find()) {
                    hasCatch = true;
                }
            }

            if(!hasAssertion) {
                System.out.println("类名: '" + fileName + "', 方法名: '" + testMethod + "' 缺少断言");
                invalidAssertionsCount++;
            }
            if(hasTry && hasCatch) {
                System.out.println("类名: '" + fileName + "', 方法名: '" + testMethod + "' 包含 try 和 catch");
                containTryCatchCount++;
            }
        }
    }

    private static int findMethodEnd(String content, int start) {
        int depth = 1;
        for(int i = start; i < content.length(); i++) {
            if(content.charAt(i) == '{') {
                depth++;
            }
            else if(content.charAt(i) == '}') {
                depth--;
                if(depth == 0) {
                    return i + 1;
                }
            }
        }
        return content.length();
    }
}