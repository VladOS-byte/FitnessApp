
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try  {
            ServerSocket ss = new ServerSocket(1013);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StartServer.conn();
            StartServer.create();
            DataBaseForCommunication.conn();
            DataBaseForCommunication.create();
            while (!ss.isClosed()) {
                if (br.ready()) {
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("deleteTable")) {
                        StartServer.delete();
                        ss.close();
                        break;
                    }
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        ss.close();
                        break;
                    }
                }
                Socket connection = ss.accept();
                service.execute(new MonoServerhttp(connection));
            }
            StartServer.fullTable();
            service.shutdown();
        } catch (IOException | SQLException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}