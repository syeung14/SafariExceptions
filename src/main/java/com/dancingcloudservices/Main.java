package com.dancingcloudservices;

record Message(String msg) {}
public class Main {
    public static void main(String[] args) {
        Object obj = new Message("Hello Java 21 world!");

        System.out.println(switch(obj) {
            case Message(String s) -> s;
            default -> "What's that!?";
        });
    }
}