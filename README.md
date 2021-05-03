#Description of Classes

##Server
Class holds information about the server such as nextDepartureTime, nextArrivalTime, Queue, and the jobInService.

##Job
Class holds information about a job including depTime, arrTime, and size.

##Queue
A basic Queue with extra functions such as peekTail()- returns the tail of the Queue, and numJobs() which returns
the number of item in the Queue.

##GeometricGenerator
Class has only a single static function generateVar(double p) which generates a random Var ~ Geometric(p).

##QueueSimulation
Brings everything together and generates a simulation of the queue through its function
simulateQueue(int n, double p , double q, int k).

#How to run
Run main from the QueueSimulation.java file. 

In the main function, As only p needs to be changed in question 4, I repetitively ask user a value for p for which
the E[T] and E[N] needs to be calculated. Program terminates when the p entered is less than or equal to 0 or
is greater than or equal to 1. Else, The method simulateQueue(int n, double p , double q, int k) is called for that
specific p, n = 100000, q = 0.5, and k = 2000.
