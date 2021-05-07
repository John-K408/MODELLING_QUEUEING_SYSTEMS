public class Job {
    public long arrTime;
    public int size;
    public long depTime;
    public boolean isExpress;

    public Job(long arrTime, int size, long depTime, boolean isExpress)
    {
        this.arrTime = arrTime;
        this.depTime = depTime;
        this.size = size;
        this.isExpress = isExpress;
    }
}
