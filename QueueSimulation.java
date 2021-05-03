import java.util.Scanner;

public class QueueSimulation {


    static void simulateQueue(int n, double p , double q, int k)
    /* Where n is the total Number of Jobs that should arrive in the simulation.
     p is the probability for inter-arrival time Geometric Distribution.
     q is the probability for Job Size Geometric Distribution.
     k is the number of first arrivals that we should not check in our simulation.
     */
    {
        long currentTime = 0;
        int tempJobSize = GeometricGenerator.generateVar(q); //Size for first Job
        Job firstJob = new Job(0, tempJobSize, tempJobSize); // Arrival time for first job will be zero, Departure Time will be equal to Size of Job
        Server server = new Server(tempJobSize, GeometricGenerator.generateVar(p), firstJob); // Server with Next Departure time = Departure Time of first Job , Next Arrival time be x~Geometric(p), and job in processing be the firstJob

        int numJobsArr = 1; // The First Job has already arrived.
        int numJobsDep = 0; // No Jobs have departed yet.
        long sumN = 0;
        long sumT = 0;

        while(numJobsArr < n)
        {
            if(server.nextArrivalTime < server.nextDepartureTime)
            {
                currentTime = server.nextArrivalTime;
                tempJobSize = GeometricGenerator.generateVar(q); // Generate Size for Job
                Job tempJob = new Job(currentTime, tempJobSize, server.emptyTime(currentTime)  + tempJobSize); // Generate job

                if(numJobsArr > k)  // The first k boundary exceptions have already arrived
                {
                    sumN += server.numJobs(); //Add current jobs in server
                }
                numJobsArr++;

                if(server.jobInService == null) // No jobs in server
                {
                    server.jobInService = tempJob;
                    server.nextDepartureTime = server.jobInService.depTime;
                }

                else // There is a job in service on the server
                {
                    server.queue.add(tempJob);
                }

                server.nextArrivalTime += GeometricGenerator.generateVar(p); // Add a new Inter Arrival Time
            }

            else
            {
                currentTime = server.nextDepartureTime;
                if(numJobsArr > k) // The first k boundary exceptions have already arrived
                {
                    sumT += server.jobInService.depTime - server.jobInService.arrTime; // Adding Service Time
                    numJobsDep++;
                }

                server.jobInService = null; // Take out the current Job from Service

                if(!server.queue.isEmpty()) // Queue is not empty
                {
                    server.jobInService = server.queue.pop(); //Bring in the next Job from queue
                    server.nextDepartureTime = server.jobInService.depTime; //Setting the nextDepartureTime as the JobInService's Departure Time.
                }
                else // No Jobs to be Processed
                {
                    server.nextDepartureTime = Long.MAX_VALUE; // an infinite number
                }

            }
        }


        double expectedN = sumN / (double)(n-k);     // Dividing by total number of jobs that arrived within the boundary
        double expectedT = sumT /(double)numJobsDep; // Dividing by total number of jobs that arrived within the boundary

        System.out.println("For p = " + p);
        System.out.println("E[T] = " + expectedT);
        double formulaeT = (1-p)/(q-p);
        double formulaeN = p*(1-q)/(q-p);
        System.out.println("Expected T from formulae is " + formulaeT);
        System.out.println("E[N] = " + expectedN);
        System.out.println("Expected N from formulae is " + formulaeN);

    }
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        double p;
        do {

            System.out.print("Please Enter Value of p in between 0 and 1(or not in range to terminate program): ");
            p = scan.nextDouble();
            if(p <= 1 && p > 0)
            {
                simulateQueue(100000, p,0.5, 2000);
            }

        } while (p <= 1 && p > 0);


    }
}
