package com.hank.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConvertTextDemo {
    public static void main(String[] args) throws IOException {
        BufferedImage bi = (BufferedImage) ImageIO.read(new File("e.png"));

        // 获取图像的宽度和高度
        int width = bi.getWidth();
        int height = bi.getHeight();
System.err.println(width + "," + height);
        // 扫描图片
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {// 行扫描
                int dip = bi.getRGB(j, i);
                if(dip == -1)
                    System.out.print(" ");
                else
                    System.out.print("+");
            }
            System.out.println();// 换行
        }

    }

}