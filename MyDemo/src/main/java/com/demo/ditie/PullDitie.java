package com.demo.ditie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.own.util.HttpClientUtil;

public class PullDitie {
    private static final String url = "http://restapi.amap.com/v3/place/text?key=a4941c90006419a7a5b3af3278f7859b&keywords=#SUBWAY&types=&city=010&children=1&offset=20&page=1&extensions=all";

    public static void main(String[] args) throws IOException {
        String file = "D:\\data\\地铁站数据拉取\\补充地铁站.txt";
        String baseDir = "D:\\data\\地铁站数据拉取\\地铁json";
        InputStreamReader read = new InputStreamReader(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        Set<String> set = new HashSet<String>();

        while((lineTxt = bufferedReader.readLine()) != null) {
            set.add(lineTxt.trim());
        }

        bufferedReader.close();
        System.err.println(set.size());
        

        for(String s : set) {
            String name = s + "地铁站";
            FileWriter fw = new FileWriter(baseDir + File.separator + name + ".txt");
            String nurl = url.replace("#SUBWAY", name);
            String rs = HttpClientUtil.get(nurl, 0);
            JSONObject data = JSON.parseObject(rs);
            JSONArray pois = data.getJSONArray("pois");
            JSONObject ditie = pois.getJSONObject(0);
            
String n = ditie.getString("name");
String loc = ditie.getString("location");
String typecode = ditie.getString("typecode");
String adcode = ditie.getString("adcode");

StringBuilder sb = new StringBuilder("INSERT INTO public.geo_station(gid, \"name\", name_py, \"type\", ad_code, status, name_abbr, geom)VALUES(nextval('station_gid_seq'::regclass),");
sb.append("'" + n + "',");
sb.append("'',");
sb.append("'" + typecode + "',");
sb.append("'" + adcode + "',");
sb.append(0 + ",");
sb.append("'',");
sb.append("st_geomfromtext('POINT(" + loc.replace(",", " ") + ")'));");

            System.err.println(sb.toString());
            fw.write(rs);
            fw.close();
        }
    }
}
