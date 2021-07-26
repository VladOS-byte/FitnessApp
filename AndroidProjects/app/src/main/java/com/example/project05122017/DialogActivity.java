package com.example.project05122017;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DialogActivity extends AppCompatActivity {
ListView lv0;
ArrayList<String> comm;
ArrayList<Communit> arrayList=new ArrayList<>();
ArrayAdapterForCommunities aafm;
String[] x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        String log;
        if (isOnline()){
            try {
                BufferedReader reader=new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));
                reader.readLine();
                log=reader.readLine();
                reader.close();
                comm=new Sender0().execute("read",log,"communities").get();
                if (comm.isEmpty()){
                    TextView tv=(TextView)findViewById(R.id.tav);
                    tv.setText("Нет доступных диалогов");}
                    else
                if(comm.get(0).equals("ERROR ANSWER")){
                    finish();}
                else{
                for (String aComm : comm) {
                    arrayList.add(new Communit(aComm.split("Ќ")[0], aComm.split("Ќ")[1], Integer.parseInt(aComm.split("Ќ")[2]), false));
                }
                aafm=new ArrayAdapterForCommunities(this,arrayList);}
                lv0=(ListView)findViewById(R.id.lv0);
                lv0.setAdapter(aafm);
                lv0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(getApplicationContext(),MessagesActivity.class);
                        intent.putExtra("log", comm.get(position * 2));
                        startActivity(intent);
                    }
                });
            } catch (IOException | ExecutionException | InterruptedException ignored) {
            }
        }
        else
            finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menui,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_settings){
        Intent intent=new Intent(DialogActivity.this,SettingActivity1.class);//объект перехода к Активити настроек
        startActivity(intent);
        return true;}
        if (item.getItemId()==R.id.sv){
            Intent intent=new Intent(DialogActivity.this,WorkoutActivity.class);//объект перехода к Активити настроек
            intent.putExtra("upr","1");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline(){//метод проверки подключения к сети Интернет (см. MainActivity)
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
