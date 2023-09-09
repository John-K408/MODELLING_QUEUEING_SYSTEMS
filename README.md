# Modelling A Queuing System

Optimized a theoretical Multi-Server System (in Java) with Express and Regular Servers, whose job sizes and inter-arrival times were modeled with geometric distributions.

Improved average response time for regular jobs by 80% by efficiently using Express Servers for both Express and Regular Jobs.


# Description of Classes

## Server
Class holds information about the server such as nextDepartureTime, nextArrivalTime, Queue, and the jobInService.

## Job
Class holds information about a job including depTime, arrTime, and size.

## Queue
A basic Queue with extra functions such as peekTail()- returns the tail of the Queue, and numJobs() which returns
the number of item in the Queue.

## GeometricGenerator
Class has only a single static function generateVar(double p) which generates a random Var ~ Geometric(p).

## QueueSimulation
Brings everything together and generates a simulation of the queue through its function
simulateQueue(int n, double p , double q, int k).

# How to run
Run main from the QueueSimulation.java file. 

In the main function, I repetitively ask user a value for p for which E[T] . Program terminates when the p entered is less than or equal to 0 or
is greater than or equal to q. Else, The method simulateQueue(int n, double p , double q, int k, int system, int phase) is called for that
specific p, n = 100000, q = 0.3, and k = 2000, system = 2, phase = 1.

You can change the arguments provided to the simulateQueue function in the main function to get results for different systems, phases, and q values.

## NOTE
The Systems in the code start from 0. So, System 1 from our research paper would be System 0 in the code.

The Phases in the code start from 0. So, Phase 1 from our research paper would be Phase 0 in the code.
