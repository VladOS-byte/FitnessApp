import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DataBaseForCommunication {
    private static Connection connection;
    private static Connection connectionx;
    private static PreparedStatement ps;
    private static Cipher cipher;
    private static Cipher cipher1;
    public static void conn() throws ClassNotFoundException, SQLException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        connection=null;
        connectionx=null;
        Class.forName("org.sqlite.JDBC");
        connection= DriverManager.getConnection("jdbc:sqlite:CommunityDataBas.s3db");//создание и подключение к БД
        connectionx= DriverManager.getConnection("jdbc:sqlite:fitDataBase.s3db");
        cipher= Cipher.getInstance("AES");
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key=keyGenerator.generateKey();
        cipher.init(Cipher.ENCRYPT_MODE,key);
        cipher1=Cipher.getInstance("AES");
        cipher1.init(Cipher.DECRYPT_MODE,key);
    }
    public static void create() throws SQLException{
        ps=connection.prepareStatement("CREATE TABLE IF NOT EXISTS community (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login0, login1,message text,date,status,time integer);");
        ps.execute();
        ps.close();
    }
    public static ArrayList<String> readMessages(String login0, String login1,int delta1time) throws SQLException{
        ArrayList<String> messages=new ArrayList<>();
        ps=connection.prepareStatement("SELECT id,message,date,time,status,login0 FROM community WHERE (login0='"+login0+"')AND(login1='"+login1+"')OR(login0='"+login1+"')AND(login1='"+login0+"')");
        ResultSet rs=ps.executeQuery();
        int deltat=(TimeZone.getDefault().getRawOffset()-delta1time)/60000;
        while (rs.next()){
            String s[]=per(rs.getInt("time")-deltat,rs.getInt("date"));
            messages.add(rs.getString("login0")+"Ќ"+rs.getString("message")+"Ќ"+s[0]+"Ќ"+s[1]+"Ќ"+rs.getString("status"));
            if(login1.equals(rs.getString("login0"))&&1==rs.getInt("status")){
                PreparedStatement pas=connection.prepareStatement("UPDATE community SET status=? WHERE id='"+rs.getInt("id")+"'");
                pas.setInt(1,0);
                pas.executeUpdate();
                pas.close();
            }
        }
        rs.close();
        ps.close();
        return messages;
    }
    public static String send(String login0,String login1,String message,int delta1time)throws  SQLException{
        ps=connection.prepareStatement("INSERT INTO community(login0,login1,message,status,date,time) VALUES (?,?,?,?,?,?)");
        ps.setString(1,login0);
        ps.setString(2,login1);
        ps.setString(3,message);
        ps.setInt(4,1);
        String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        ps.setInt(5, Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        int time=new Date().getHours()*60+new Date().getMinutes();
        ps.setInt(6,new Date().getHours()*60+new Date().getMinutes());
        ps.executeUpdate();ps.close();
        int deltat=(TimeZone.getDefault().getRawOffset()-delta1time)/1000/60;
        String m[]=per(time-deltat,Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        update(login0,login1);
        ps.close();
        return m[0]+"Ќ"+m[1]+"Ќ"+1;
    }
    private static String[] per(int time, int date){
        if (time<0){
            time+=24*60;
            if(date%100==1)
                if (Math.round(date/100%100)==0)
                    date=date-10000+1100+30;
                else{
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.YEAR,Math.round(date/10000));
                    calendar.set(Calendar.MONTH,Math.round(date/100%100)-1);
                    date=date+calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-101;
                }
            else
                date-=1;
        }
        if(time>24*60){
            time-=24*60;
            Calendar calendar=Calendar.getInstance();
            calendar.set(Calendar.YEAR,Math.round(date/10000));
            calendar.set(Calendar.MONTH,Math.round(date/100%100));
            if(date%100==calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                if (Math.round(date/100%100)==11)
                    date=date+10000-1100-30;
                else{
                    date=Math.round((date+100)/100)*100+1;
                }
            else
                date+=1;
        }
        String timestr=Math.round(time/60)+":"+time%60;
        String datestr=date%100+"/"+Math.round(date/100%100)+"/"+Math.round(date/10000);
        return new String[]{timestr,datestr};
    }
    private static void update(String login0, String login1)throws SQLException{
        ps=connectionx.prepareStatement("SELECT communities FROM users WHERE login='"+login0+"'");
        ResultSet ras=ps.executeQuery();
        upd(login1, login0, ras);
        ras.close();
        ps.close();
        ps=connectionx.prepareStatement("SELECT communities FROM users WHERE login='"+login1+"'");
        ResultSet rs=ps.executeQuery();
        upd(login0, login1, rs);
        rs.close();
        ps.close();
    }

    private static void upd(String login0, String login1, ResultSet rs) throws SQLException {
        if (rs.next())
        {
            String[] as=rs.getString("communities").split(";");
            if (rs.getString("communities").equals("")){
                PreparedStatement pas=connectionx.prepareStatement("UPDATE users SET communities=? WHERE login='"+login1+"'");
                pas.setString(1,login0);
                pas.executeUpdate();
                pas.close();
            }
            else
            for (String a:as) {
                if(a.equals(login0))
                    break;
                else
                if (a.equals(as[as.length-1]))
                {
                    PreparedStatement pas=connectionx.prepareStatement("UPDATE users SET communities=? WHERE login='"+login1+"'");
                    pas.setString(1,rs.getString("communities")+";"+login0);
                    pas.executeUpdate();
                    pas.close();
                }
            }
        }
    }

    public static String news(String login0,String login1,int delta1time)throws SQLException{
        ps=connection.prepareStatement("SELECT message,time,date FROM community WHERE login0='"+login1+"' AND login1='"+login0+"'AND status=1");
        ResultSet rs=ps.executeQuery();
        int deltat=(TimeZone.getDefault().getRawOffset()-delta1time)/60000;
        if (rs.next()){
            String s[]=per(rs.getInt("time")-deltat,rs.getInt("date"));
            String a=rs.getString("message")+"Ќ"+s[0]+"Ќ"+s[1];
            ps.close();
            rs.close();
            update(login0,login1);
            return a;}
        return "NO";
    }
}
