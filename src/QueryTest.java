import junit.framework.TestCase;

/**
 * Created by beast on 5/7/17.
 */
public class QueryTest extends TestCase {
    public void testOptionsParse() {
        // Setup
        String[] args = {"-s", "TITLE,REV,DATE", "-o", "DATE,TITLE", "-f", "DATE=2014-04-01"};
        try {
            // Call
            Query query = new Query(args);
            // Test
            assertEquals(3, query.select.length);
            assertEquals(2, query.order.length);
            assertEquals(2, query.filter.length);
        }
        catch (Exception e) {
            assertTrue(false);
        }
     }

    public void testOptionsParseNoFilter() {
        // Setup
        String[] args = {"-s", "TITLE,REV,DATE", "-o", "DATE,TITLE"};
        try {
            // Call
            Query query = new Query(args);
            // Test
            assertEquals(3, query.select.length);
            assertEquals(2, query.order.length);
            assertEquals(0, query.filter.length);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testOptionsParseInvalid() {
        // Setup
        String[] args = {"-s", "TITLE,REV,DATE", "-o"};
        try {
            // Call
            Query query = new Query(args);
            // Test
            assertTrue(false);
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

}