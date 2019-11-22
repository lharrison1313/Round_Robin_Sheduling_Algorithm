import java.io.FileNotFoundException;

public class Driver {
    public static void main(String[] args){
        try {
            if(args.length >= 3) {
                Scheduler s = new Scheduler(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                while (!s.isCompleted()) {
                    //executes the runNextProcess method until the job is completed
                    s.runNextProcess();
                    if (args.length == 4 && args[3].equalsIgnoreCase("true"))
                        s.printSchedulerState();
                }
                s.printAllMetrics();
            }
            else{
                System.out.println("invalid arguments");
            }



        }
        catch (FileNotFoundException e){
            System.out.println(e);
        }

    }
}
