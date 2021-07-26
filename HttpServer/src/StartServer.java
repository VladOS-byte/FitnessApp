import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class StartServer {
    private static Connection connection;
    private static PreparedStatement ps;
    private static Cipher cipher;
    private static Cipher cipher1;
    public static void conn() throws ClassNotFoundException, SQLException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        connection=null;
        Class.forName("org.sqlite.JDBC");
        connection= DriverManager.getConnection("jdbc:sqlite:fitDataBas.s3db");
        cipher=Cipher.getInstance("AES");
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key=keyGenerator.generateKey();
        cipher.init(Cipher.ENCRYPT_MODE,key);
        cipher1=Cipher.getInstance("AES");
        cipher1.init(Cipher.DECRYPT_MODE,key);
    }
    public static void create() throws SQLException{
        ps=connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login,pass,name,mail,communities text,date real, weight text,hight," +
                "upr1,upr2,upr3,upr4,upr5,upr6,upr7,upr8,upr9,upr10,upr11,upr12,upr13,upr14,upr15,upr16,upr17,upr18,upr19,upr20,upr21,upr22,upr23,upr24,upr25,upr26,upr27,upr28,upr29,upr0 INT);");
        ps.execute();
        ps.close();
    }
    private static int checkLogin(String resurse)throws SQLException{
        ps=connection.prepareStatement("SELECT login,id FROM users");
        ResultSet resultSet=ps.executeQuery();
        while(resultSet.next())
            if (resurse.equals(resultSet.getString("login"))){
                return resultSet.getInt("id");
            }
        return -1;
    }
    public static String sendDate(String log)throws SQLException{
        ps=connection.prepareStatement("SELECT date FROM users WHERE login=?");
        ps.setString(1,log);
        ResultSet rs=ps.executeQuery();
        if (rs.next()){
            return Long.toString(rs.getLong("date"));
        }
        return "ERROR";
    }
    public static void writeAllRecords(final String log,String resurse)throws SQLException{
        String[] mas=resurse.split(",");
        for (int i = 0; i < 30; i++) {
            ps=connection.prepareStatement("SELECT upr"+i+" FROM users WHERE login='"+log+"'");
            ResultSet resultSet=ps.executeQuery();
            if (resultSet.next()){
                int res=resultSet.getInt("upr"+i);
                resultSet.close();
                ps.close();
                if (res<Integer.parseInt(mas[i])){
                    ps=connection.prepareStatement("UPDATE users SET upr"+i+"=? WHERE login='"+log+"'");
                    ps.setInt(1,Integer.parseInt(mas[i]));
                    ps.executeUpdate();
                    ps.close();}}
        }
    }
    private static String upDateUpr(final String log, String clientCommand, String resurse)throws SQLException{
        ps=connection.prepareStatement("SELECT "+clientCommand+" FROM users WHERE login='"+log+"'");
        ResultSet resultSet=ps.executeQuery();
        if (resultSet.next()){
            int res=resultSet.getInt(clientCommand);
            resultSet.close();
            ps.close();
            if (res<Integer.parseInt(resurse)){
                ps=connection.prepareStatement("UPDATE users SET "+clientCommand+"=? WHERE login='"+log+"'");
                ps.setInt(1,Integer.parseInt(resurse));
                ps.executeUpdate();
                ps.close();
            }
            return Integer.toString(res);
        }
        return "ERROR";
    }
    public static String write(final String log,String clientCommand,String resurse) throws  SQLException{
        if(clientCommand.substring(0,3).equals("upr")){
            return upDateUpr(log, clientCommand, resurse);
        }
        switch (clientCommand){
            case "pass":{ ps=connection.prepareStatement("UPDATE users SET pass=? WHERE login=?");break;}
            case "name":{ ps=connection.prepareStatement("UPDATE users SET name=? WHERE login=?");break;}
            case "mail":{ ps=connection.prepareStatement("UPDATE users SET mail=? WHERE login=?");break;}
            case "weight":{ps=connection.prepareStatement("UPDATE users SET weight=? WHERE login=?");break;}
            case "hight":{ ps=connection.prepareStatement("UPDATE users SET hight=? WHERE login=?");break;}
            default: return "ERROR";
        }
        ps.setString(2,log);
        if (clientCommand.equals("hight"))
            ps.setInt(1,Integer.parseInt(resurse));
        else
            ps.setString(1,resurse);
        ps.executeUpdate();
        ps.close();
        return "OK";
    }
    public static void fullTable()throws SQLException{
        ResultSet resultSet=connection.createStatement().executeQuery("SELECT * FROM users");
        while(resultSet.next()) System.out.println(resultSet.getString("pass")+resultSet.getString("login")+resultSet.getString("hight")+resultSet.getString("weight")+resultSet.getString("mail")+resultSet.getString("name"));
        resultSet.close();
    }
    public static void delete()throws SQLException{
        ps=connection.prepareStatement("DROP TABLE users");
        ps.execute();
        ps.close();
    }
    public static String maxOfUpr(String log,int i)throws SQLException{
        StringBuilder result=new StringBuilder();
            String sql="SELECT COUNT(login) FROM users WHERE upr"+i+">(SELECT upr"+i+" FROM users WHERE login='"+log+"')";
            ps=connection.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            rs.next();
            int res=rs.getInt("COUNT(login)");
            sql="SELECT COUNT(login) FROM users WHERE upr"+i+"=(SELECT upr"+i+" FROM users WHERE login='"+log+"')";
            ps=connection.prepareStatement(sql);
            rs=ps.executeQuery();
            rs.next();
            int res0=rs.getInt("COUNT(login)");
            if(res0==0|res0==1)
                result.append(res+1).append(" ");
            else
                result.append(res+1).append("-").append(res+res0).append(" ");
            rs.close();ps.close();
            sql="SELECT max(upr"+i+") FROM users";
            ps=connection.prepareStatement(sql);
            rs=ps.executeQuery();
            rs.next();
            int max=rs.getInt("max(upr"+i+")");
            result.append(max).append(" ");rs.close();ps.close();
        return result.toString();
    }
    public static String forUpr()throws SQLException{
        StringBuilder result=new StringBuilder();
        for (int i = 0; i < 30; i++) {
            String sql="SELECT max(upr"+i+") FROM users";
            ps=connection.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            rs.next();
            int max=rs.getInt("max(upr"+i+")");
            result.append(max).append("--");rs.close();ps.close();
            sql="SELECT login  FROM users WHERE upr"+i+"="+max;
            ps=connection.prepareStatement(sql);
            rs=ps.executeQuery();
            rs.next();
            result.append(rs.getString("login")).append(" ");rs.close();ps.close();}
        return result.toString();
    }
    public static String readAll(final String log)throws SQLException {
        final int ID = checkLogin(log);
        ps = connection.prepareStatement("SELECT login FROM users WHERE id=?");
        StringBuilder s = new StringBuilder();
        if (ID != -1) {
            ps.setInt(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                s.append(rs.getString("login")).append(";");
            }
            rs.close();
            ps.close();
        } else
            return "ERROR";
        ps = connection.prepareStatement("SELECT pass,mail,name,hight,weight FROM users WHERE login='"+log+"'");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            s.append(rs.getString("pass")).append(";").append(rs.getString("mail")).append(";").append(rs.getString("weight"))
                    .append(";").append(rs.getString("hight")).append(";").append(rs.getString("name")).append(";");
        }
        rs.close();
        ps.close();
        for (int i = 0; i < 30; i++) {
            String sql="SELECT upr"+i+" FROM users WHERE login='"+log+"'";
            ps=connection.prepareStatement(sql);
            rs=ps.executeQuery();
            rs.next();
            s.append(rs.getInt("upr"+i)).append(";");rs.close();ps.close();}
        return s.toString();
    }
    public static String read(final String log,String clientCommand) throws  SQLException{
        switch (clientCommand){
            case "pass":{
                ps=connection.prepareStatement("SELECT pass FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){return rs.getString("pass");}
                ps.close();
                break;
            }
            case "name":{
                ps=connection.prepareStatement("SELECT name FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){return rs.getString("name");}
                ps.close();
            }
            case "log":{
                final int ID=checkLogin(log);
                ps=connection.prepareStatement("SELECT login FROM users WHERE id=?");
                if (ID!=-1){
                    ps.setInt(1,ID);
                    ResultSet rs=ps.executeQuery();
                    if(rs.next()){return rs.getString("login");}
                    ps.close();}
                else
                    return "ERROR";
                break;
            }
            case "mail":{
                ps=connection.prepareStatement("SELECT mail FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){return rs.getString("mail");}
                ps.close();
                break;
            }
            case "weight":{
                ps=connection.prepareStatement("SELECT weight FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){ return rs.getString("weight");}
                ps.close();
                break;
            }
            case "communities":{
                ps=connection.prepareStatement("SELECT communities FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){ return rs.getString("communities");}
                ps.close();
                break;
            }
            case "hight":{
                ps=connection.prepareStatement("SELECT hight FROM users WHERE login=?");
                ps.setString(1,log);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){ return rs.getString("hight");}
                ps.close();
                break;
            }
            default:{
                return "ERROR";
            }
        }
        return "ERROR";
    }
    public static String writeNEW(String[] resurse) throws  SQLException {
        int ch = checkLogin(resurse[0]);
        if (ch == -1) {
            Date datenow=new Date();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyy");
            long date=Long.parseLong(simpleDateFormat.format(datenow));
            ps = connection.prepareStatement("INSERT INTO users(login,pass,mail,weight,hight,name,date,upr1,upr2,upr3,upr4,upr5,upr6,upr7,upr8,upr9,upr10,upr11,upr12,upr13,upr14," +
                    "upr15,upr16,upr17,upr18,upr19,upr20,upr21,upr22,upr23,upr24,upr25,upr26,upr27,upr28,upr29,upr0,communities) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, resurse[0]);
            ps.setString(2, resurse[1]);
            ps.setString(3, resurse[2]);
            ps.setString(4, resurse[3]);
            ps.setInt(5, Integer.parseInt(resurse[4]));
            ps.setString(6, resurse[5]);
            ps.setLong(7,date);
            for (int i = 0; i < 30; i++) {
                ps.setInt(i + 8, 0);
            }
            ps.setString(38,"");
            ps.executeUpdate();
            ps.close();
            return "OK";
        }
        else
            return "ERROR";
    }
    public static String find(String log)throws SQLException{
        ps=connection.prepareStatement("SELECT login FROM users");
        ResultSet rs=ps.executeQuery();
        StringBuilder result= new StringBuilder();
        while (rs.next()){
            System.out.println(rs.getString("login"));
            if (rs.getString("login").substring(0,log.length()).equals(log)) {
                result.append(rs.getString("login")).append("Ќ");}
            }rs.close();ps.close();
        if(result.toString().isEmpty()||result.toString().equals("Ќ")){
            return "No_Such";
        }
        return result.toString();
    }
}