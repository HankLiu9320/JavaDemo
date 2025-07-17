package com.jd.myutils.log.match;

import cn.hutool.core.lang.Pair;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatchAnalyse {
    private static long cnt = 0;
    private static long errCnt = 0;
    private static long matchRows = 0;
    private static long matchPatternTime = 0;
    private static long matchContainsTime = 0;

    public static void main(String[] args) throws IOException {
        String buffalo = "/Users/liujianjia/3_data/error-analyse/buffalo_logs_error";
        String ide = "/Users/liujianjia/3_data/error-analyse/ide_logs_error";
        String excelRulesfilePath = "/Users/liujianjia/3_data/error-analyse/match-rule.xlsx";

        List<RuleInfo> ruleInfos = ExcelReader.loadMatchRules(excelRulesfilePath);
        System.err.println(ruleInfos.size());
        Pair<List<RuleInfo>, List<RuleInfo>> listListPair = initMatchRule(ruleInfos);
        scanDirectoryForMatchRule(buffalo, listListPair.getKey(), listListPair.getValue());

//        ruleInfos.sort(new Comparator<RuleInfo>() {
//            @Override
//            public int compare(RuleInfo o1, RuleInfo o2) {
//                return new Long(o1.getMatchNum() - o2.getMatchNum()).intValue();
//            }
//        });
//        for(RuleInfo ruleInfo : ruleInfos) {
//            System.err.println(ruleInfo.getPattern() + "," + ruleInfo.getMatchNum());
//        }

        ExcelReader.writeExcel(excelRulesfilePath, listListPair.getKey(), 6);

    }

    private static Pair<List<RuleInfo>, List<RuleInfo>> initMatchRule(List<RuleInfo> rules) {
        List<RuleInfo> regexList = new ArrayList<>();
        List<RuleInfo> containsList = new ArrayList<>();

        for(RuleInfo s : rules) {
            if(s.getMatchType() == 0) {
                Pattern compile = Pattern.compile(s.getPattern());
                s.setCompile(compile);
                regexList.add(s);
            }
            else {
                containsList.add(s);
            }
        }

        return Pair.of(regexList, containsList);
    }

    public static void scanDirectoryForMatchRule(String directoryPath, List<RuleInfo> patterns, List<RuleInfo> contains) throws IOException {
        List<File> files = Arrays.stream(Objects.requireNonNull(new File(directoryPath).listFiles()))
                .filter(File::isFile)
                .collect(Collectors.toList());
        long t = 0;

        // 处理每个文件
        long matchCnt = 0;
        for(File file : files) {
            long startTime = System.nanoTime();
            Pair<List<String>, List<RuleInfo>> listListPair = matchFile(file, patterns, contains);
            long duration = System.nanoTime() - startTime;
            t += duration;

            if(listListPair.getKey().size() > 0) {
                matchCnt++;
                System.err.println("file:" + file.getAbsolutePath() + ",匹配成功");
                System.err.println(StringUtils.join(listListPair.getKey(), "\n"));
            }
            else {
                System.err.println("file:" + file.getAbsolutePath() + "," + listListPair.getKey().size());
            }
        }
        System.err.println("匹配到的数量：" + matchCnt + "(" + files.size() + ")");
        System.err.println("平均一个文件耗时：" + t * 1.0 / files.size());
        System.err.println("总匹配行数：" + matchRows);
        System.err.println("正则总耗时：" + matchPatternTime);
        System.err.println("包含总耗时：" + matchContainsTime);
        System.err.println("每条正则耗时：" + (matchPatternTime / matchRows * 1.0));
        System.err.println("每条包含耗时：" + (matchContainsTime / matchRows * 1.0));
    }

    private static Pair<List<String>, List<RuleInfo>> matchFile(File file, List<RuleInfo> patterns, List<RuleInfo> contains) throws IOException {
        List<String> lines = new ArrayList<>();
        List<RuleInfo> matchRules = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while((line = reader.readLine()) != null) {
                matchRows++;
                long start = System.nanoTime();

                for(RuleInfo pattern : patterns) {
                    Matcher matcher = pattern.getCompile().matcher(line);
                    // 执行匹配操作
                    while(matcher.find()) {
                        pattern.incrNum();
                        // 实际应用中可处理匹配结果
                        lines.add(line);
                        matchRules.add(pattern);
                    }
                }

                long time1 = System.nanoTime();
                matchPatternTime += (time1 - start);

                for(RuleInfo contain : contains) {
                    if(line.contains(contain.getPattern())) {
                        contain.incrNum();
                        lines.add(line);
                        matchRules.add(contain);
                    }
                }

                long time2 = System.nanoTime();
                matchContainsTime += (time2 - time1);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return Pair.of(lines, matchRules);
    }
}