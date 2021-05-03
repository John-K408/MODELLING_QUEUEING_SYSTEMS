public class Job {
    public long arrTime;
    public int size;
    public long depTime;

    public Job(long arrTime, int size, long depTime)
    {
        this.arrTime = arrTime;
        this.depTime = depTime;
        this.size = size;
    }
}
