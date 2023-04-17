#ifndef __NET_SCORE_METRIC_
#define __NET_SCORE_METRIC_

struct sNetScoreMetric {
    float License;
    float Correctness;
    float BusFactor;
    float RampUp;
    float ResponsiveMaintainer;
    float NetScore;
};

typedef struct sNetScoreMetric NetScoreMetric;

#endif