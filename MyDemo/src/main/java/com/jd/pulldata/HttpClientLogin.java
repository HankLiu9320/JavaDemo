package com.jd.pulldata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.util.StringUtils;

public class HttpClientLogin {
    private static final String loginUrl = "http://ssa.jd.com/sso/login?ReturnUrl=http%3A%2F%2Fdbquery.jd.com%2Fhome";
    private static final String dataUrl = "http://dbquery.jd.com/home/ajaxQueryData";
    private static final String fileds = "http://dbquery.jd.com/home/ajaxQuery";

    /**
     * 登录
     * 
     * @param username
     * @param pwd
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static String login(String username, String pwd) throws HttpException, IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(loginUrl);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        NameValuePair[] data = { new NameValuePair("username", username), new NameValuePair("password", pwd) };
        postMethod.setRequestBody(data);
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        int s = httpClient.executeMethod(postMethod);

        String rs = postMethod.getResponseBodyAsString();
        System.err.println(rs);
        Cookie[] cookies = httpClient.getState().getCookies();
        StringBuilder tmpcookies = new StringBuilder();

        for(Cookie c : cookies) {
            tmpcookies.append(c.toString() + ";");
        }

        return tmpcookies.toString();
    }

    /**
     * 拿列
     * 
     * @param sql
     * @param cookies
     * @param params
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static List<Field> getFileds(String cookies, Map<String, String> params) throws HttpException, IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(fileds);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        postMethod.setRequestHeader("Referer", "http://dbquery.jd.com/home");
        postMethod.setRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
        postMethod.setRequestHeader("cookie", cookies);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        if(params != null) {
            for(Entry<String, String> entry : params.entrySet()) {
                nvps.add(new NameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        postMethod.setRequestBody(nvps.toArray(new NameValuePair[0]));
        httpClient.executeMethod(postMethod);
        String rs = postMethod.getResponseBodyAsString();
        System.err.println(rs);
        GetFieldData f = JSON.parseObject(rs, GetFieldData.class);
        return f.getKeys();
    }

    /**
     * 拿数据
     * 
     * @param sql
     * @param cookies
     * @param params
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static GetData getData(String cookies, Map<String, String> params) throws HttpException, IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(dataUrl);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        postMethod.setRequestHeader("Referer", "http://dbquery.jd.com/home");
        postMethod.setRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
        postMethod.setRequestHeader("cookie", cookies);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        if(params != null) {
            for(Entry<String, String> entry : params.entrySet()) {
                nvps.add(new NameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        postMethod.setRequestBody(nvps.toArray(new NameValuePair[0]));
        httpClient.executeMethod(postMethod);
        String rs = postMethod.getResponseBodyAsString();
        System.err.println(rs);
        try {
            GetData data = JSON.parseObject(rs, GetData.class);
            return data;
        }
        catch(Exception e) {
            System.err.println("出错重试！" + e.getMessage());
            return getData(cookies, params);
        }
    }

    public static void getTableData(ParamsData pd) throws HttpException, IOException {
        String sqlFile = pd.getSqlFile();

        if(sqlFile != null) {
            StringBuilder sql = new StringBuilder();
            File file = new File(sqlFile);
            InputStreamReader read = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            while((lineTxt = bufferedReader.readLine()) != null) {
                sql.append(lineTxt);
                sql.append("\n");
            }

            bufferedReader.close();
            read.close();

            pd.getParam().put("sql", sql.toString());
        }

        String sql = pd.getParam().get("sql");
        String start = pd.getStart();
        String end = pd.getEnd();
        //修正sql
        String nsql = sql.replace("${START}", start != null ? "'" + start + "'": "''");
        nsql = nsql.replace("${END}", end != null ? "'" + end + "'" : "''");
        pd.getParam().put("sql", nsql);

        String cookies = login(pd.getUserName(), pd.getPassword());
        List<Field> fs = getFileds(cookies, pd.getParam());
        FileWriter fw = new FileWriter(pd.getOutFilePath());
        List<String> fsStr = new ArrayList<String>();

        if(fs != null) {
            for(int i = 0; i < fs.size(); i++) {
                fw.write(fs.get(i).getField() + "|");
                fsStr.add(fs.get(i).getField());

                if(i != fsStr.size() - 1) {
                    fw.write("|");
                }
            }

            fw.write("\n");
        }

        while(true) {
            GetData rs = getData(cookies, pd.getParam());
            List<Map<String, String>> rows = rs.getRows();

            if(rows == null || rows.size() == 0) {
                break;
            }

            for(Map<String, String> row : rows) {
                for(int i = 0; i < fsStr.size(); i++) {
                    String val = row.get(fsStr.get(i));
                    val = val != null ? val : "";
                    fw.write(val);
                    
                    if(i != fsStr.size() - 1) {
                        fw.write("|");
                    }
                }
                
                fw.write("\n");
            }
            
            String orderCol = pd.getOrderField();
            
            if(orderCol != null) {
                Map<String, String> lrow = rows.get(rows.size() - 1);
                String orderVal = lrow.get(orderCol);

                //修正sql
                String subsql = sql.replace("${START}", "'" + orderVal + "'");
                subsql = subsql.replace("${END}", end != null ? "'" + end + "'" : "''");
                pd.getParam().put("sql", subsql);
            }
            
            fw.flush();
        }

        fw.close();
    }

    public static void main(String[] args) throws Exception {
        ParamsData pd = new ParamsData();
        pd.setUserName("liujianjia");
        pd.setPassword("Liu033614@@");
        pd.setOutFilePath("E:\\data.csv");
        pd.setSqlFile("E:\\sql.txt");
        pd.setOrderField("PIN");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "25467");
        params.put("sql", "select * from geo_poi where id > 100");
        pd.setParam(params);
        getTableData(pd);
    }
}
