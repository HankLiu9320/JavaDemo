package com.jd.myutils.log.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DownloadIdeLog {
    public static void main(String[] args) {
        downloadIdeLog();
    }

    public static void downloadIdeLog() {
        String baseUrl = "http://pre.dp.jd.com/dp/scriptcenter/script/logDownload.ajax?runDetailId=";
        String saveDir = "/Users/liujianjia/3_data/ide_logs/";
        String cookieValue = "shshshfpa=6a185f56-a3ee-f916-5fca-74587b05087d-1734675170; shshshfpx=6a185f56-a3ee-f916-5fca-74587b05087d-1734675170; __jdu=1732179973149155115537; __jdv=230157721|direct|-|none|-|1752117059410; sso.jd.com=BJ.F2777955E4E2738B9560D385C3630665.2420250714110201; 3AB9D23F7A4B3CSS=jdd03IRXQYXF4ILB34LK46GXPYHSFAIEAO6HCTMNGQ5HF3YDNZKYZHNNESTQIBWUAYVLCFUA272UJDVU23JHY2UH52YTOWQAAAAMYBEZTOAYAAAAAD6YO4XNARM7SO4X; areaId=1; ipLoc-djd=1-72-0-0; shshshfpb=BApXSJRA7Cv1A7vFGUakXou0DHS_Aw7mnBnYmVl919xJ1Ml4xeIG2; 3AB9D23F7A4B3C9B=IRXQYXF4ILB34LK46GXPYHSFAIEAO6HCTMNGQ5HF3YDNZKYZHNNESTQIBWUAYVLCFUA272UJDVU23JHY2UH52YTOWQ; focus-token-type=7; focus-login-switch=esuite; jdd69fo72b8lfeoe=PWZ4U75YQCUXA4IRCIZVR2Q5C75AADNGDIC5KX4QA7FCRFI5LWW4BYFM6BMVOW3ESYJMUO25PGUAZYNMVSS5NXHKCI; token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImI4Y0kwX25NSkctR0UzZkU3ME1qIiwiZXhwIjoxNzUzMTUzMzc1LCJpYXQiOjE3NTI1NDg1NzV9.5Mpj1ODu6bgysLVjkhCkva_1YIhpSyezer_ixFeSrZo; focus-team-id=00046419; me_token=undefined; erp=liujianjia; __jda=26439548.1732179973149155115537.1732179973.1736833848.1752568532.223; __jdc=26439548; __jdb=26439548.2.1732179973149155115537|223.1752568532; SSAID=b9e39fd63893ebada0d625a579435243a60921df93d6846b46fa316395882dfc0149889159cbebded129eb8a2a639481f99f5fff5b58f1da145a1bc722c906ec7968a3110dee88e397f3ffa703f15bb91c86b586989269a049048522c345ca2b45a8a08142b76083cd2952842f7192b02c2c02fd6c02098c69b1957b5fb93df97d17991d7ee9e1aac127451ec021f09f4c88444492558f3839849f176da5cc54bac822793a245b2d0bc282cde7c5e10c298be29576ffc07761c366140631600e; ssa.bdp.state=CMsYj3skoqcSjOk2GQdJBnzoSSajk_Dg28t3PuCnxW4; ssa.bdp=d1547d577d72e5a445a29dc8f72552e8e714f29f1048a9ce69234b3c5a96d4599ea81b6150b9113ab5e2679c4483be26fb5284d3e18e9a63cbfee3888a00ae083af068af805baef1a26ed7963236bdba1b2b71758c19f931c1c6948337da10865fce21ce532625b5990d7b1002db4c2a5c18184baa72a0ec20fe17f37e84a230; ssa.global.ticket=F6BE6B90A23FF38497E6B8D095895A57";
        List<String> ids = readIdsFromFile("ide-ids.txt");

        int cnt = 0;
        for(String id : ids) {
            cnt++;
            String url = baseUrl + id;
            String savePath = saveDir + id + ".log";

            try {
                downloadFileWithCookie(url, savePath, cookieValue);
                System.out.println("文件下载成功！保存路径: " + savePath + ", 当前第:" + cnt + ", 总计：" + ids.size());
            }
            catch(Exception e) {
                System.err.println("下载失败: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public static void downloadFileWithCookie(String fileUrl, String savePath, String cookie) throws IOException {

        // 1. 创建URL对象
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // 2. 设置请求属性和Cookie[5,11](@ref)
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", cookie); // 设置Cookie头
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // 模拟浏览器
            conn.setConnectTimeout(15000); // 15秒连接超时
            conn.setReadTimeout(30000);    // 30秒读取超时

            // 3. 检查响应状态
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("服务器返回错误状态: " + responseCode);
            }

            // 4. 创建本地文件并写入数据[9](@ref)
            Path outputPath = Paths.get(savePath);
            try(InputStream inputStream = conn.getInputStream();
                OutputStream outputStream = Files.newOutputStream(outputPath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
        finally {
            conn.disconnect(); // 确保连接关闭
        }
    }

    public static List<String> readIdsFromFile(String filePath) {
        List<String> ids = new ArrayList<>();

        // 从classpath根目录读取文件（无需"/"开头）
        InputStream inputStream = DownloadIdeLog.class.getClassLoader().getResourceAsStream(filePath);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = reader.readLine()) != null) {
                ids.add(line);
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }

        return ids;
    }

}