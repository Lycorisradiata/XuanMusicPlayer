package com.example;


import java.util.Arrays;
import java.util.List;

import sun.rmi.runtime.Log;

public class MyClass {
    public static void main(String[] args){
        List<String> titles;
        String[] array = new String[]{"1", "2", "3"};
        titles = Arrays.asList(array);
        System.out.println("titles " + titles.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        titles.add("4");
        System.out.println("titles " + titles.size());
    }
}
