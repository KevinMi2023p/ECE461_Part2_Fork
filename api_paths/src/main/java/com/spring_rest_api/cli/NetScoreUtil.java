package com.spring_rest_api.cli;

public class NetScoreUtil {
    static { System.loadLibrary("NetScoreUtil"); }
    
    public static native NetScoreMetric CalculateNetScore(String url);
    
    public static NetScoreMetric[] test() {
        // this github
        NetScoreMetric nsm1 = CalculateNetScore("https://github.com/Alethon/ECE461_Part2");
        // a string that should fail
        NetScoreMetric nsm2 = CalculateNetScore("athihgagg");
        return new NetScoreMetric[] { nsm1, nsm2 };
    }
}