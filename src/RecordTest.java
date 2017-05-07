import junit.framework.TestCase;

/**
 * Created by DJ Sabo on 5/6/17.
 */
public class RecordTest extends TestCase {

    /** Helper method to easily create a record
     * Returns a new <code>Record</code>
     */
    private Record createRecord () {
        try {
            return new Record("abc123", "title1", "provider1",
                    "2017-05-06", 1.5, "0:20");
        }
        catch (Exception e) {
            return null;
        }
    }

    public void testGetStb() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals( "abc123", record.getStb());
    }

    public void testGetTitle() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals("title1", record.getTitle());
    }

    public void testGetProvider() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals("provider1", record.getProvider());
    }

    public void testGetdYear() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(2017, record.getDateYear());
    }

    public void testGetdMonth() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(5, record.getDateMonth());
    }

    public void testGetdDay() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(6, record.getDateDay());
    }

    public void testGetRev() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(1.5, record.getRev());
    }

    public void testGetvHour() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(0, record.getViewHour());
    }

    public void testGetvMinute() throws Exception {
        // Setup
        Record record = createRecord();
        // Test
        assertEquals(20, record.getViewMinute());
    }

    public void testParser() throws Exception {
        // Setup
        String data = "stb1|the matrix|warner bros|2014-04-01|4.00|1:30";
        // Call
        try {
            Record record = new Record(data);
            assertEquals("stb1", record.getStb());
            assertEquals("the matrix", record.getTitle());
            assertEquals("warner bros", record.getProvider());
            assertEquals(2014, record.getDateYear());
            assertEquals(4, record.getDateMonth());
            assertEquals(1, record.getDateDay());
            assertEquals(4.0, record.getRev());
            assertEquals(1, record.getViewHour());
            assertEquals(30, record.getViewMinute());

        }
        catch (Exception e) {
            // if an exception is thrown the test fails
            assertFalse(true);
        }
    }

    public void testParserBadYear() throws Exception {
        // Setup
        String data = "stb1|the matrix|warner bros|224-04-01|4.00|1:30";
        // Call
        try {
            Record record = new Record(data);
            assertTrue(false);

        }
        catch (Exception e) {
            // if an exception is thrown the test fails
            assertTrue(true);
        }
    }

    public void testParserBadHour() throws Exception {
        // Setup
        String data = "stb1|the matrix|warner bros|2014-04-01|4.00|128:30";
        // Call
        try {
            Record record = new Record(data);
            assertTrue(false);

        } catch (Exception e) {
            // if an exception is thrown the test fails
            assertTrue(true);
        }
    }
}