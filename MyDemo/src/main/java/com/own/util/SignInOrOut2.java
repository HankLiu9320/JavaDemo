package com.own.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SignInOrOut2 {
   //http://weixin.seeyon.com/map/mapRedirect?openid=022623&accountid=8a25f5da510fba390151140dfa462520
    //choose("116.455288","39.983202","北京市朝阳区房美汇海外房产")
    
   private static final String url = "http://weixin.seeyon.com/map/savelbsinfo";
   private static Map<String, Object> infos = new HashMap<String, Object>();

   //少林：022623
   private static void initSignInInfo() {
       infos.put("formattedAddress", "北京市朝阳区房美汇海外房产");
       infos.put("openid", "022559");
       infos.put("locationX", 116.455288);
       infos.put("locationY", 39.983202);
       infos.put("province", "北京市");
       infos.put("city", "");
       infos.put("town", "望京街道");
       infos.put("street", "北四环东路");
   }

   public static String sign() throws IOException {
      initSignInInfo();
      return HttpClientUtil.post(url, infos);
   }
   
   public static String sing2() throws UnsupportedEncodingException {
      initSignInInfo();
      String nurl = "";
      int i = 0;

      for(Map.Entry<String, Object> entry : infos.entrySet()) {
         if(i == 0) {
            nurl += "?" + entry.getKey() + "=" + entry.getValue();
         }
         else {
            nurl += "&" + entry.getKey() + "=" + entry.getValue();
         }
      }

      String lastUrl = url + URLEncoder.encode(nurl, "UTF-8");
      System.err.println(lastUrl);
      return HttpClientUtil.get(lastUrl, 0);
   }

   public static void main(String[] args) throws IOException {
      String rs = sign();
      System.err.println("结果：" + rs);
   }
}
