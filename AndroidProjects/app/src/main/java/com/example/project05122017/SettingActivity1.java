package com.example.project05122017;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

public class SettingActivity1 extends AppCompatActivity{//Активити настроек
    Button btn;//кнопка сохранения
    int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
    int i=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}
        btn=(Button)findViewById(R.id.button);//кнопка сохранения(ссылка на объект экрана)
        Spinner spin=(Spinner)findViewById(R.id.spin);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i=position;
                LinearLayout ll=(LinearLayout)findViewById(R.id.ll);
                ll.setBackgroundResource(images[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void onClick(View view) {//обработка нажатий
        if(view.getId()==R.id.button)
        try {
            String log;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));
            if((log=bufferedReader.readLine())!=null) {
                String[] s=new String[7];
                s[1]=log;
                for (int i = 1; i < 7; i++) {
                    s[i]=bufferedReader.readLine();
                }
                bufferedReader.close();
                BufferedWriter outputStream=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRegistr", Context.MODE_PRIVATE)));
                EditText et0 = (EditText) findViewById(R.id.editText);
                EditText et1 = (EditText) findViewById(R.id.editText1);
                EditText et3 = (EditText) findViewById(R.id.editText3);
                Boolean answer=true;
                if (!et0.getText().toString().equals("")) {
                    if(new Sender().execute("write", log, "pass", et0.getText().toString()).get().equals("OK"))
                        s[2]=et0.getText().toString();
                    else
                        answer=false;
                }
                if (!et1.getText().toString().equals("")&&answer) {
                    if(new Sender().execute("write", log, "mail", et0.getText().toString()).get().equals("OK"))
                        s[3]=et1.getText().toString();
                    else
                        answer=false;
                }
                if (!et3.getText().toString().equals("")&&answer) {
                    if(new Sender().execute("write", log, "hight", et0.getText().toString()).get().equals("OK"))
                        s[5]=et3.getText().toString();
                    else
                        answer=false;}
                for (int i = 0; i < 7; i++) {
                    outputStream.write(s[i]+"\n");
                }
                if (!answer){
                    TextView tv=(TextView)findViewById(R.id.tv0);
                    tv.setText("Ошибка отправки на сервер");
                }
                outputStream.close();
                if(i>-1){BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(openFileOutput("fileBackground",Context.MODE_PRIVATE)));
                bw.write(Integer.toString(i)+"\n");
                bw.close();}
                Intent i = new Intent(SettingActivity1.this,HomeActivity.class);//объект перехода на главный экран устройства Android, приложение не завершается.
                startActivity(i);
            }}  catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
