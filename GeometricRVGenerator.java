public class GeometricRVGenerator {
    private double p;
    private int numRuns;

    public GeometricRVGenerator(double p){
        this.p = p;
        numRuns = 0;
    }

    public int getRandomValue(){
        numRuns = 0;
        while(true){
            if(numRuns == Integer.MAX_VALUE) return Integer.MAX_VALUE;
            numRuns++;
            if(Math.random() < p){
                return numRuns;
            }
        }
    }
}
