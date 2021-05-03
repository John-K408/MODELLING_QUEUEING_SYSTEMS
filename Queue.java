
class QueueNode
{
    Job job;
    QueueNode nextNode;
}

public class Queue {
    private QueueNode head;
    private QueueNode tail;
    private int numJobs;
    void add(Job job)
    {
        QueueNode temp = new QueueNode();
        temp.job = job;

        if(head == null)
            head = temp;

        else
            tail.nextNode = temp;

        tail = temp;
        numJobs++;
    }

    Job pop()
    {
        if(numJobs != 0) numJobs--;
        if(head == null) return null;
        Job temp = head.job;
        head = head.nextNode;
        return temp;
    }

    Job peek()
    {
        if(head == null) return null;
        return head.job;
    }

    Job peekTail() // Returns Last Element of Queue
    {
        if(tail == null) return null;
        return tail.job;
    }

    boolean isEmpty()
    {
        return head == null;
    }

    int numJobs() // Returns number of elements in the Queue
    {
        return numJobs;
    }
}
