import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * Manages a set of Records. Any record with the same STB, TITLE, and DATE will be overwritten by the
 * most recent record. Stores the records in a JSON file for archiving.
 */

public class Records {
    private final String JSONFILE = "data.json";
    private HashMap<String, Record> records = new HashMap<>();
    private HashMap<String, Location> recordMappings = new HashMap<>();
    private int maxRecords = 1000;
    private int recordCount = 0;
    private String jsonName = "export";

    /**
     * Object to store a records location
      */
    public class Location {
       String filename;
       int index;

       public Location(String filename, int index) {
           this.filename = filename;
           this.index = index;
       }
   }

    /**
     * Class to store a record needing to be updated
     */
    public class RecordUpdate extends Location {
        Record record;

        public RecordUpdate(Location location, Record record) {
            super(location.filename, location.index);
            this.record = record;
        }
    }

    /**
     * Sets the max number of records to add before exporting to a file
     * @param max record count per file
     */
    public void setMaxRecords( int max) {
        this.maxRecords = max;
    }

    /**
     * sets the default name for json exports
     * @param name file name to export
     */
    public void setJsonName(String name){
        this.jsonName = name;
    }


    /**
     * Adds the <code>Record</code> to <code>Records</code>. Creates a key out of the record's STB+TITLE+DATE
     * to ensure that each record is unique. If the key already exits in records the record in records
     * will be overwritten.
     * @param record the data to add
     */
    public void addRecord(Record record) {
        String key = record.getStb() + record.getTitle() + record.getDate();
        records.put(key, record);
        recordCount++;
    }

    /**
     * @return the total number if unique records
     */
    public int getCount() {
        return records.size();
    }

    /**
     * Imports all records in a given file into the hashmap. Records with duplicate keys will be overwritten subsequent
     * records. If there is any issue opening the file an error will be printed and the task will terminate gracefully.
     * If there is any issue parsing a record, the record will be printed, skipped, and the import will continue.
     * @param filename the name of the file to import from.
     */
    public void importFromFile(String filename){
        File file;
        Scanner reader;

        // Open the file and throw an exception if there is any issue
        try {
            file = new File(filename);
            reader = new Scanner(file);
        }
        catch (IOException e) {
            System.out.println("Error: could not open file '" + filename + "'");
            return;
        }
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            try {
                Record record = new Record(line);
                // If the current record count is the max we will export to a json file
                if (this.recordCount % maxRecords == maxRecords -1 ) {
                    int num = this.recordCount / this.maxRecords;
                    exportToJson(this.jsonName + num);
                }
                addRecord(record);
            }
            catch (Exception e) {
                System.out.println("Error: Record could not be parsed '" + line + "'");
                continue;
            }
        }

    }

    /**
     * Exports the data into a json file. Also adds a new pair with a key and value so that objects can be found
     * to be updated. The key is a combination of the STB+TITLE+DATE which identifies unique records.
     * @throws IOException if there is a problem with the file
     */
    public void exportToJson(String filename) throws Exception {
        ConcurrentSkipListMap<String, RecordUpdate> toUpdate = new ConcurrentSkipListMap<>();
        FileWriter file = new FileWriter(filename);
        JSONArray list = new JSONArray();
        int index = 0;

        for (Record record: records.values()) {
            // see if the record already exists in a file
            Location found = recordMappings.get(record.getKey());
            // if the found then we need to update the record so we add it to our update list
            if( found != null) {
                RecordUpdate upRecord = new RecordUpdate(found, record);
                // Creating the key with the filename first so that the files will be grouped
                toUpdate.put(found.filename + record.getKey(), upRecord);
            }
            // otherwise we add the file to our output
            else {
                JSONObject jsonObject = recordToJson(record);
                // Add the record to our json blob
                list.add(jsonObject);
                // Add the location to the record mapping
                recordMappings.put(record.getKey(), new Location(filename, index));
                index++;
            }
        }

        // update other files if needed
        if(toUpdate.size() > 0) {
            updateFiles(toUpdate);
        }

        file.write(list.toJSONString());
        file.close();
        // when we are done exporting we clear the records
        records.clear();

    }

    protected JSONObject recordToJson(Record record) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("KEY", record.getKey());
        jsonObject.put("STB", record.getStb());
        jsonObject.put("TITLE", record.getTitle());
        jsonObject.put("PROVIDER", record.getProvider());
        jsonObject.put("DATE", record.getDate());
        jsonObject.put("REV", record.getRev());
        jsonObject.put("VIEW_TIME", record.getViewTime());
        return jsonObject;
    }

    /**
     * Updates the records in their corresponding files. Groups all records for a specific file so that it only
     * needs to be updated ones.
     * @param toUpdate the records needing to be updated
     * @throws Exception IO or Parse exceptions if there are any issues
     */
    protected void updateFiles(ConcurrentSkipListMap<String, RecordUpdate> toUpdate) throws Exception {
        if (toUpdate.size() == 0) {
            return;
        }

        // Get the first file and setup the parser
        String filename =  toUpdate.get(toUpdate.firstKey()).filename;
        File file = new File(filename);
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(file);
        JSONArray list = (JSONArray) parser.parse(reader);


        for(RecordUpdate uRecord : toUpdate.values()){
            //RecordUpdate uRecord = toUpdate.get(i);
            // Check if the record is in the file already loaded, if not load the file
            if (!filename.equals(uRecord.filename)) {
                // updated the file before we move to the next
                writeJsonArrayToFile(filename, list);
                // read in the new file
                filename = uRecord.filename;
                reader = new FileReader(file);
                list = (JSONArray) parser.parse(reader);
            }

            // updates the data in the json array
            list.remove(uRecord.index);
            list.add(uRecord.index,recordToJson(uRecord.record));
        }

        // update the last file
        writeJsonArrayToFile(filename, list);

    }

    /**
     * Writes the contents of the array to the file
     * @param filename the name of the file to be written
     * @param array the data to be written in the file
     * @throws Exception when there is an IO issue
     */
    protected void writeJsonArrayToFile(String filename, JSONArray array) throws Exception {
        FileWriter file = new FileWriter(filename);
        file.write(array.toJSONString());
        file.close();

    }

    public void importFromJson() throws Exception {
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

            // get the info for the json object
            String stb = (String) obj.get("STB");
            String title = (String) obj.get("TITLE");
            String provider = (String) obj.get("PROVIDER");
            String date = (String) obj.get("DATE");
            Double rev = (Double) obj.get("REV");
            String view_time = (String) obj.get("VIEW_TIME");
            Record record = new Record(stb, title, provider, date, rev, view_time);

            // add the record to records
            addRecord(record);
        }

    }

}

