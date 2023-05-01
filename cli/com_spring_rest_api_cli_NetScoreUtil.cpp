#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "com_spring_rest_api_cli_NetScoreUtil.h"
#include "NetScoreMetric.h"
#include "libpackageanalyze.h"

JNIEXPORT jobject JNICALL Java_com_spring_1rest_1api_cli_NetScoreUtil_CalculateNetScore(JNIEnv *env, jclass clazz, jstring url) {
    GoString gs;
    NetScoreMetric cMetric;

    // instantiate the gostring by converting the jstring to const char*
    gs.p = env->GetStringUTFChars(url, 0);
    gs.n = strlen(gs.p);
    
    // call the go code
    bool success = CalculateNetScoreMetric(gs, &cMetric);

    // release the converted jstring
    env->ReleaseStringUTFChars(url, gs.p);

    if (!success) {
    #if DEBUG == 1
        printf("unsuccessful\n");
    #endif
        return NULL;
    }

    // instantiate and return the jobject if go was successful
    jclass jclazz = env->FindClass("com/spring_rest_api/cli/NetScoreMetric");
    if (!jclazz) {
    #if DEBUG == 1
        printf("couldn't find jclass\n");
    #endif
        return NULL;
    }

    jmethodID mid = env->GetMethodID(jclazz, "<init>", "(FFFFFF)V");
    if (!mid) {
    #if DEBUG == 1
        printf("couldn't find constructor\n");
    #endif
        return NULL;
    }

    return env->NewObject(clazz, mid, cMetric.License, cMetric.Correctness, cMetric.BusFactor,
        cMetric.RampUp, cMetric.ResponsiveMaintainer, cMetric.NetScore);
}