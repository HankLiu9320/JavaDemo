package com.hank.demo;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeDemo {
    private static Unsafe unsafe;
    public int a = 0;
    public long b = 0;
    public static String c = "123";
    public static Object d = null;
    public static int e = 100;

    public static void main(String[] args) throws NoSuchFieldException, SecurityException {
        long aOffset = unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("a"));
        long bOffset = unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("b"));

        // 数组第一个元素的偏移地址,即数组头占用的字节数
        int[] intarr = new int[0];
        System.err.println(unsafe.arrayBaseOffset(intarr.getClass()));
        // 数组中每个元素占用的大小
        System.err.println(unsafe.arrayIndexScale(intarr.getClass()));

        // 获取类的静态字段偏地址
        System.err.println(unsafe.staticFieldOffset(UnsafeDemo.class.getDeclaredField("c")));
        System.err.println(unsafe.staticFieldOffset(UnsafeDemo.class.getDeclaredField("d")));

        // 获取静态字段的起始地址,通过起始地址和偏移地址,就可以获取静态字段的值了
        // 只不过静态字段的起始地址,类型不是long,而是Object类型
        Object base1 = unsafe.staticFieldBase(UnsafeDemo.class);
        Object base2 = unsafe.staticFieldBase(UnsafeDemo.class.getDeclaredField("d"));
        System.err.println(base1 == base2);// true

        // Report the size in bytes of a native pointer.
        // 返回4或8,代表是32位还是64位操作系统。
        System.err.println(unsafe.addressSize());
        // 返回32或64,获取操作系统是32位还是64位
        System.err.println(System.getProperty("sun.arch.data.model"));

        
      //获取实例字段的属性值  
        UnsafeDemo vo = new UnsafeDemo();  
        vo.a = 10000;  
        long aoffset = unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("a"));  
        int va = unsafe.getInt(vo, aoffset);
        System.out.println("va="+va);  

        
        unsafe.park(true, 1);
        unsafe.compareAndSwapInt(vo, aoffset, 10000, 10001);
        System.err.println("++++++++++:" + vo.a);
        System.err.println("a offset:" + aOffset);
        System.err.println("b offset:" + bOffset);
    }

    static {
        // 通过反射获取rt.jar下的Unsafe类
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            // return (Unsafe) theUnsafeInstance.get(null);是等价的
            unsafe = (Unsafe) theUnsafeInstance.get(Unsafe.class);
        }
        catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}