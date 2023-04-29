package com.spring_rest_api.cli;

public class NetScoreUtil {
    static { System.loadLibrary("NetScoreUtil"); }
    
    private static native NetScoreMetric CalculateNetScore(String url);
    
    public static NetScoreMetric GetNetScore(String url) {
        // Null check
        if (url == null) {
            return null;
        }

        // Call the C++ library
        NetScoreMetric nsm = CalculateNetScore(url);

        // Null check
        if (nsm == null) {
            return null;
        }
        
        // Something about instantiating this in C++ breaks the object a bit. This fixes that.
        return new NetScoreMetric(nsm.License, nsm.Correctness, nsm.BusFactor, nsm.RampUp, nsm.ResponsiveMaintainer, nsm.NetScore);
    }

    public static NetScoreMetric[] test() {
        // this github
        NetScoreMetric nsm1 = GetNetScore("https://github.com/Alethon/ECE461_Part2");
        // a string that should fail
        NetScoreMetric nsm2 = GetNetScore("athihgagg");
        return new NetScoreMetric[] { nsm1, nsm2 };
    }
}