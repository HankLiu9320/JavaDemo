package com.own.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient工具类
 * 
 * @return
 * @author SHANHY
 * @create 2015年12月18日
 */
public class HttpClientUtil {
   static final int timeOut = 10 * 1000;
   private static CloseableHttpClient httpClient = null;
   private final static Object syncLock = new Object();

   private static void config(HttpRequestBase httpRequestBase) {
      // 设置Header等
      httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
      httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
      httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate");
      httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
      httpRequestBase.setHeader("Cookie", "uuid=3fe6def73c25c9724a32.1501730472.0.0.0; oc=e_RwrhEGqvaPMtvLRIjOk9g321RLDn0KUU8Emcm4vR3gEDwna0kTumJ_3TrHBjmGL4iuu8QqL0GznOhM66ImgyHQYMVVw2PuiYLTd4Lde-4twY93WYLubvDw76McFn9RXCXsZtpNrM-nh7R7evPyjJUDe-t-JUa3ubIIgC8Ww2Y; ci=1; abt=1501730472.0%7CBDF; _lxsdk_cuid=15da61c8805c8-0ca673859edce3-41554130-144000-15da61c8806c8; _lxsdk_s=15da61c8807-c92-068-ba1%7C%7C9; __mta=155155079.1501730474307.1501732243707.1501732574528.9; __utma=211559370.1647339134.1501730474.1501730474.1501730474.1; __utmb=211559370.9.10.1501730474; __utmz=211559370.1501730474.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=211559370.|1=city=bj=1^5=cate=all=1; __utmc=211559370; em=bnVsbA; om=bnVsbA");
      httpRequestBase.setHeader("Connection", "keep-alive");
      httpRequestBase.setHeader("Accept-Charset", "utf-8");

      // 配置请求的超时设置
      RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut).setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
      httpRequestBase.setConfig(requestConfig);

   }

   /**
    * 获取HttpClient对象
    * 
    * @return
    * @author SHANHY
    * @create 2015年12月18日
    */
   public static CloseableHttpClient getHttpClient(String url) {
      String hostname = url.split("/")[2];
      int port = 80;
      if(hostname.contains(":")) {
         String[] arr = hostname.split(":");
         hostname = arr[0];
         port = Integer.parseInt(arr[1]);
      }

      if(httpClient == null) {
         synchronized(syncLock) {
            if(httpClient == null) {
               httpClient = createHttpClient(400, 80, 150, hostname, port);
            }
         }
      }

      return httpClient;
   }

   /**
    * 创建HttpClient对象
    * 
    * @return
    * @author SHANHY
    * @create 2015年12月18日
    */
   public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname, int port) {
      ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
      LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
      Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf).build();
      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
      // 将最大连接数增加
      cm.setMaxTotal(maxTotal);
      // 将每个路由基础的连接增加
      cm.setDefaultMaxPerRoute(maxPerRoute);
      HttpHost httpHost = new HttpHost(hostname, port);
      // 将目标主机的最大连接数增加
      cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

      // 请求重试处理
      HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
         public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if(executionCount >= 5) {// 如果已经重试了5次，就放弃
               return false;
            }
            if(exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
               return true;
            }
            if(exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
               return false;
            }
            if(exception instanceof InterruptedIOException) {// 超时
               return false;
            }
            if(exception instanceof UnknownHostException) {// 目标服务器不可达
               return false;
            }
            if(exception instanceof ConnectTimeoutException) {// 连接被拒绝
               return false;
            }
            if(exception instanceof SSLException) {// SSL握手异常
               return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if(!(request instanceof HttpEntityEnclosingRequest)) {
               return true;
            }
            return false;
         }
      };

      CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();

      return httpClient;
   }

   private static void setPostParams(HttpPost httpost, Map<String, Object> params) {
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      Set<String> keySet = params.keySet();
      for(String key : keySet) {
         nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
      }
      try {
         httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
      }
      catch(UnsupportedEncodingException e) {
         e.printStackTrace();
      }
   }

   /**
    * GET请求URL获取内容
    * 
    * @param url
    * @return
    * @author SHANHY
    * @throws IOException
    * @create 2015年12月18日
    */
   public static String post(String url, Map<String, Object> params) throws IOException {
      HttpPost httppost = new HttpPost(url);
      config(httppost);
      setPostParams(httppost, params);
      CloseableHttpResponse response = null;
      try {
         response = getHttpClient(url).execute(httppost, HttpClientContext.create());
         HttpEntity entity = response.getEntity();
         String result = EntityUtils.toString(entity, "utf-8");
         EntityUtils.consume(entity);
         return result;
      }
      catch(Exception e) {
         // e.printStackTrace();
         throw e;
      }
      finally {
         try {
            if(response != null)
               response.close();
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }


   /**
    * GET请求URL获取内容
    * 
    * @param url
    * @return
    * @author SHANHY
    * @create 2015年12月18日
    */
   public static String get(String url, int errCallTimes) {
      HttpGet httpget = new HttpGet(url);
      config(httpget);
      CloseableHttpResponse response = null;

      try {
         response = getHttpClient(url).execute(httpget, HttpClientContext.create());

         if(response.getStatusLine().getStatusCode() != 200) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.getEntity().getContent().close();
            return null;
         }

         HttpEntity entity = response.getEntity();
         String result = EntityUtils.toString(entity, "utf-8");
         EntityUtils.consume(entity);
         return result;
      }
      catch(IOException e) {
         errCallTimes--;

         if(errCallTimes > 0) {
            get(url, errCallTimes);
         }
         else {
            // e.printStackTrace();
            System.err.println("error:" + e.getMessage() + "|" + url);
         }
      }
      finally {
         try {
            if(response != null) {
               response.close();
            }
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }

      return null;
   }

   public static void main(String[] args) {
      // URL列表数组
      String[] urisToGet = { "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497",

            "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497",

            "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497",

            "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497",

            "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497",

            "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497", "http://blog.csdn.net/catoop/article/details/38849497" };

      long start = System.currentTimeMillis();
      try {
         int pagecount = urisToGet.length;
         ExecutorService executors = Executors.newFixedThreadPool(pagecount);
         CountDownLatch countDownLatch = new CountDownLatch(pagecount);
         for(int i = 0; i < pagecount; i++) {
            HttpGet httpget = new HttpGet(urisToGet[i]);
            config(httpget);
            // 启动线程抓取
            executors.execute(new GetRunnable(urisToGet[i], countDownLatch));
         }
         countDownLatch.await();
         executors.shutdown();
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }
      finally {
         System.out.println("线程" + Thread.currentThread().getName() + "," + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
      }

      long end = System.currentTimeMillis();
      System.out.println("consume -> " + (end - start));
   }

   static class GetRunnable implements Runnable {
      private CountDownLatch countDownLatch;
      private String url;

      public GetRunnable(String url, CountDownLatch countDownLatch) {
         this.url = url;
         this.countDownLatch = countDownLatch;
      }

      @Override
      public void run() {
         try {
            System.out.println(HttpClientUtil.get(url, 1));
         }
         finally {
            countDownLatch.countDown();
         }
      }
   }
}