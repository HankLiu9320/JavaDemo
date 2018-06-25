package com.hank.demo;

public class ExceptionDemo {
    public void test() {
        try {
            //
            System.err.println(1);
        }
        catch(Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        ExceptionDemo demo = new ExceptionDemo();
        demo.test();
    }
}
