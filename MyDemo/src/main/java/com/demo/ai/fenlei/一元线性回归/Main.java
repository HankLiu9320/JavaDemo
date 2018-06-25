package com.demo.ai.fenlei.一元线性回归;

import java.io.IOException;
import java.net.URLDecoder;

public class Main {
    public static void main(String args[]) throws IOException {
        String fileName = "test.txt";
        String path = Main.class.getResource("").getPath() + fileName;
        path = URLDecoder.decode(path,"UTF-8");
        System.err.println(path);
        MyLinearRegression linearRegression = new MyLinearRegression(path);
        linearRegression.getAB();
        linearRegression.getR2();
        System.out.println("alpha = " + linearRegression.getAlpha());
        System.out.println("beta = " + linearRegression.getBeta());
        System.out.println("R2 = " + linearRegression.getR());
    }
}