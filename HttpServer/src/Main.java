import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Main extends JFrame{
    static private ObjectInputStream is;
    static private ObjectOutputStream os;
    private String answer;


    public static void main(String[] args) {
        new Main();

    }

    private Main() {
        setLayout(new FlowLayout());
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        final JButton b1 = new JButton("Start");
        b1.addActionListener((ActionEvent e) -> {
            if (e.getSource() == b1) {
                try {
                    Socket connection = new Socket("localhost", 1013);
                    BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
                    os = new ObjectOutputStream(connection.getOutputStream());
                    is = new ObjectInputStream(connection.getInputStream());
                    while (!connection.isOutputShutdown()) {
                        if(br.ready()){
                            String[] command0=br.readLine().split(",");
                            os.writeObject(command0);
                            os.flush();
                            while (!connection.isClosed()){
                                answer=is.readUTF();
                                if(!answer.isEmpty()){
                                System.out.println(answer);break;}
                            }
                            break;
                        }
                    }

                } catch (IOException ignored) {
                }

            }
        });
        add(b1);
    }

}
