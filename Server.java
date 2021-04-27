import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Server {

     int currentSystemTime;
     int nextDepartureTime;
     Job jobInService;
     int numJobsProcessed;
     Queue<Job> queue;
     int nextArrivalTime;


    public Server(){

        numJobsProcessed = 0;
        queue = new LinkedList<>();
        nextDepartureTime = Integer.MAX_VALUE;
        currentSystemTime  = 0;
        jobInService = null;
        nextArrivalTime = Integer.MAX_VALUE;

    }






}
