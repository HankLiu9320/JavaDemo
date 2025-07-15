package com.jd.myutils.log;

import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;

public class LogMatchUtil {
    private static String regexStr = "/bin/sh: line.*Killed.*/bin/bash \"\\$@\"\n" +
            "put: File does not exist:.*does not have any open files\n" +
            "failed: Error opening Hive split .*: Malformed ORC file. Invalid postscript\n" +
            "Error in query: Can not create the managed table(\\S+) The associated location(\\S+) already exists.\n" +
            "FAILED: SemanticException \\[Error 10001\\]: Line (\\S+) Table not found\n" +
            "Failed to submit application_(\\S+) to YARN : User (\\S+) cannot submit applications to queue\n" +
            "MetaException\\(message:JD:用户(\\S+)没有表(\\S+)敏感表(\\S+)的(\\S+)权限\n" +
            "JD:用户(\\S+)没有表(\\S+)敏感表(\\S+)的(\\S+)权限\n" +
            "YarnAllocator: Container killed by YARN for exceeding memory limits.*physical memory used. Consider boosting\n" +
            "  spark.yarn.executor.memoryOverhead.\n" +
            "Error in query: Invalid usage of '\\*' in expression\n" +
            "Unable to execute method public java.lang.String \\S+evaluate\n" +
            "org.apache.spark.SparkException: Cannot broadcast the table with 512 million or more rows: .* rows\n" +
            "AnalysisException: .* requires that the data to be inserted have the same number of columns as the target\n" +
            "  table: target table has .* column\n" +
            "IOError: \\[Errno 32\\] Broken pipe\n" +
            "SetCommand: 'SET hive.exec.max.dynamic.partitions=\\d+' might not work\n" +
            "Permission denied: user=.*, access=.+, inode=\\\"/user/.*\\\"\n" +
            "diagnostics: Application.* failed .* times\n" +
            "due to AM Container for .* exited\n" +
            "with exitCode: 13\n" +
            "MetaException: the table: .* doesn't have the jdhive_storage_policy of\n" +
            "the param\n" +
            "Table .* AlreadyExistsException\n" +
            "Exception in thread \"main\" java.io.FileNotFoundException: File.*does not exist\n" +
            "post_status_to_aiflow  \\d+ FAILED\n" +
            "用户【.*】 ,对任务实例【ID:\\d+】进行了强制失败操作\n" +
            "com.mysql.jdbc.exceptions.jdbc4.MySQLQueryInterruptedException: vttablet: rpc error: code = Aborted desc = transaction .*unlocked closed\n" +
            "  connection\n" +
            "FAILED: ParseException line \\d+:\\d+ cannot recognize input near .* in table name\n" +
            "Error in query: cannot resolve .* given input columns: \n" +
            "AnalysisException: cannot resolve.*given input columns:\n" +
            "AlreadyExistsException\\(message:Table.*already exists\n" +
            "num of table .* less than 10!\n" +
            "Unavailable prefix <.*>, should be \n" +
            "Error in query: Reference.* is ambiguous, could be\n" +
            "AnalysisException: Reference.+is ambiguous, could be:\n" +
            "curl: .+ Failed connect to.*; Connection refused\n" +
            "\\.sh: line .+: unary operator expected\n" +
            "数据导入失败.+java.net.SocketTimeoutException: Read timed out\n" +
            "http://decision-engine\\.jd\\.com.+failed, res code\n" +
            "rm: `hdfs://.+': No such file or directory\n" +
            "put: `.+': No such file or directory\n" +
            "ls: `hdfs://.+': No such file or directory\n" +
            "java.sql.SQLException: vtgate: .+table .+ not found\n" +
            "java.sql.SQLException:.+closed.+CallerID\n" +
            "java.sql.SQLException: vtgate:.+exceeded timeout:\n" +
            "AnalysisException: Column .+ not found in schema\n" +
            "ClickHouseUnknownException:.+Timeout exceeded: elapsed\n" +
            "ClickHouseUnknownException.+DB::Exception: Table.+doesn't exist\n" +
            "\\[FATAL\\]run check_data_all_dict.*sh failed\n" +
            "Unable to close file because the last blockBP.*does not have enough number of replicas\n" +
            "\\[ERR\\] Remove file .+ failed";

