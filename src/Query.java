import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * Handles queries from command line arguments.
 * Required: Select '-s FIELDNAME'. can have multiple fields separated by commas
 * Optional: Order By '-o FIELDNAME' can handle multiple order bys separated by commas
 * Optional: Filter '-f FIELDNAME=DATA' filters the result to only contain elements that match the given data
 */
public class Query {
    private final String JSONFILE = "data.json";
    protected String[] select = {};
    protected String[] order = {};
    protected String[] filter = {};

    public Query(String[] args){
        execute(args);
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

    protected void execute(String[] args){
        // set the parameters
        optionsParse(args);

        try {
            importRecords();
        }
        catch (Exception e) {
            System.out.println("Could not import records, Exiting");
            System.exit(1);
        }

    }

    public void importRecords() throws Exception {
        ConcurrentSkipListMap<String, String> records = new ConcurrentSkipListMap<>();
        File file = new File(JSONFILE);

        if (!file.exists()) {
            System.out.println("Info: No data to load from Json");
            return;
        }

        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(file);
        JSONArray list = (JSONArray) parser.parse(reader);
        for(int i = 0; i < list.size(); i++) {
            JSONObject obj = (JSONObject) list.get(i);

            // build the key so that records are sorted correctly
            String key = "";
            if(this.order.length > 0) {
                for (int k = 0; k < this.order.length; k++) {
                    key += obj.get(this.order[k]);
                    key += (k == this.order.length - 1 ? i : "");
                }
            }
            else {
                key += obj.get("STB");
                key += i;
            }

            // builds the string to display
            String value = "";
            for(int j = 0; j < this.select.length; j++){
                value += obj.get(this.select[j]);
                value += (j == this.select.length -1 ? "": "," );
            }

            // If there is a filter only add the record if the filter matches
            if (this.filter.length == 2) {
                if (this.filter[1].equals((String) obj.get(this.filter[0]))) {
                    records.put(key, value);
                }
            }
            // if no filter just add the record
            else {
                records.put(key, value);
            }
        }

        for (String value : records.values()){
            System.out.println(value);
        }

    }


    public static void main(String[] args) {
      Query query = new Query(args);

    }

}
