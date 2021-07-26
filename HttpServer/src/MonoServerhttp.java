import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class MonoServerhttp implements Runnable {
    private static Socket clientDialog;
    MonoServerhttp(Socket connection) {
        clientDialog=connection;
    }

    @Override
    public void run(){
        try {
            ObjectInputStream in = new ObjectInputStream(clientDialog.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientDialog.getOutputStream());
            while (!clientDialog.isClosed()) {
                String[] clientCommand = (String[]) in.readObject();
                if (clientCommand[0].equals("quit")){
                    in.close();
                    out.close();
                    clientDialog.close();
                    break;}
                if(clientCommand[0].equals("write")){
                    String serveranswer = "";
                    try {
                        serveranswer =StartServer.write(clientCommand[1], clientCommand[2],clientCommand[3]);
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        if (!(serveranswer.equals("")))
                            out.writeUTF(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("writeNEW")){
                    clientCommand=Arrays.copyOfRange(clientCommand, 1,clientCommand.length);
                    String s=StartServer.writeNEW(clientCommand);
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeUTF(s);
                    }
                    break;
                }
                if(clientCommand[0].equals("read")){
                    String serveranswer = "";
                    try {
                        serveranswer =StartServer.read(clientCommand[1], clientCommand[2]);
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeUTF(serveranswer);
                    }break;}
                    if(clientCommand[0].equals("readAll")){
                        String serveranswer = "";
                        try {
                            serveranswer =StartServer.readAll(clientCommand[1]);
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                        long start=System.currentTimeMillis();
                        while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                            out.flush();
                            out.writeUTF(serveranswer);
                        }
                        break;
                }
                if(clientCommand[0].equals("writeAllRecords")){
                    try {
                        StartServer.writeAllRecords(clientCommand[1],clientCommand[2]);
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeUTF("OK");
                    }
                    out.flush();
                    break;}
                if(clientCommand[0].equals("maxOfUpr")){
                    String serveranswer = "";
                    serveranswer=StartServer.maxOfUpr(clientCommand[1],Integer.parseInt(clientCommand[2]));
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        if (!(serveranswer.equals("")))
                            out.writeUTF(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("forUpr")){
                    String serveranswer = "";
                    serveranswer=StartServer.forUpr();
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        if (!(serveranswer.equals("")))
                            out.writeUTF(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("findLog")){
                    String serveranswer = "";
                    serveranswer=StartServer.find(clientCommand[1]);
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeUTF(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("sendMessage")){
                    String serveranswer = "";
                    serveranswer=DataBaseForCommunication.send(clientCommand[1], clientCommand[2],clientCommand[3],Integer.parseInt(clientCommand[4]));
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeUTF(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("readMessages")){
                    ArrayList<String> serveranswer=DataBaseForCommunication.readMessages(clientCommand[1],clientCommand[2],Integer.parseInt(clientCommand[3]));
                    long start=System.currentTimeMillis();
                    while (!clientDialog.isClosed()&&System.currentTimeMillis()-start<5000){
                        out.flush();
                        out.writeObject(serveranswer);
                    }
                    break;
                }
                if(clientCommand[0].equals("news")){
                    String serveranswer=DataBaseForCommunication.news(clientCommand[1],clientCommand[2],Integer.parseInt(clientCommand[3]));
                    long start=System.currentTimeMillis();
                    while(!clientDialog.isClosed()&&System.currentTimeMillis()-start<3000){
                        out.flush();
                        if (!(serveranswer.isEmpty()))
                            out.writeUTF(serveranswer);
                    }
                }
            }

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
