package at.ac.tuwien.infosys.dslab.server.auth;

import java.util.Calendar;
import java.util.Date;

public class Duration {

    private final Date start;
    private final Date end;

    private Duration(Date start, Date end){
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
    }

    public static Duration create(Date start, Date end) {
        return new Duration(start, end);
    }

    public static Duration createFromNow(int seconds) {
        Date now = new Date();
        return new Duration(now, getEndDateFrom(now, seconds));
    }

    private static Date getEndDateFrom(Date from, int duration) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, duration);
        return cal.getTime();
    }

    public Date getStart() {
        return new Date(this.start.getTime());
    }

    public Date getEnd() {
        return new Date(this.end.getTime());
    }
}
