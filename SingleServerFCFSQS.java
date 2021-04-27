public class SingleServerFCFSQS {

    public static void main(String args[]){
        Server server = new Server();

        //Random variable for interarrival time p-value
        double  p = Double.parseDouble(args[0]);



        //Random variable for job size p-value
        double q = Double.parseDouble(args[1]);


        int numJobs = Integer.parseInt(args[2]);
        GeometricRVGenerator interArrivalTimeRV = new GeometricRVGenerator(p);

        GeometricRVGenerator jobSizeRV = new GeometricRVGenerator(q);

        runServer(numJobs,interArrivalTimeRV,jobSizeRV,server);



    }


    public static void runServer(int numJobs, GeometricRVGenerator interArrivalTimeRV, GeometricRVGenerator jobSizeRV,Server server){
        int jobNumber = 0;
        int interArrivalTime = interArrivalTimeRV.getRandomValue();

        server.nextArrivalTime = interArrivalTime;


        //whilst there are more jobs to be processed
        while(server.numJobsProcessed< numJobs || !server.queue.isEmpty()){

            //if next event is an arrival
            if(server.nextArrivalTime < server.nextDepartureTime){

                System.out.println("Next is arrival");

                //if we haven't reached our job limit, process new job arrival;
                if(server.numJobsProcessed < numJobs){
                    server.numJobsProcessed += 1;

                    System.out.println("New number of jobs in server: " + server.numJobsProcessed);

                    //Move current system time to next arrival time;
                    server.currentSystemTime = server.nextArrivalTime;

                    System.out.println("current system time: " + server.currentSystemTime);

                    //calculate new interarrival time to be used to set nextarrival time.
                    interArrivalTime = interArrivalTimeRV.getRandomValue();


                    //setNextArrivalTime
                    server.nextArrivalTime = server.currentSystemTime + interArrivalTime;
                    System.out.println("Next arrival time: " + server.nextArrivalTime);


                    //generate job
                    int jobSize = jobSizeRV.getRandomValue();
                    Job job = new Job(jobSize,server.currentSystemTime);


                    //if there are no jobs in server, add job to server;
                    if(server.jobInService == null){
                        System.out.println("Job " + ++jobNumber + " enters service");
                        server.jobInService = job;


                        //set next departure time;
                        server.nextDepartureTime = server.currentSystemTime + jobSize;

                        System.out.println("Job departs at " + server.nextDepartureTime);
                    }


                    //if there is a job in server, add new job to queue;
                    else{
                        System.out.println("Job enters queue at position " + (server.queue.size() + 1));
                        server.queue.add(job);
                    }

                }

                //if we have, make it so that next arrival time is infinity.
                else{
                    System.out.println("All jobs have been processed");
                    server.nextArrivalTime = Integer.MAX_VALUE;
                }

            }


            //if next job is a departure
            else{
                System.out.println("Job about to depart");


                //move current time to departure time
                server.currentSystemTime = server.nextDepartureTime;
                System.out.println("Current time: " + server.currentSystemTime);


                //finish job
                server.jobInService = null;
                System.out.println("Job finished");


                //get new job from queue if queue isn't empty
                if(!server.queue.isEmpty()){
                    System.out.println("Moving job "+ ++jobNumber + " from queue to server");
                    server.jobInService = server.queue.poll();

                    server.nextDepartureTime = server.currentSystemTime + server.jobInService.getJobSize();

                    System.out.println("Current job leaves server at " + server.nextDepartureTime);
                }

                //if queue is empty, set new departure time to highest;
                else{
                    System.out.println("Finished processing all current jobs in system");
                    server.nextDepartureTime = Integer.MAX_VALUE;
                }


                //set new departure time;

            }
        }

    }


}
