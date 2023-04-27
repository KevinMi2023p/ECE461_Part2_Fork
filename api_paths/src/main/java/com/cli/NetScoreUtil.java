package com.cli;

public class NetScoreUtil {
    static { System.loadLibrary("NetScoreUtil"); }
    
    public static native NetScoreMetric CalculateNetScore(String url);
    
    public static void main(String[] args) {
        String github = "https://github.com/Alethon/ECE461_Part2";
        NetScoreMetric nsm = CalculateNetScore(github);
        System.out.println(nsm == null);
        nsm = CalculateNetScore("athihgagg");
        System.out.println(nsm == null);
    }
}