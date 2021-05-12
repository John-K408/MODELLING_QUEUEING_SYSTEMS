import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Scanner;

public class QueueSimulation {


    static void simulateQueue(int n, double p1 ,double q1, int k) throws IOException
    /* Where n is the total Number of Jobs that should arrive in the simulation.
     p1 is the probability for inter-arrival time Geometric Distribution for general jobs
     p2 is the probability for inter-arrival time for Geometric distribution for e
     xpress jobs
     q1 is the probability for Job Size Geometric
      Distribution for general jobs
     k is the number of first arrivals that we should not check in our simulation.
     */
    {
        BufferedWriter bwExpress = new BufferedWriter(new FileWriter("ExpressResponseTimes.txt",false));

        BufferedWriter bwGeneral = new BufferedWriter(new FileWriter("GeneralResponseTime.txt",false));


        long currentTime;

        int tempJobSize;

        int expressJobSize = (int)(0.5/q1 + 1);

        //1/p1 is the expected value for the interrarrival time of general jobs.
        //We expect it to take a little longer for express jobs to come in
        double p2 = p1/3.0;
        int expressJobInterArrival = GeometricGenerator.generateVar(p2);

        //5th percentile for interrArrival times;
        int worstExpressJobInterArrival = (int)Math.ceil(Math.log(1 - 0.05)/Math.log(1 - p2));

        int boundarySizeExpress = expressJobSize * 3;
        //Index 0: Express server
        //Index 1: General server
        //Index 2: General server
        Server[] servers = new Server[3];

        // index row 0: Departures
        //Index row 1: Arrivals
        long[][] times = new long[2][3];

        servers[0] = new Server(Long.MAX_VALUE,null,true);

        //Let q1 be the probability for the distribution of job size for general jobs.
        //Let p1 be the probability for the distribution of interarrival time for general jobs.


        //Do these because we want to know the average size of general jobs so we can set fixed size of express jobs to something
        //similar or lower

        //Number of general jobs worked on so far beyond the initial k;
        int numGeneralJobs = 0;

        //Sum of response times for general jobs
        long sumRTGeneralJobs = 0;

        //Sum of response times for express jobs
        long sumRTExpressJobs = 0;

        for(int i = 1; i < 3; i++)
        {
            servers[i] = new Server(Long.MAX_VALUE,null,false);
        }

        //The server we are currently performing departure or arrival on - could be express or
        Server server;



        int numJobsArr = 0; // No Jobs have already arrived.

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 3; j++)
                times[i][j] = Long.MAX_VALUE;

        times[1][0] = expressJobInterArrival;
        times[1][1] = GeometricGenerator.generateVar(p1);
        //times [1][2] is unused.
        //times [0] stores all departure times. time[1][0] store express job arrival time. time[1][1] stores regular job arrival time.


        //check if express lane has been checked
        boolean expressLaneChecked = false;

        //keep track of number of jobs moved from general to express lane
        int numJobsMoved = 0;

        //keep track of the number of jobs that could not be moved because they crossed size boundary
        int numExpressJobsNotInBoundary = 0;

        //while there are more jobs to be checked...
        while(numJobsArr < n)
        {

            //Decide on which server to look at next and whether to perform arrival operation or departure operation on
            //server.
            for(int i = 0; i < 3; i++)
            {
                times[0][i] = servers[i].nextDepartureTime;
            }

            //indices will contain the indices of the minimum value in the 2 x 3 array -> The server and whether it is arrival or departure.
            //Index 0: indicates whether it is an arrival or departure (If 0 -> Departure; If 1 -> Arrival).
            //Index 1: Indicates which server the arrival/departure was for.
            int[]  indices  = minValue2DArray(times);


            if(indices[0] == 0) // Means Departure Operation
            {
                //set server to whichever server it's for.
                server = servers[indices[1]];
                currentTime = server.nextDepartureTime;

                System.out.println("Departure of " + ((server.jobInService.isExpress)? "express job":"general job") +" at " + currentTime);


                server.jobInService = null; // Take out the current Job from Service
                System.out.println("Job departed");

                if(!server.queue.isEmpty()) // Queue is not empty
                {
                    System.out.println("New job moved into service");
                    server.jobInService = server.queue.pop(); //Bring in the next Job from queue
                    server.nextDepartureTime = server.jobInService.depTime; //Setting the nextDepartureTime as the JobInService's Departure Time.
                    System.out.println("Next Departure time of " + ((indices[1] == 0)? "express server":"general server " + indices[1]) +" is " + server.nextDepartureTime);
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
                //if express job
                if(indices[1] == 0)
                {
                    tempJobSize = expressJobSize;
                    server = servers[0];
                    expressLaneChecked = false;
                }
                //if general job
                else
                {
                    tempJobSize = GeometricGenerator.generateVar(q1);
                    server = servers[indices[1]];
                    long[] emptyTimeArray = {servers[0].emptyTime(currentTime), servers[1].emptyTime(currentTime), servers[2].emptyTime(currentTime)};
                    //get server with least emptyTime
                    int serverIndex = minValue1DArray(emptyTimeArray);
                    //if any of general servers have least empty time
                    if (serverIndex != 0) {
                        server = servers[serverIndex];
                    }
                    //if express server has least empty time
                    else
                    {

                        //if all current jobs, general job that is about to be moved,  and next express job will be finished within
                        //boundary time, move general job to express server
                        if ((servers[0].emptyTime(currentTime) + tempJobSize + expressJobSize) - (currentTime + worstExpressJobInterArrival)< boundarySizeExpress) //Calculate if next Express Job is done within boundary time. DepartureTime - ArrivalTime < boundarySizeExpress
                        {
                            server = servers[0];
                            numJobsMoved++;
                        }

                        //if they won't finish within time,
                        else
                        {
                            /*boolean tempBool = !expressLaneChecked;
                            expressLaneChecked = true;
                            //move job wrongly with probability 5%
                            if (tempBool && Math.random() < 0.05)
                            {
                                server = servers[0];
                                numJobsMoved++;
                            }
                            else
                            {*/
                                if (servers[1].emptyTime(currentTime) < servers[2].emptyTime(currentTime))
                                {
                                    server = servers[1];
                                }
                                else
                                {
                                    server = servers[2];
                                }
                            //}
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
                    //if express job and response time exceeds boundary
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

                if(indices[1] == 0)times[1][0] += GeometricGenerator.generateVar(p2);
                else times[1][1] += GeometricGenerator.generateVar(p1);

                System.out.println("Next Arrival Time For " + ((indices[1] == 0)? "express job is " + times[1][0] :"general job is " + times[1][1]));

            }
        }



        System.out.println("Average Response Time for General Jobs: " + (sumRTGeneralJobs+ 0.0)/numGeneralJobs);
        System.out.println("Average Response Time For Express Jobs: " + (sumRTExpressJobs+ 0.0)/(n - numGeneralJobs - k));
        System.out.println("Number of Jobs Moved from General Servers to Express Servers: " + numJobsMoved);
        System.out.println("Percentage of Express Jobs done within boundary: " + (100 * (1 - (0.0 +  numExpressJobsNotInBoundary)/(n - numGeneralJobs - k))) );



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
        double p1;


        do {

            System.out.print("Please Enter Value of p1 in between 0 and 1(or not in range to terminate program): ");
            p1 = scan.nextDouble();
            if(p1 <= 1 && p1 > 0)
            {
                simulateQueue(100000, p1,0.1, 100);
            }

        } while (p1 <= 1 && p1 > 0);


        int boundary = (int)(Math.log(0.01)/Math.log(1 - p1));


    }
}

