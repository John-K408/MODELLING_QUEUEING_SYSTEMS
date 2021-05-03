public class GeometricGenerator {
    public static int generateVar(double p)
    {
        double r = Math.random();
        //(1-p)^i = P(X>i) = r
        //i * log(1-p) = log(r)
        //i = log(r) / log(1-p)
        return (int) Math.ceil(Math.log(r)/ Math.log(1-p));
    }
}
