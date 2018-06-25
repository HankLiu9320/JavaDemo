package com.hank.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test extends HashMap {
    class Test2 extends HashMap {
        Test2() {

        }

        {
            put("1", "2");
            put("1", "2");
        }
    }

    public static void main(String[] args) {
        Test t = new Test() {
            {
                put("1", "1");
                put("2", "2");
            }
        };

        System.err.println(t);

        Test2 t2 = new Test().new Test2();
        System.err.println(t2);

        List<String> names = new ArrayList<String>() {
            {
                for(int i = 0; i < 10; i++) {
                    add("A" + i);
                }
            }
        };

        System.err.println(names);
    }
}
