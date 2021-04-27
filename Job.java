public class Job {

    private int jobSize;
    private int arrivalTime;

    public Job( int jobSize,int arrivalTime){
        this.jobSize = jobSize;
        this.arrivalTime = arrivalTime;

    }

    public int getJobSize(){
        return this.jobSize;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

}