    // 预编译10个常见日志匹配正则表达式
    private static final List<Pattern> REGEX_PATTERNS = new ArrayList<>();

    public static void main(String[] args) {
        init();
        // 1. 准备测试日志数据
        String logData = generateLogData(5000);  // 生成5000行日志
        int warmupCycles = 100;  // JVM预热次数
        int testCycles = 1000;   // 正式测试次数

        // 2. JVM预热（避免编译优化影响结果）
        System.out.println("开始JVM预热...");
        runBenchmark(logData, warmupCycles, false);

        // 3. 正式性能测试
        System.out.println("\n开始性能测试...");
        Map<Integer, Long> results = runBenchmark(logData, testCycles, true);

        // 4. 分析并展示结果
        analyzeResults(results, testCycles);
    }

    private static void init() {
        String[] split = regexStr.split("\\n");
        for(String s : split) {
            Pattern compile = Pattern.compile(s);
            REGEX_PATTERNS.add(compile);
        }

        System.err.println(REGEX_PATTERNS.size());
    }

    // 生成模拟日志数据
    private static String generateLogData(int lineCount) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < lineCount; i++) {
            sb.append(String.format("2025-07-14 10:%02d:%02d [%s] user_id=%d visited https://example.com/path/%d\n",
                    rand.nextInt(60), rand.nextInt(60),
                    rand.nextBoolean() ? "ERROR" : "INFO",
                    rand.nextInt(100000),
                    rand.nextInt(1000)));
        }
        return sb.toString();
    }

    // 执行基准测试
    private static Map<Integer, Long> runBenchmark(String logData, int cycles, boolean collectResults) {
        Map<Integer, Long> resultMap = new HashMap<>();

        for(int cycle = 0; cycle < cycles; cycle++) {
            for(int i = 0; i < REGEX_PATTERNS.size(); i++) {
                Pattern pattern = REGEX_PATTERNS.get(i);
                Matcher matcher = pattern.matcher(logData);

                long startTime = System.nanoTime();
                // 执行匹配操作
                while(matcher.find()) {
                    // 实际应用中可处理匹配结果
                }
                long duration = System.nanoTime() - startTime;

                // 收集测试结果
                if(collectResults) {
                    resultMap.merge(i, duration, Long::sum);
                }
            }
        }
        return resultMap;
    }

    // 分析并展示结果
    private static void analyzeResults(Map<Integer, Long> results, int testCycles) {
        System.out.println("\n正则表达式性能报告 (测试轮次: " + testCycles + ")");
        System.out.println("==========================================================");
        System.out.println("ID | 正则表达式模式 | 总耗时(ms) | 平均耗时(ms)");
        System.out.println("----------------------------------------------------------");

        List<Map.Entry<Integer, Long>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort(Map.Entry.comparingByValue());

        for(Map.Entry<Integer, Long> entry : sortedResults) {
            int regexId = entry.getKey();
            long totalNanos = entry.getValue();
            double totalMillis = totalNanos / 1_000_000.0;
            double avgMillis = totalMillis / testCycles;

            System.out.printf("%-2d | %-40s | %9.3f | %9.5f\n",
                    regexId + 1,
                    abbreviatePattern(REGEX_PATTERNS.get(regexId).pattern()),
                    totalMillis,
                    avgMillis);
        }
    }

    // 简化正则表达式显示
    private static String abbreviatePattern(String pattern) {
        return pattern.length() > 40 ? pattern.substring(0, 37) + "..." : pattern;
    }
}
