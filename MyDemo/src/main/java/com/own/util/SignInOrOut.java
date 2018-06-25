package com.own.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignInOrOut {
   /**
    * [ {"wxCode":"","signPositionName":"集团新厂","createTime":"","accountID":"",
    * "phone":"","updateTime":"",
    * "userCode":"","corporationID":"","pid":1,"organizationId":826,"distance":
    * 500,"email":"","userName":"", "longitude":116.3066,"latitude":39.617},
    * 
    * {"wxCode":"","signPositionName":"九办","createTime":"","accountID":
    * "","phone":"","updateTime":"","userCode":"","corporationID":"","pid":2,
    * "organizationId":826,"distance":500,
    * "email":"","userName":"","longitude":116.4553,"latitude":39.98302},
    * 
    * {"wxCode":"","signPositionName":"北总店",
    * "createTime":"","accountID":"","phone":"","updateTime":"","userCode":"",
    * "corporationID":"","pid":3,
    * "organizationId":826,"distance":500,"email":"","userName":"","longitude":
    * 116.437035,"latitude":39.992752}]
    */

   private static final String url = "http://weixin.boloni.com.cn:8080/XXQD/saveAddressSign.action";
   private static Map<String, Object> infos = new HashMap<String, Object>();

   //少林：022623
   private static void initSignInInfo() {
      infos.put("userCode", "022559");
      infos.put("longitude", 116.4553);
      infos.put("latitude", 39.98301);
      infos.put("signInPositionID", 2);
      infos.put("issx", "上班");
   }

   private static void initSignOutInfo() {
      infos.put("userCode", "022559");
      infos.put("longitude", 116.4554);
      infos.put("latitude", 39.98301);
      infos.put("signInPositionID", 2);
      infos.put("issx", "下班");
   }

   public static String signIn() throws IOException {
      initSignInInfo();
      return HttpClientUtil.post(url, infos);
   }

   public static String signOut() throws IOException {
      initSignOutInfo();
      return HttpClientUtil.post(url, infos);
   }

   public static void main(String[] args) throws IOException {
      //上班签到
      String rs = signIn();
      //下班签到
//      String rs = signOut();
      System.err.println("结果：" + rs);
   }
}
