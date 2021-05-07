import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Scanner;

public class QueueSimulation {


    static void simulateQueue(int n, double p , double q, int k) throws IOException
    /* Where n is the total Number of Jobs that should arrive in the simulation.
     p is the probability for inter-arrival time Geometric Distribution for general jobs
     q is the probability for Job Size Geometric Distribution for general jobs
     k is the number of first arrivals that we should not check in our simulation.
     */
    {
        BufferedWriter bwExpress = new BufferedWriter(new FileWriter("ExpressResponseTimes.txt",false));

        BufferedWriter bwGeneral = new BufferedWriter(new FileWriter("GeneralResponseTime.txt",false));


        long currentTime;

        int tempJobSize;


        //int tempJobSize = GeometricGenerator.generateVar(q); //Size for first Job

        //Set expressJobSize to 5;
        int expressJobSize = (int)(0.5/q + 1);

        //1/p is the expected value for the interrarrival time of general jobs.
        //We expect it to take a little longer for express jobs to come in
        int expressJobInterArrival =(int) (3.0/p);

        Server[] servers = new Server[3];
        long[][] times = new long[2][3];


        //Job firstExpressJob = new Job(0, expressJobSize, expressJobSize); // Arrival time for first job will be zero, Departure Time will be equal to Size of Job
        servers[0] = new Server(Long.MAX_VALUE,null,true);
        //Boundary for express Job -- 30 (this is hypothetical);

        //Let q be the probability for the distribution of job size for general jobs.
        //Let p be the probability for the distribution of interarrival time for general jobs.
        int generalJobSize = GeometricGenerator.generateVar(q);

        //Do these because we want to know the average size of general jobs so we can set fixed size of express jobs to something
        //similar or lower

        //sum of job Sizes for general job
        int sumJobSizeGeneral  = 0;

        //Number of general jobs worked on so far beyond the initial k;
        int numGeneralJobs = 0;

        //Sum of response times for general jobs
        long sumRTGeneralJobs = 0;

        //Sum of response times for express jobs

        long sumRTExpressJobs = 0;

        //

        for(int i = 1; i < 3; i++)
        {
            servers[i] = new Server(Long.MAX_VALUE,null,false);
        }

        //The server we are currently performing departure or arrival on - could be express or
        Server server ;


        //Use booleans to keep track of which activity is happening next for each server
        //isArrGen: The next event in the general server is arrival
        boolean isArrGen = true;

        //isArrExp: The next event in the express server is arrival;
        boolean isArrExp = true;

        //decide on which operation to perform on server
        //server will be set to whichever server among server1 or server2 we are working on.
        boolean isArrivalOperation = false;



        int numJobsArr = 0; // No Jobs have already arrived.
        int numJobsDep = 0; // No Jobs have departed yet.
        //sumN is the total number of jobs in system at any moment in time
        long sumN = 0;

        //sumT is the total response time for all jobs above k till now.
        long sumT = 0;

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 3; j++)
                times[i][j] = Long.MAX_VALUE;

