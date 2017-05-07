import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileWriter;


/**
 * Manages a HashMap of Records. Any record with the same STB, TITLE, and DATE will be overwritten
 *
 */
public class Records {
    public final String JSONFILE = "data.json";
    private HashMap<String, Record> records;

    public Records() {
        this.records = new HashMap<>();
    }

    public Records(String filename) {
        this.records = new HashMap<>();
        importFromFile(filename);
    }

    public void addRecord(Record record) {
        String key = record.getStb() + record.getTitle() + record.getDate();
        records.put(key, record);
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
     *
     */
    public void importFromFile(String filename){
        File file;
        Scanner reader;
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
    public void exportToJson() throws IOException {
        FileWriter file = new FileWriter(JSONFILE);
        JSONArray list = new JSONArray();

        for (Record record: records.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("KEY", record.getStb()+record.getTitle()+record.getDate());
            jsonObject.put("STB", record.getStb());
            jsonObject.put("TITLE", record.getTitle());
            jsonObject.put("PROVIDER", record.getProvider());
            jsonObject.put("DATE", record.getDate());
            jsonObject.put("REV", record.getRev());
            jsonObject.put("VIEW_TIME", record.getViewTime());

            //file.write(jsonObject.toJSONString() + "\n" );
            list.add(jsonObject);

        }

        file.write(list.toJSONString());
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
