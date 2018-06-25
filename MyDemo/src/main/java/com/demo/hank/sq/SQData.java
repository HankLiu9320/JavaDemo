package com.demo.hank.sq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import com.own.util.HttpClientUtil;

public class SQData {
   private static Map<String, String> map = new HashMap<String, String>();
   
   public static void init() {
      map.put("朝阳区", "http://bj.meituan.com/category/all/chaoyangqu/");
      map.put("海淀区", "http://bj.meituan.com/category/all/haidianqu/");
      map.put("丰台区", "http://bj.meituan.com/category/all/fengtaiqu/");
      map.put("西城区", "http://bj.meituan.com/category/all/xichengqu/");
      map.put("东城区", "http://bj.meituan.com/category/all/dongchengqu/");
      map.put("昌平区", "http://bj.meituan.com/category/all/changpingqu/");
      map.put("石景山区", "http://bj.meituan.com/category/all/shijingshanqu/");
      map.put("通州区", "http://bj.meituan.com/category/all/tongzhouqu/");
      map.put("大兴区", "http://bj.meituan.com/category/all/daxingqu/");
      map.put("顺义区", "http://bj.meituan.com/category/all/shunyi/");
      map.put("房山区", "http://bj.meituan.com/category/all/fangshan/");
      map.put("密云区", "http://bj.meituan.com/category/all/miyunxian/");
      map.put("怀柔区", "http://bj.meituan.com/category/all/huairou/");
      map.put("延庆区", "http://bj.meituan.com/category/all/yanqingxian/");
      map.put("门头沟", "http://bj.meituan.com/category/all/mentougou/");
      map.put("平谷区", "http://bj.meituan.com/category/all/pinggu/");
   }

   public static void main(String[] args) throws Exception {
      init();
      Set<String> sq1 = new HashSet<String>();
      Set<String> sq2 = new HashSet<String>();

      for(Entry<String, String> entry : map.entrySet()) {
         String key = entry.getKey();
         String url = entry.getValue();
         System.err.println("----------" + key + "---------------");
         String rs = HttpClientUtil.get(url, 0);
         File f = new File("d:\\" + key + ".txt");
         
         if(!f.exists()) {
            f.createNewFile();
         }

         FileOutputStream out = new FileOutputStream(f);
         out.write(rs.getBytes("UTF-8"));
         out.close();
         
         Parser titleParser = Parser.createParser(rs, "UTF-8");
         NodeFilter filter = new CssSelectorNodeFilter("ul[class='inline-block-list J-area-block']");
         NodeList nlist = titleParser.extractAllNodesThatMatch(filter);
         SimpleNodeIterator iterator = nlist.elements();

         while(iterator.hasMoreNodes()) {
            BulletList span = (BulletList)iterator.nextNode();
            NodeList clist = span.getChildren();
            SimpleNodeIterator ci = clist.elements();

            while(ci.hasMoreNodes()) {
               Node n = ci.nextNode();

               if(n instanceof Bullet) {
                  Bullet text = (Bullet) n;
                  int count = text.getChildCount();
                  
                  for(int i = 0; i < count; i++) {
                     Node child = text.getChild(i);
                     
                     if(child instanceof LinkTag) {
                        LinkTag tag = (LinkTag) child;
                        String sq = tag.getLinkText();
                        
                        if(!"全部".equals(sq.trim())) {
                           sq1.add(sq);
                        }
                     }
                  }
               }
            }
         }
      }

      String sql = "select name from public.tra_shang_quan group by name";
      List<String> list = PostSQLUtil.getFirstCol(sql);
      sq2.addAll(list);
      
      for(String s1 : sq1) {
         if(!sq2.contains(s1)) {
            System.err.println(s1);
         }
      }
   }
}
