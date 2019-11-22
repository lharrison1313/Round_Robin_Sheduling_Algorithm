import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Scheduler {
    /*
        Each field is described as follows
        clock: the current time of the cpu
        idleClock: the number of time units the cpu spent idle (context switching)
        timeQuantum: the period each process is allotted to run on the cpu
        contextSwitchTime: the number of time units the cpu will take to perform a context switch
        totalProcesses: the total number of processes in the current job
        completedProcesses: the total number of processes that have completed their execution
        ganttChart: a string representation of the order in which the process were run on the cpu and context switched
        readyQueue: queue data structure used to represent the ready queue for the scheduler
        processList: the list of all processes of the current job
     */
    private int clock, idleClock, timeQuantum, contextSwitchTime, totalProcesses, completedProcesses;
    private String ganttChart, processFile;
    private Queue<Process> readyQueue;
    private ArrayList<Process> processList;

    public Scheduler(String processFile, int timeQuantum, int contextSwitchTime) throws FileNotFoundException{
        this.clock = 0;
        this.idleClock = 0;
        this.completedProcesses = 0;
        this.totalProcesses = 0;
        this.timeQuantum = timeQuantum;
        this.contextSwitchTime = contextSwitchTime;
        this.processFile = processFile;
        ganttChart = "";
        this.readyQueue = new LinkedList<>();
        this.processList = new ArrayList<>();
        parseProcessFile(processFile);
        createProcesses();


    }

    //Uses a scanner to parse a csv or text file into an array of process objects
    private void parseProcessFile(String processFile) throws FileNotFoundException {
        String[] processInfo;
        readyQueue.clear();
        processList.clear();
        int id;
        int at;
        int bt;
        Scanner s = new Scanner(new File(processFile));
        while(s.hasNext()){
            totalProcesses++;
            processInfo = s.nextLine().split(",");
            id = Integer.parseInt(processInfo[0]);
            at = Integer.parseInt(processInfo[1]);
            bt = Integer.parseInt(processInfo[2]);
            processList.add(new Process(id,at,bt));
        }
    }

    //prints the average wait time of all processes to terminal
    private void printAverageWait(){
        if(isCompleted()) {
            int total = 0;
            double avg;
            for (Process x : processList) {
                total += x.getCurrentWaitingTime();
            }
            avg = (double) total / (double) totalProcesses;
            System.out.println(String.format("Average Wait Time: %.2f",avg));
        }
        else{
            System.out.println("simulation not completed");
        }

    }

    //prints the average turnaround time of all processes to terminal
    private void printAverageTurnaround(){
        if(isCompleted()) {
            int total = 0;
            double avg;
            for (Process x : processList) {
                total += 1+x.getCompletionTime()-x.getArrivalTime();
            }
            avg = (double)total / (double) totalProcesses;
            System.out.println(String.format("Average Turnaround Time: %.2f",avg));
        }
        else{
            System.out.println("simulation not completed");
        }
    }

    //prints the calculated throughput to the terminal
    private void printThroughput(){
        if(isCompleted()) {
            double throughput = (double)completedProcesses/(double) clock;
            System.out.println(String.format("Throughput: %.4f",throughput) );
        }
        else{
            System.out.println("simulation not completed");
        }
    }

    //prints the cpu utilization to the terminal
    private void printUtilization(){
        if(isCompleted()) {
            double util = 1-((double)idleClock/(double) clock);
            System.out.println(String.format("CPU Utilization: %.4f",util) );
        }
        else{
            System.out.println("simulation not completed");
        }
    }

    private void printAverageResponseTime(){
        if(isCompleted()){
            int total = 0;
            double avg = 0;
            for(Process p: processList){
                total += p.getResponseTime();
            }
            avg = (double) total / (double) totalProcesses;
            System.out.println(String.format("Average Response Time: %.2f",avg));
        }
        else{
            System.out.println("simulation not completed");
        }
    }

    //prints each individual process metric to the terminal
    private void printProcessMetrics(){
        if(isCompleted()){
            int id;
            int turnaround;
            int wait;
            int switches;
            int response;
            for(Process x: processList){
                id = x.getId();
                turnaround = 1+x.getCompletionTime()-x.getArrivalTime();
                wait = x.getCurrentWaitingTime();
                switches = x.getContextSwitches();
                response = x.getResponseTime();
                System.out.println(String.format("Id: %d || Turnaround: %d || Wait Time: %d || Context Switches: %d || Response Time: %d",id,turnaround,wait,switches,response));
            }
        }
        else{
            System.out.println("simulation not completed");
        }
    }

    //prints all of the metrics to the terminal including gantt chart, elapsed time, cpu idle time,average wait, average turnaround, throughput, cpu utilization, process metrics
    public void printAllMetrics(){
        System.out.println(String.format("Simulation Completed!\nGantt Chart: %s\nElapsed Time: %d\nCPU Idle Time: %d", ganttChart,clock,idleClock));
        printAverageWait();
        printAverageTurnaround();
        printAverageResponseTime();
        printThroughput();
        printUtilization();
        printProcessMetrics();

    }

    //prints the current state of the schedule including current time,specific process metrics, processes in ready queue, gantt chart, and numbers of processes completed
    public void printSchedulerState(){
        System.out.println("clock: " + clock);
        for(Process x: processList){
            System.out.println(x);
        }
        System.out.print("Ready Queue: ");
        Process p;
        for(int i = 0; i < readyQueue.size(); i++){
            p = readyQueue.remove();
            System.out.print(p.getId()+"|");
            readyQueue.add(p);
        }
        System.out.println();
        System.out.println("Gantt Chart: " + ganttChart);
        System.out.println("processes completed " + completedProcesses + "/" + totalProcesses);
        System.out.println();
    }

    //acts as the process creator, it loops through each of the processes in the process list and determines if it should be added to the ready queue.
    private void createProcesses(){
        for(Process x : processList){
            if(x.getArrivalTime() == clock){
                readyQueue.add(x);
                x.setInReady(true);
            }
        }
    }

    //makes all processes currently in ready queue wait for one time unit
    private void haveProcessesWait(Process currentProcess){
        for(Process x: processList){
            if(x != currentProcess && !x.isCompleted() && x.isInReady()){
                x.waiting();
            }
        }
    }

    //used to simulate a context switch based on designated context switch time
    private void contextSwitch(Process p){
        p.contextSwitch();
        int count = contextSwitchTime;
        while(count > 0){
            idleClock++;
            clock++;
            count--;
            createProcesses();
        }
    }

    //acts as the cpu, running a single process for the specified time quantum or until the process is finished executing
    public void runNextProcess() {
        if(readyQueue.isEmpty() && !isCompleted()){
            //occurs if there is no process in the ready queue but there are still processes that need to be executed
            int idleCount = 0;
            while(readyQueue.isEmpty()){
                //loops until a process has entered the ready queue
                clock++;
                idleClock++;
                idleCount++;
                createProcesses();
            }
        }
        else if(!isCompleted()) {
            Process p = readyQueue.remove();
            if(!p.hasResponded()){
                p.setResponseTime(clock);
            }
            int count = timeQuantum;
            ganttChart += "->P"+p.getId();
            while (count > 0 && !p.isCompleted()) {
                //loops until the specified time quantum is complete or the process has finished execution
                p.run(clock);
                haveProcessesWait(p);
                clock++;
                count--;
                createProcesses();
            }
            if (!p.isCompleted()) {
                //if process p is not completed it must be placed back in ready queue
                readyQueue.add(p);
                if(p != readyQueue.peek()){
                    //cpu does not context switch if next process is the same as the current
                    contextSwitch(p);
                }
            } else {
                //if process p is completed total completed processes must be updated
                //also no context switch on process termination
                completedProcesses++;
            }
        }
    }

    //checks if the designated job's processes have all been completed
    public boolean isCompleted(){
        return totalProcesses == completedProcesses;
    }




}
