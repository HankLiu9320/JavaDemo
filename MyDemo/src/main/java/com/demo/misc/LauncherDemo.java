package com.demo.misc;

import java.net.URL;

public class LauncherDemo {
    public static void main(String[] args) {
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();

        for(int i = 0; i < urls.length; i++) {
            System.out.println(urls[i].toExternalForm());
        }

        System.out.println(System.getProperty("sun.boot.class.path"));
        System.err.println(LauncherDemo.class.getClassLoader());
    }
}
