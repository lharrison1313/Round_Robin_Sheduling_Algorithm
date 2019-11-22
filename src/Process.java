

public class Process {
    /*
        Each field is described as follows
        id: process id
        burstTime: the initial process burst time
        currentBurstTime: the current burst time of the process
        arrivalTime: the time the process will arrive in the ready queue
        currentWaitingTime: the number of time units this process has spent waiting
        completionTime: the time of this processes completion
        contextSwitches: the number of context switches performed on this process
        responseTime: the time which the process has responded
        completed: boolean value that shows if the process has completed
        inready: boolean value that shows if the process is in the ready queue currently
        responded: boolean value that shows if the process has responded
     */
    private int id, burstTime, currentBurstTime, arrivalTime, currentWaitingTime, completionTime, contextSwitches, responseTime;
    boolean completed,inReady, responded;

    public Process(int id, int arrivalTime, int burstTime){
        this.id = id;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.currentBurstTime = burstTime;
        this.currentWaitingTime = 0;
        this.completionTime = -1;
        this.contextSwitches = 0;
        this.responseTime = -1;
        responded = false;
        completed = false;
        inReady = false;

    }

    // input: the current cpu clock time
    // simulates the running of the process for 1 time unit by decreasing its current burst time
    public void run(int currentTime){
        if(currentBurstTime > 1) {
            currentBurstTime--;
        }
        else if(currentBurstTime == 1){
            currentBurstTime--;
            terminate(currentTime);
        }
    }

    //input current cpu clock time
    // simulates the termination of the process by setting the completion time and setting completed to true
    private void terminate(int currentTime){
        completionTime = currentTime;
        inReady = false;
        completed = true;
    }

    //input cpu clock time
    //sets the response time with the current cpu clock time
    public void setResponseTime(int currentTime){
        responded = true;
        responseTime  = currentTime-arrivalTime;
    }

    // simulates the process waiting by increasing its wait time
    public void waiting(){
        currentWaitingTime++;
    }

    // increases the processes number of context switches by one
    public void contextSwitch(){
        contextSwitches += 1;
    }

    // checks if the process has completed its execution
    public boolean isCompleted(){
        return completed;
    }

    // checks if the process is in the ready queue
    public boolean isInReady(){
        return inReady;
    }

    public boolean hasResponded(){
        return responded;
    }

    //sets the processes in ready queue state
    public void setInReady(boolean x){
        inReady = x;
    }

    //various getters for fields
    public int getId() {
        return id;
    }

    public int getContextSwitches(){
        return contextSwitches;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCurrentWaitingTime() {
        return currentWaitingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getResponseTime(){
        return responseTime;
    }

    //returns a formatted string of the processes state
    public String toString(){
        return String.format("ID: %d || Remaining Burst Time: %d || Time Waiting: %d || Context Switches: %d || Responded: %b|| Completed: %b",id,currentBurstTime,currentWaitingTime,contextSwitches,responded,completed );
    }

}
