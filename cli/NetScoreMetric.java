package cli;

public class NetScoreMetric {
    public float License;
    public float Correctness;
    public float BusFactor;
    public float RampUp;
    public float ResponsiveMaintainer;
    public float NetScore;

    public NetScoreMetric() {
        this.License                = 0;
        this.Correctness            = 0;
        this.BusFactor              = 0;
        this.RampUp                 = 0;
        this.ResponsiveMaintainer   = 0;
        this.NetScore               = 0;
    }

    public NetScoreMetric(float license, float correctness, float busfactor, float rampup, float responsivemaintainer, float netscore) {
        this.License                = license;
        this.Correctness            = correctness;
        this.BusFactor              = busfactor;
        this.RampUp                 = rampup;
        this.ResponsiveMaintainer   = responsivemaintainer;
        this.NetScore               = netscore;
    }
}
