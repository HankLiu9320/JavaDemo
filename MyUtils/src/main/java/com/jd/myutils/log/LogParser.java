package com.jd.myutils.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    public static void main(String[] args) {
        // 替换为你的日志文件路径
        String logFilePath = "/Users/liujianjia/Downloads/log.log";

        // 正则表达式模式，用于匹配appId和token
        String regex = "appId=([^&]+)&token=([^&\\s]+)";
        String regex2 = "(time=)(\\d+)";

        Pattern pattern = Pattern.compile(regex);
        Map<String, Set<String>> map = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while((line = reader.readLine()) != null) {
                String get = line.substring(line.indexOf("GET") + 4, line.indexOf(" HTTP/1.0"));
                // 使用replaceAll方法替换time值
                String updatedUrl = get.replaceAll(regex2, "$1" + System.currentTimeMillis());
                System.err.println("http://pre.dp.jd.com" + updatedUrl);

                Matcher matcher = pattern.matcher(line);
                if(matcher.find()) {
                    String appId = matcher.group(1);
                    String token = matcher.group(2);
//                    System.out.println("appId: " + appId + ", token: " + token);

                    Set<String> set = map.get(appId);

                    if(set == null) {
                        set = new HashSet<>();
                        map.put(appId, set);
                    }
                    set.add(token);
                }
            }

            System.err.println(map);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}