import java.util.Scanner;

public class QueueSimulation {


    static void simulateQueue(int n, double p , double q, int k)
    /* Where n is the total Number of Jobs that should arrive in the simulation.
     p is the probability for inter-arrival time Geometric Distribution for general jobs
     q is the probability for Job Size Geometric Distribution for general jobs
     k is the number of first arrivals that we should not check in our simulation.
     */
    {


        long currentTime;

        int tempJobSize;



        //int tempJobSize = GeometricGenerator.generateVar(q); //Size for first Job

        //Set expressJobSize to 10;
        int expressJobSize = 10;
        int expressJobInterArrival = 9;
        //Job firstExpressJob = new Job(0, expressJobSize, expressJobSize); // Arrival time for first job will be zero, Departure Time will be equal to Size of Job
        Server expressServer = new Server(expressJobSize,expressJobInterArrival,null,true);
        //Boundary for express Job -- 30 (this is hypothetical);

        //Let q be the probability for the distribution of job size for general jobs.
        //Let p be the probability for the distribution of interarrival time for general jobs.
        int generalJobSize = GeometricGenerator.generateVar(q);
        //Job firstGeneralJob = new Job(0,generalJobSize,generalJobSize);
        Server generalServer = new Server(generalJobSize,GeometricGenerator.generateVar(p),null,false);

        Server server ; // Server with Next Departure time = Departure Time of first Job , Next Arrival time be x~Geometric(p), and job in processing be the firstJob

        //Use booleans to keep track of which activity is happening next for each server

        //isArrGen: The next event in the general server is arrival
        boolean isArrGen = true;

        //isArrExp: The next event in the express server is arrival;
        boolean isArrExp = true;

        //decide on which operation to perform on server
        //server will be set to whichever server among server1 or server2 we are working on.
        boolean isArrivalOperation = false;



        int numJobsArr = 2; // The First Job has already arrived.
        int numJobsDep = 0; // No Jobs have departed yet.
        long sumN = 0;
        long sumT = 0;

        while(numJobsArr < n)
        {

            //Decide on which server to look at next and whether to perform arrival operation or departure operation on
            //server.

            if(isArrGen){
                if(isArrExp){
                    isArrivalOperation = true;
                    if(generalServer.nextArrivalTime < expressServer.nextArrivalTime){
                        server = generalServer;
                    }
                    else{
                        server = expressServer;
                    }

                }
                else{
                    if(generalServer.nextArrivalTime < expressServer.jobInService.depTime){
                        isArrivalOperation = true;
                        server = generalServer;
                    }
                    else{
                        isArrivalOperation = false;
                        server = expressServer;

                    }

                }
            }
            else{

                if(isArrExp){
                    if(generalServer.jobInService.depTime < generalServer.nextArrivalTime){
                        isArrivalOperation = false;
                        server = generalServer;
                    }
                    else{
                        isArrivalOperation = true;
                        server = expressServer;
                    }
                }
                else{
                    isArrivalOperation = false;
                    if(generalServer.jobInService.depTime < expressServer.jobInService.depTime){
                        server = generalServer;
                    }
                    else{
                        server  = expressServer;
                    }

                }

            }

            if(isArrivalOperation)
            {

                currentTime = server.nextArrivalTime;
                System.out.println("Arrival of " + ((server.isExpress)? "express job":"general job") +" at " + currentTime);
                if(server.isExpress)tempJobSize = expressJobSize;
                else tempJobSize = GeometricGenerator.generateVar(q); // Generate Size for Job
                Job tempJob = new Job(currentTime, tempJobSize, server.emptyTime(currentTime)  + tempJobSize); // Generate job

                if(numJobsArr > k)  // The first k boundary exceptions have already arrived
                {
                    sumN += server.numJobs(); //Add current jobs in server
                }
                numJobsArr++;

                if(server.jobInService == null) // No jobs in server
                {
                    System.out.println("Job moved straight into service");
                    server.jobInService = tempJob;
                    server.nextDepartureTime = server.jobInService.depTime;
                    System.out.println("Next Departure Time For " + ((server.isExpress)? "express job":"general job") +" is " + server.nextDepartureTime);
                }

                else // There is a job in service on the server
                {
                    System.out.println("Job added to queue");
                    server.queue.add(tempJob);
                }

                server.nextArrivalTime += GeometricGenerator.generateVar(p); // Add a new Inter Arrival Time

                System.out.println("Next Arrival Time For " + ((server.isExpress)? "express job":"general job") +" is " + server.nextArrivalTime);


            }

            else
            {
                currentTime = server.nextDepartureTime;

                System.out.println("Departure of " + ((server.isExpress)? "express job":"general job") +" at " + currentTime);

                if(numJobsArr > k) // The first k boundary exceptions have already arrived
                {
                    sumT += server.jobInService.depTime - server.jobInService.arrTime; // Adding Service Time
                    numJobsDep++;
                }

                server.jobInService = null; // Take out the current Job from Service
                System.out.println("Job departed");

                if(!server.queue.isEmpty()) // Queue is not empty
                {
                    System.out.println("New job moved into service");
                    server.jobInService = server.queue.pop(); //Bring in the next Job from queue
                    server.nextDepartureTime = server.jobInService.depTime; //Setting the nextDepartureTime as the JobInService's Departure Time.
                    System.out.println("Next Departure time of " + ((server.isExpress)? "express job":"general job") +" is " + server.nextDepartureTime);
                }
                else // No Jobs to be Processed
                {
                    System.out.println("No jobs in " + ((server.isExpress)? "express server":"general server") +" at " + currentTime);
                    server.nextDepartureTime = Long.MAX_VALUE; // an infinite number
                }


            }

            if(server.nextArrivalTime < server.nextDepartureTime){
                if(server.isExpress) isArrExp = true;
                else isArrGen = true;
            }
            else{
                if(server.isExpress) isArrExp = false;
                else isArrGen = false;
            }
        }


//        double expectedN = sumN / (double)(n-k);     // Dividing by total number of jobs that arrived within the boundary
//        double expectedT = sumT /(double)numJobsDep; // Dividing by total number of jobs that arrived within the boundary
//
//        System.out.println("For p = " + p);
//        System.out.println("E[T] = " + expectedT);
//        double formulaeT = (1-p)/(q-p);
//        double formulaeN = p*(1-q)/(q-p);
//        System.out.println("Expected T from formulae is " + formulaeT);
//        System.out.println("E[N] = " + expectedN);
//        System.out.println("Expected N from formulae is " + formulaeN);

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
                simulateQueue(100, p,0.5, 2);
            }

        } while (p <= 1 && p > 0);


    }
}
