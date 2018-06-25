package com.own.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileUtils {
    public static Map<String, Integer> readUniqueLine(String path) {
        Map<String, Integer> keyCount = new HashMap<String, Integer>(1000240);

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String data = null;

            while((data = bufferedReader.readLine()) != null) {
                if(data != null && data.length() > 0) {
                    String[] ss = data.split("\t");
                    Integer c = keyCount.get(ss[0]);
                    keyCount.put(ss[0], (c == null ? 0 : c) + Integer.parseInt(ss[2]));
                }
            }

            bufferedReader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return keyCount;
    }

    public static List<String> readAllLine(String path) {
        List<String> datalines = new ArrayList<String>(1024);

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String data = null;

            while((data = bufferedReader.readLine()) != null) {
                datalines.add(data);
            }

            bufferedReader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return datalines;
    }

    public static List<String> readAllLine(String path, String code) {
        List<String> datalines = new ArrayList<String>(1024);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), code);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String data = null;
            while((data = bufferedReader.readLine()) != null) {
                datalines.add(data);
            }
            bufferedReader.close();
        }
        catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return datalines;
    }

    public static void writeData(String path, String data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), "utf-8");
            writer.write(data);
            writer.close();
        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    public static void writeData(String path, String data, String encoding) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), encoding);
            writer.write(data);
            writer.close();
        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    public static void writeData(String path, Map<String, Integer> map) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), "utf-8");
            int i = 0;

            for(Entry<String, Integer> entry : map.entrySet()) {
                writer.write(entry.getKey() + "\ttag\t" + entry.getValue() + "\n");
                i++;

                if(i % 1000 == 0) {
                    writer.flush();
                }
            }

            writer.close();
        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\data\\词库清洗\\error.log-1504769162\\export\\Logs\\taddress.jd.com";
        Map<String, Integer> map = readUniqueLine(filePath + "\\error.log");
        writeData(filePath + "\\result.txt", map);
    }
}
