package lambda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Created by Edward Lin
 * on 3/8/16 7:39 AM.
 */
public class ThreadLocalTest {

    public static void main(String[] args) {
        Thread t1 = new Thread(new Task());
        Thread t2 = new Thread(new Task());
        t1.start();
        t2.start();
        Thread t3 = new Thread(()-> System.out.println("ddd"));
    }

    public static String threadSafeFormat(Date date){
        DateFormat formatter = PerThreadFormatter.getDateFormatter();
        return formatter.format(date);
    }
//    public final static ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> new DateFormatter(new SimpleDateFormat("dd-MMM-yyyy")));
}

class PerThreadFormatter{

    private static final ThreadLocal<SimpleDateFormat>
            dateFormatHolder = new ThreadLocal<SimpleDateFormat>(){

        @Override
        protected SimpleDateFormat initialValue() {
            System.out.println("Create SimpleDateFormat for Thread:"
                    + Thread.currentThread().getName());
            return new SimpleDateFormat("dd-MM-yyyy", Locale.CHINA);
        }

    };

    public static DateFormat getDateFormatter() {
        return dateFormatHolder.get();
    }
}

class Task implements Runnable{

    @Override
    public void run() {
        for(int i=0; i<2; i++){
            System.out.println("Thread: "
                    + Thread.currentThread().getName()
                    + " Formatted Date: "
                    + ThreadLocalTest.threadSafeFormat(new Date()) );
        }
    }
}