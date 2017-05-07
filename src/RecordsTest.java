import junit.framework.TestCase;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by DJ Sabo on 5/6/17.
 */
public class RecordsTest extends TestCase {
    private String createFile(String filename) {
        File file = new File(filename);
        try {
            if(file.exists()){
                deleteFile(filename);
            }

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            StringBuilder sb = new StringBuilder();
            writer.write("stb1|the matrix|warner bros|2014-04-01|4.00|1:30\n");
            writer.write("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45\n");
            writer.write("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05\n");

            writer.write("stb3|the matrix|warner bros|2014-04-02|4.00|1:05\n");
            writer.close();
        }

        catch (Exception e) {
            System.out.println("Error creating file");
        }
        return filename;
    }

    private void deleteFile(String filename){
        File file = new File(filename);
        if(file.exists()){
            file.delete();
        }
    }

    public void testImportFromFile() throws Exception {
        // Setup
        String filename = "TempFile";
        createFile(filename);
        // Call
        Records records = new Records(filename);
        // Test
        assertEquals(4, records.getCount());
        // Cleanup
        deleteFile(filename);
    }

    public void testImportFromFileNoDuplicates() throws Exception {
        // Setup
        String filename = "TempFile";
        createFile(filename);
        // Call
        Records records = new Records(filename);
        records.importFromFile(filename);
        // Test
        assertEquals(4, records.getCount());
        // Cleanup
        deleteFile(filename);
    }

    public void testJSONExportImport() throws Exception {
        // Setup
        String filename = "TempFile";
        createFile(filename);
        Records records2 = new Records();
        // Call
        Records records = new Records(filename);
        records.exportToJson();
        records2.importFromJson();
        // Test
        assertEquals(4, records.getCount());
        assertEquals(4, records2.getCount());
        // Cleanup
        deleteFile(filename);

    }

}