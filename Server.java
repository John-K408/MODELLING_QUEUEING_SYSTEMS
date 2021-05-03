public class Server {

    long nextDepartureTime;
    long nextArrivalTime;
    Queue queue;
    Job jobInService;

    public Server(long nextDepartureTime, long nextArrivalTime, Job jobInService)
    {
        this.nextArrivalTime = nextArrivalTime;
        this.nextDepartureTime = nextDepartureTime;
        queue = new Queue();
        this.jobInService = jobInService;
    }
    long emptyTime(long currentTime) // Returns the time the server will become empty if no new Jobs Arrive
    {
        if(queue.isEmpty())
        {
            if(jobInService == null)
                return currentTime; // server is already empty

            else
                return nextDepartureTime; // Server has only a single job(that is currently being processed)
        }

        return queue.peekTail().depTime;
    }

    int numJobs() // Returns number of jobs in a server
    {
        if(jobInService == null)
        {
            return 0;
        }

        return queue.numJobs() + 1;
    }
}
