package com.hank.demo;

//被final修饰的对象，是引用不可变，对象的内容是可以改变
//fianl赋值声明的时候可以不用赋值，但是在构造方法中必须每一次都赋值
public class finalTest {
    public static void main(String[] args) {
        final StringBuffer sb = new StringBuffer("ss");
        sb.append("2");// 改变对象的内容,对
        //sb = new StringBuffer();// 编译未通过，报错：The final local variable sb cannot
                                // be assigned.
                                // It must be blank and not using a compound
                                // assignment

    }
}

class finalDemo {
    public final int a;

    public finalDemo() {
        a = 10;
    }
}