        times[1][0] = expressJobInterArrival;
        times[1][1] = GeometricGenerator.generateVar(p);
        //times [1][2] is unused.
        //times [0] stores all departure times. time[1][0] store express job arrival time. time[1][1] stores regular job arrival time.
        int boundarySizeExpress = expressJobSize * 2;
        boolean expressLaneChecked = false;
        int numJobsMoved = 0;
        int numExpressJobsNotInBoundary = 0;
        while(numJobsArr < n)
        {

            //Decide on which server to look at next and whether to perform arrival operation or departure operation on
            //server.
            for(int i = 0; i < 3; i++)
            {
                times[0][i] = servers[i].nextDepartureTime;
            }
            int[]  indices  = minValue2DArray(times);


            if(indices[0] == 0) // Means Departure Operation
            {
                server = servers[indices[1]];
                currentTime = server.nextDepartureTime;

                System.out.println("Departure of " + ((server.jobInService.isExpress)? "express job":"general job") +" at " + currentTime);

                if(numJobsArr > k) // The first k boundary exceptions have already arrived
                {
                    //sumT += server.jobInService.depTime - server.jobInService.arrTime; // Adding Service Time
                    numJobsDep++;
                }

                server.jobInService = null; // Take out the current Job from Service
                System.out.println("Job departed");

                if(!server.queue.isEmpty()) // Queue is not empty
                {
                    System.out.println("New job moved into service");
                    server.jobInService = server.queue.pop(); //Bring in the next Job from queue
                    server.nextDepartureTime = server.jobInService.depTime; //Setting the nextDepartureTime as the JobInService's Departure Time.
                    System.out.println("Next Departure time of" + ((indices[1] == 0)? "express server":"general server " + indices[1]) +" is " + server.nextDepartureTime);
                }
                else // No Jobs to be Processed
                {
                    System.out.println("No jobs in " + ((indices[1] == 0)? "express server":"general server " + indices[1]) +" at " + currentTime);
                    server.nextDepartureTime = Long.MAX_VALUE; // an infinite number
                }


            }
            else // Means Arrival Operation
            {
                currentTime = times[indices[0]][indices[1]];
                System.out.println("Arrival of " + ((indices[1] == 0)? "express job":"general job") +" at " + currentTime);
                if(indices[1] == 0)
                {
                    tempJobSize = expressJobSize;
                    server = servers[0];
                    expressLaneChecked = false;
                }
                else
                {
                    tempJobSize = GeometricGenerator.generateVar(q);
                    long[] emptyTimeArray = {servers[0].emptyTime(currentTime), servers[1].emptyTime(currentTime), servers[2].emptyTime(currentTime)};
                    int serverIndex = minValue1DArray(emptyTimeArray);
                    if (serverIndex != 0) {
                        server = servers[serverIndex];
                    }

                    else
                    {
                        if ((servers[0].emptyTime(currentTime) + tempJobSize + expressJobSize) - (times[1][1])< boundarySizeExpress) //Calculate if next Express Job is done within boundary time. DepartureTime - ArrivalTime < boundarySizeExpress
                        {
                            server = servers[0];
                            numJobsMoved++;
                        }

                        else
                        {
                            boolean tempBool = !expressLaneChecked;
                            expressLaneChecked = true;
                            if (tempBool && Math.random() < 0.05)
                            {
                                server = servers[0];
                                numJobsMoved++;
                            }
                            else
                            {
                                if (servers[1].emptyTime(currentTime) < servers[2].emptyTime(currentTime))
                                {
                                    server = servers[1];
                                }
                                else
                                {
                                    server = servers[2];
                                }
                            }
                        }
                    }
                }

                Job tempJob = new Job(currentTime, tempJobSize, server.emptyTime(currentTime)  + tempJobSize, indices[1] == 0); // Generate job

                if(numJobsArr > k)  // The first k boundary exceptions have already arrived
                    {
                        //Since not every job actually finishes (program ends when all the jobs we want have arrived), we
                        //should calculate a job's response time once it arrives so all jobs above boundary's response time
                        //can be calculated
                        long responseTime = tempJob.depTime - tempJob.arrTime;
                        if(indices[1] == 0 && responseTime > boundarySizeExpress)
                        {
                            numExpressJobsNotInBoundary++;
                        }
                        System.out.println(((indices[1] == 0)? "Express Job":"General Job")+" response time: " + responseTime);
                        if(indices[1] == 0){
                            sumRTExpressJobs += responseTime;
                            bwExpress.write(responseTime + "\n");
                        }
                        else{
                            //get the number of general jobs that arrive in summer over entire iteration
                            numGeneralJobs++;
                            sumRTGeneralJobs += responseTime;
                            bwGeneral.write(responseTime + "\n");
                        }

                        //sumN += server.numJobs(); //Add current jobs in server
                    }

                numJobsArr++;

                if(server.jobInService == null) // No jobs in server
                    {
                        System.out.println("Job moved straight into service");
                        server.jobInService = tempJob;
                        server.nextDepartureTime = server.jobInService.depTime;
                        System.out.println("Next Departure Time For " + (indices[1] == 0? "express job":"general job") +" is " + server.nextDepartureTime);
                    }

                else // There is a job in service on the server
                    {
                        System.out.println("Job added to queue");
                        server.queue.add(tempJob);
                    }

                if(indices[1] == 0)times[1][0] += expressJobInterArrival;
                else times[1][1] += GeometricGenerator.generateVar(p);

                System.out.println("Next Arrival Time For " + ((indices[1] == 0)? "express job is " + times[1][0] :"general job is " + times[1][1]));

                }
            }


        // System.out.println("Average General Job Size: " + sumJobSizeGeneral/numGeneralJobs);
        System.out.println("Average Response Time for General Jobs: " + (sumRTGeneralJobs+ 0.0)/numGeneralJobs);
        System.out.println("Average Response Time For Express Jobs: " + (sumRTExpressJobs+ 0.0)/(n - numGeneralJobs - k));
        System.out.println("Number of Jobs Moved from General Servers to Express Servers: " + numJobsMoved);
        System.out.println("Percentage of Express Jobs done within boundary: " + (100 * (1 - (0.0 +  numExpressJobsNotInBoundary)/(n - numGeneralJobs - k))) );


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

    static int[] minValue2DArray(long[][] array)
    {
        int[] returnValue = new int[2];
        long minValue = Long.MAX_VALUE;
        for(int i = 0; i < array.length; i++)
        {
            for(int j = 0; j < array[0].length; j++)
            {
                if(array[i][j] < minValue)
                {
                    minValue = array[i][j];
                    returnValue[0] = i;
                    returnValue[1] = j;
                }
            }
        }
        return returnValue;
    }

    static int minValue1DArray(long[] array)
    {
        int returnValue = 0;
        long minValue = Long.MAX_VALUE;
        for(int i = 0 ; i < array.length; i++)
        {
            if(array[i] < minValue)
            {
                minValue = array[i];
                returnValue = i;
            }
        }
        return returnValue;
    }
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        double p;


        do {

            System.out.print("Please Enter Value of p in between 0 and 1(or not in range to terminate program): ");
            p = scan.nextDouble();
            if(p <= 1 && p > 0)
            {
                simulateQueue(1000000, p,0.1, 2000);
            }

        } while (p <= 1 && p > 0);


    }
}

