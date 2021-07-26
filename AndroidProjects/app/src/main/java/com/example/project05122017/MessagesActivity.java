package com.example.project05122017;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class MessagesActivity extends AppCompatActivity {
    String log,logme;
    ArrayList<String> mess;
    String[] mas;
    ArrayList<Message> arrayList=new ArrayList<>();
    ArrayAdapterForMessages aafm;
    ListView lvm;
    EditText et;
    ImageButton ib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        log=getIntent().getStringExtra("log");
        lvm=(ListView)findViewById(R.id.lvm);
        final TextView tv=(TextView)findViewById(R.id.taav);
        if(isOnline()){
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));
            br.readLine();logme=br.readLine();br.close();
            mess=new Sender0().execute("readMessages",logme,log,Integer.toString(TimeZone.getDefault().getRawOffset())).get();
            if((mess.isEmpty())){

                tv.setText("Нет сообщений");}
            else{if (mess.get(0).equals("ERROR ANSWER"))
                finish();
            else {
            for (String mes : mess) {
                arrayList.add(new Message(mes.split("Ќ")[0], logme, mes.split("Ќ")[1], mes.split("Ќ")[2], mes.split("Ќ")[3], Integer.parseInt(mes.split("Ќ")[4])));
            }

            aafm=new ArrayAdapterForMessages(this,arrayList);

            lvm.setAdapter(aafm);}}
        } catch (IOException | InterruptedException | ExecutionException ignored) {finish();}}
        else finish();
        et=(EditText)findViewById(R.id.et);
        ib=(ImageButton)findViewById(R.id.ib);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(isOnline())){
                    /*no connection*/
                    return;
                }
                if (!(et.getText().toString().isEmpty())){
                    try {
                        tv.setText("");
                        mas=new Sender().execute("sendMessage",logme,log,et.getText().toString(),Long.toString(TimeZone.getDefault().getRawOffset())).get().split("Ќ");
                        if (mas[0].equals("ERROR"))
                            finish();
                        arrayList.add(new Message(logme,logme,et.getText().toString(),mas[0],mas[1],Integer.parseInt(mas[2])));
                        et.setText("");
                    } catch (InterruptedException | ExecutionException ignored) {finish();}
                }

            }
        });
        lvm.setAdapter(new ArrayAdapterForMessages(getApplicationContext(),arrayList));
        Thread thread=new Thread(){
            public void run() {
                try {
                    Socket connection = new Socket("185.228.232.167", 1014);//создание соединения с сервером по IP и порту
                    ObjectOutputStream os = new ObjectOutputStream(connection.getOutputStream());//поток отправления данных
                    ObjectInputStream is = new ObjectInputStream(connection.getInputStream());//поток принятия данных
                    while (true){//если соединение не закрыто

                        if (isOnline()){
                        os.writeObject(new String[]{"news",logme,log,Integer.toString(TimeZone.getDefault().getRawOffset())});//отправление команды
                        os.flush();//очищение потока
                        String answer = is.readUTF();//принятие данных
                        if (answer.equals("ERROR")){
                        Toast.makeText(MessagesActivity.this, "Нет соединения с сервером", Toast.LENGTH_SHORT).show();
                        break;}
                        if (!answer.equals("NO")&&!answer.equals("")){//если данные не пусты
                            os.close();//закрытие потока
                            is.close();//закрытие потока
                            arrayList.add(new Message(log,logme,answer.split("Ќ")[0],answer.split("Ќ")[1],answer.split("Ќ")[2],1));
                        }}
                        else break;
                        Thread.sleep(3000);
                    }
                    os.close();//закрытие потока
                    is.close();//закрытие потока
                } catch (IOException | InterruptedException ignored){}//игнорирование исключений
            }
        };
        //thread.run();
    }
    public boolean isOnline(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            NetworkInfo netInfo=cm.getActiveNetworkInfo();
            return netInfo!=null&&netInfo.isConnectedOrConnecting();
        }
        catch (NullPointerException e){
            return false;
        }

    }
}
