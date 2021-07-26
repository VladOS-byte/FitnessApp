package com.example.project05122017;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        final String log=getIntent().getStringExtra("log");

        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}

        TextView tv=(TextView)findViewById(R.id.tv0);
        TextView tv1=(TextView)findViewById(R.id.btn);

        ListView lv=(ListView)findViewById(R.id.lv);
        ArrayList<String> arrayList=new ArrayList<>();
        try {
            String[] x=new Sender().execute("readAll",log).get().split(";");
            if (x[0].equals("ERROR ANSWER")){
                Toast.makeText(getApplicationContext(),"Ошибка подключения к серверу",Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {tv.setText(log);
            tv1.setText(x[5]);
                for (int i = 6; i < 36; i++) {
                    arrayList.add(getResources().getStringArray(R.array.names)[i-6] + "  |  " + x[i]);
                }   }
        } catch (InterruptedException | ExecutionException ignored) {
            Toast.makeText(getApplicationContext(),"Ошибка подключения к серверу",Toast.LENGTH_SHORT).show();
            finish();
        }

        lv.setAdapter(new ArrayAdapter<String>(LogActivity.this,android.R.layout.simple_list_item_1,arrayList));
    }

}
