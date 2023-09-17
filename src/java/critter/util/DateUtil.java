package critter.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class DateUtil {
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String localTimeToStr(LocalTime t) {
        return t.format(timeFormatter);
    }

    public static String localDateToStr(LocalDate ld) {
        return ld.format(dateFormatter);
    }
    public static long unixTime(LocalDate ld, LocalTime tm) {
        LocalDateTime ldt = LocalDateTime.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth(),
                tm.getHour(), tm.getMinute(), 0);
        return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
    /*
    private static final DateFormat _formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final Date _toDay = new Date();
    private static final GregorianCalendar _cal0 = new GregorianCalendar();
    private static final GregorianCalendar _cal1 = new GregorianCalendar();
    private static int MILLIS_IN_DAY = 60 * 60 * 24 * 1000;

    static {
        _cal0.setTime(_toDay);
    }

    public static double diffDays(Date curDate) {
        _cal1.setTime(curDate);
        return ((_cal1.getTimeInMillis() - _cal0.getTimeInMillis()) / MILLIS_IN_DAY);
    }

    public static Date createDate(int year, int month, int day) {
        _cal1.set(Calendar.YEAR, year);
        _cal1.set(Calendar.MONTH, month-1);
        _cal1.set(Calendar.DATE, day);
        return _cal1.getTime();
    }

    public static Date parse(String dateStr) {
        try {
            return (Date)_formatter.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtils.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    public static String format(Date date) {
        return _formatter.format(date);
    }

     */
}
