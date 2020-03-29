package com.heroslender.herovender.helpers.menu;

public class Test {

    public static void main(String[] args) {

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 54 - 9) {
                System.out.println(i);
            } else if (i % 9 == 0) {
                System.out.println(i);
                System.out.println(i + 8);
            }
        }
    }
}
