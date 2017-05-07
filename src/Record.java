import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Object to store a single Record. There will be no setters for this because once the object created it should not be
 * modified. There are two constructors, one which takes in the individual arguments and one that takes in a string and
 * will parse the arguments out of the string.
 */
public class Record {
    private char[] stb;
    private char[] title;
    private char[] provider;
    private LocalDateTime date_view_time;
    private double rev;

    /**
     * Constructor to create a new <code>Record</code>
     * @param stb  The set top box id on which the media asset was viewed. (Text, max size 64 char)
     * @param title The title of the media asset. (Text, max size 64 char)
     * @param provider The distributor of the media asset. (Text, max size 64 char)
     * @param date TThe local date on which the content was leased by through the STB (A date in YYYY-MM-DD format)
     * @param rev The price incurred by the STB to lease the asset. (Price in US dollars and cents)
     * @param view_time The amount of time the STB played the asset.  (Time in hours:minutes)
     */
    public Record(String stb, String title, String provider, String date, double rev, String view_time) throws Exception {
        this.stb = stb.toCharArray();
        this.title = title.toCharArray();
        this.provider = provider.toCharArray();
        setDateTime(date, view_time);
        this.rev = rev;

    }

    public Record (String data) throws Exception {
        parser(data);
    }

    protected void setStb(String stb) {
        if (stb.length() > 64 || stb.length() == 0){
            throw new IllegalArgumentException("Invalid STB: " + stb);
        }
        this.stb = stb.toCharArray();
    }

    protected void setTitle(String title) {
        if (title.length() > 64 || title.length() == 0){
            throw new IllegalArgumentException("Invalid Title: " + title);
        }
        this.title = title.toCharArray();
    }

    protected void setProvider(String provider) {
        if (provider.length() > 64 || provider.length() == 0){
            throw new IllegalArgumentException("Invalid Provider: " + provider);
        }
        this.provider = provider.toCharArray();
    }

    protected void setRev(double rev) {
        this.rev = rev;
    }

    /**
     *
     * @param date string representing the data with the format of yyyy-MM-dd
     * @param time string representing the time with the format of HH:mm
     * @throws ParseException if either date or time are invalid
     */
    protected void setDateTime(String date, String time) throws ParseException{

        try { // If the date is invalid it will throw an exception
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd k:mm");
            String datetime = date + " " + time;
            this.date_view_time = LocalDateTime.parse(date + " " + time, formatter);
        }
        // If it wasn't able to set the date because of invalid format or a bad date, throws an exception
        catch (IllegalArgumentException iae) {
            throw new ParseException(date + " " + time + " is not a valid date/time", 0);
        }
        catch (NullPointerException np) {
            throw new ParseException("Date or Time cannot be null", 0);
        }

    }

    public String getStb() {
        return String.valueOf(stb);
    }

    public String getTitle() {
        return String.valueOf(title);
    }

    public String getProvider() {
        return String.valueOf(provider);
    }

    public int getDateYear() {
        return date_view_time.getYear();
    }

    public int getDateMonth() {
        return date_view_time.getMonthValue();
    }

    public int getDateDay() {
        return date_view_time.getDayOfMonth();
    }

    public double getRev() {
        return rev;
    }

    public int getViewHour() {
        return date_view_time.getHour();
    }

    public int getViewMinute() {
        return date_view_time.getMinute();
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date_view_time.format(formatter);
    }

    public String getViewTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("k:mm");
        return date_view_time.format(formatter);
    }


    /**
     * Parser splits a string into the individual pieces and sets the Record variables
     * @param data the string that will be parsed
     * @exception if any of the parsing or number conversion fails, this will throw an exception
     */
    protected void parser(String data) throws Exception{

        // split the string and verify that we have the right number of elements
        String[] list = data.split("\\|");
        if (list.length != 6 ) {
            throw new IllegalArgumentException();
        }

        this.stb = list[0].toCharArray();
        this.title = list[1].toCharArray();
        this.provider = list[2].toCharArray();
        setDateTime(list[3], list[5]);
        this.rev = Double.valueOf(list[4]);



    }

    public java.lang.String toString() {
        return "Record{" +
                "stb=" + String.valueOf(stb) +
                ", title=" + String.valueOf(title) +
                ", provider=" + String.valueOf(provider) +
                ", dYear=" + getDateYear() +
                ", dMonth=" + getDateMonth() +
                ", dDay=" + getDateDay() +
                ", rev=" + rev +
                ", vHour=" + getViewHour() +
                ", vMinute=" + getViewMinute() +
                '}';
    }

}
