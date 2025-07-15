package com.jd.myutils.logbook;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogBookLogGetter {
    static Pattern urlPattern = Pattern.compile("GET\\s+(.*?)\\s+HTTP");

    public static void main(String[] args) throws InterruptedException {
        while(true) {
            getLogs();
            TimeUnit.MINUTES.sleep(1);
        }
    }

    public static void getLogs() {
        String url = "https://taishan.jd.com/api/tianweiApi/tianmeng_search/v1/log/grep";
        String body = "{\"systemName\":\"jbdp\",\"appName\":\"jbdp-urm\",\"keyword\":\"getSuperiorBaseInfo?appId=\",\"regex\":false,\"groups\":[],\"ips\":[],\"paths\":[],\"startTimestamp\":1750906274269,\"endTimestamp\":1750907174269,\"timeout\":30000,\"maxLines\":100,\"excludedWords\":[],\"erp\":\"liujianjia\",\"showPlain\":false}";
        Map<String, String> headers = new HashMap<>();
        headers.put("token", "token-origin");
        headers.put("x-proxy-opts", "{\"target\":\"http://tianwei-gateway.jd.local/\",\"pathRewrite\":{\"^/api/tianweiApi\":\"\"}}");


        JSONObject jsonObject = JSONObject.parseObject(body);
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetMinute(date, -1);
        System.err.println("start:" + dateTime);
        System.err.println("end:" + date);

        jsonObject.put("startTimestamp", dateTime.getTime());
        jsonObject.put("endTimestamp", date.getTime());
        String jsonString = jsonObject.toJSONString();
        headers.put("Content-Length", jsonString.length() + "");
//        System.err.println(jsonString);
        HttpResponse execute = HttpUtil.createPost(url).body(jsonString).addHeaders(headers).execute();
//        System.err.println(execute.body());

        JSONObject res = JSONObject.parseObject(execute.body());
        JSONArray jsonArray = res.getJSONObject("data").getJSONArray("results");

        for(int i = 0; i < jsonArray.size(); i++) {
            JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("logs");

            for(int j = 0; j < jsonArray1.size(); j++) {
                String content = jsonArray1.getJSONObject(j).getString("content");
                String findByErpUrl = parseUrl(content);
                String s = callUrl("http://pre.dp.jd.com" + findByErpUrl);
                Integer code = JSONObject.parseObject(s).getInteger("code");

                if(code != 0) {
                    if(!s.contains("Missing value")) {
                        System.err.println("http://pre.dp.jd.com" + findByErpUrl + "\n" + s);
                    }
                }
            }
        }

    }

    private static String parseUrl(String log) {
        Matcher matcher = urlPattern.matcher(log);

        if(matcher.find()) {
            String fullPath = matcher.group(1);
            return fullPath;
        }
        return null;
    }

    private static String callUrl(String url) {
        String s = HttpUtil.get(url);
        return s;
    }
}
