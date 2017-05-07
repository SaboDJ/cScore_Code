import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by beast on 5/7/17.
 */
public class Query {
    protected String[] select = {};
    protected String[] order = {};
    protected String[] filter = {};

    public Query(String[] args){
        optionsParse(args);
    }


    /**
     * Parses the inputted arguments and adds sets the corresponding arrays to the argument values.
     *
     * @param args
     *        The arguments from the command line
     * @Throws IllegalArgumentException if any of the arguments are invalid
     */
    protected void optionsParse(String[] args ) {
        if(args.length == 0) {
            System.err.println("Missing command line arguments");
            System.exit(1);
        }

        for(int i = 0; i < args.length; i=i+2) {
            // Checks to see if we have an option flag followed
            if(args[i].equals("") || args[i].charAt(0) != '-'  || i+1 == args.length) {
                throw new IllegalArgumentException();
            }
            else if(args[i].equals("-s")){
                // Set the Select parameters
                this.select = args[i+1].split(",");
            }
            else if(args[i].equals("-o")){
                // Set the Order parameters
                this.order = args[i+1].split(",");
            }
            else if(args[i].equals("-f")){
                // Set the Filter parameters
                this.filter = args[i+1].split("=");
            }
            else {
                System.err.println("Error: " + args[i] + " is an invalid argument");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
      Query query = new Query(args);

    }
}
