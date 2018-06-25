package com.demo.httplogin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.own.util.FileUtils;

/**
 *
 * @ClassName: HttpLogin
 * @Description: java通过httpclient获取cookie模拟登录
 */

public class HttpLogin2 {
    private static String loginUrl = "https://ssa.jd.com/sso/login?ReturnUrl=http://source.jd.com/web/";
    private static HttpClient httpClient = new HttpClient();
    private static Set<String> paths = new HashSet<String>();
    private static String domain = "http://source.jd.com";
    private static String codeUrl = "http://source.jd.com/app/jd-sso.git";
    private static String branch = "master";
    private static String dwnCodePath = "E:\\dwncode";
    private static String ENCODING = "UTF-8";

    public static String login(String user, String pwd) {
        PostMethod postMethod = new PostMethod(loginUrl);
        NameValuePair[] data = { new NameValuePair("username", user), new NameValuePair("password", pwd) };
        postMethod.setRequestBody(data);

        try {
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(postMethod);
            Cookie[] cookies = httpClient.getState().getCookies();
            StringBuffer tmpcookies = new StringBuffer();

            for(Cookie c : cookies) {
                tmpcookies.append(c.toString() + ";");
            }

            if(statusCode == 302) {// 重定向到新的URL
                return tmpcookies.toString();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void downloadCode(String cookie, String url) throws HttpException, IOException {
        String nurl = url + "?encoding=" + ENCODING;
        GetMethod getMethod = new GetMethod(nurl);
        getMethod.setRequestHeader("cookie", cookie);
        getMethod.setRequestHeader("Referer", "");
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        httpClient.executeMethod(getMethod);
        String text = getMethod.getResponseBodyAsString();
        Document doc = Jsoup.parse(text);
        Elements rows = doc.select("#fileContentContainer");
        System.err.println("===============================================" + rows.size());

        for(int i = 0; i < rows.size(); i++) {
            String content = rows.get(i).wholeText();
            String npath = url.replace(domain, "");
            npath = npath.replace("blob/", "").replace("app/", "");
            npath = npath.replace("end-delimiter/", "");
            String filepath = dwnCodePath + File.separator + npath;
            File file = new File(filepath);

            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileUtils.writeData(filepath, content, ENCODING);
        }
    }

    public static void main(String[] args) throws HttpException, IOException {
        String cookie = login("liujianjia", "Liu201803@@");
        downloadCode(cookie, "http://source.jd.com/app/jd-sso/blob/master/end-delimiter/SSO%2DWEB/src/main/java/com/jd/sso/util/Utils.java");
    }
}