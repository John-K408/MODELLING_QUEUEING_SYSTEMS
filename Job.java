public class Job {

    private int jobSize;
    private int arrivalTime;
    private boolean isExpress;

    public Job( int jobSize,int arrivalTime,boolean isExpress){
        this.jobSize = jobSize;
        this.arrivalTime = arrivalTime;
        this.isExpress = isExpress;
    }

    public int getJobSize(){
        return this.jobSize;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public boolean isExpress(){
        return isExpress;
    }

}
