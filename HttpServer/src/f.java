import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class f {

    public static void main(String[] args) {
        Calendar date=Calendar.getInstance();
        date.set(2018,0,1);
        int year=date.get(Calendar.YEAR);
        int month=date.get(Calendar.MONTH);
        int day=date.get(Calendar.DAY_OF_MONTH);
        System.out.println(day+"/"+month+"/"+year);
}
}
