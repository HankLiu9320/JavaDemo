package com.jd.myutils.log.match;

import cn.hutool.core.lang.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ErrorLogScanner {
    private static long cnt = 0;
    private static long errCnt = 0;
    private static long matchRows = 0;
    private static long matchPatternTime = 0;
    private static long matchContainsTime = 0;

    public static void main(String[] args) throws IOException {
        String sourceDir = "/Users/liujianjia/3_data/error-analyse/buffalo_logs";
        String targetDir = "/Users/liujianjia/3_data/error-analyse/buffalo_logs_error";

        sourceDir = "/Users/liujianjia/3_data/error-analyse/ide_logs";
        targetDir = "/Users/liujianjia/3_data/error-analyse/ide_logs_error";
        scanDirectoryForErrors(sourceDir, targetDir);

//        File file = new File("/Users/liujianjia/3_data/ide_logs/2027800893.log");
//        processFile(file, targetDir);

        System.err.println("日志总行：" + cnt);
        System.err.println("错误日志总行：" + errCnt);
        System.err.println("错误日志总行：" + (errCnt * 1.00 / cnt * 1.00) + "%");

    }

    public static void scanDirectoryForErrors(String directoryPath, String targetDir) throws IOException {
        List<File> files = Arrays.stream(Objects.requireNonNull(new File(directoryPath).listFiles()))
                .filter(File::isFile)
                .collect(Collectors.toList());

        // 处理每个文件
        for(File file : files) {
            processFile(file, targetDir);
        }
    }

    private static void processFile(File file, String targetDir) throws IOException {
        System.err.println("file:" + file.getAbsolutePath());
        String errName = file.getName() + "-error.log";
        String errFilePath = targetDir + File.separator + errName;
        BufferedWriter errorWriter = new BufferedWriter(new FileWriter(errFilePath, true));

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while((line = reader.readLine()) != null) {
                cnt++;
                // 检测错误关键词（不区分大小写）
                String newLine = line.toLowerCase();
                if(newLine.contains("exception") || newLine.contains("error") || newLine.contains("failed") || newLine.contains("killed")) {
                    errCnt++;
                    // 写入错误行信息
                    errorWriter.write(line);
                    errorWriter.newLine();
                }
            }

            errorWriter.flush();
            errorWriter.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}