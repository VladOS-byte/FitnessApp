package com.example.project05122017;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;


public class UprActivity extends AppCompatActivity {
    ToDoDataBase database;
    SQLiteDatabase db;
    ContentValues cv;
    Cursor cursor;
    EditText et;
    GraphView graph;
    String[] s={};
    String sarray="";
    DataPoint[] dp;
    int record=0;
    int num;
    String[] weight;
    String log;
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

    @Override
    public void onBackPressed(){//метод обработки клавиши Назад
        Intent i = new Intent(UprActivity.this,WorkoutActivity.class);//объект перехода на главный экран устройства Android, приложение не завершается.
        i.putExtra("upr","0");
        startActivity(i);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upr);
        Intent intent=getIntent();
        num=Integer.parseInt(intent.getStringExtra("name"));
        findViewById(R.id.ll).setBackgroundResource(R.drawable.im2);



        database = new ToDoDataBase(this);
        db = database.getWritableDatabase();
        cv=new ContentValues();
        cursor=db.query("UprList",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            sarray=cursor.getString(cursor.getColumnIndex("upr"+num));
            try{
                s=sarray.split(" ");
            }
            catch (NullPointerException ignored){}
        }

        else{
            cv.put("ident",1);
            db.insert("UprList",null,cv);
            cv.clear();
        }
        cursor.close();db.close();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));br.readLine();
            log=br.readLine();
            br.readLine();br.readLine();br.readLine();br.readLine();
            weight=br.readLine().split(" ");
            br.close();
            br.close();
        } catch (IOException ignored) {
        }
        if (isOnline()){
        try {
            br=new BufferedReader(new InputStreamReader(openFileInput("FileToRecord0")));
            for (int i = 0; i <num; i++) {
                br.readLine();
            }
            int a=br.read();
            br.close();
            String[] w=new Sender().execute("maxOfUpr",log,Integer.toString(num)).get().split(" ");
            TextView textView=(TextView)findViewById(R.id.textView);
            textView.setText("Рейтинг: "+w[0]);
            if (a/Float.parseFloat(w[1])<0.4)
                textView.setBackgroundColor(Color.RED);
            else {
            if (a/Float.parseFloat(w[1])<0.45)
                textView.setBackgroundColor(R.color.brown);
            else{
                if (a/Float.parseFloat(w[1])<0.5)
                    textView.setBackgroundColor(R.color.orange);
                else {
                    if (a/Float.parseFloat(w[1])<0.55)
                        textView.setBackgroundColor(Color.YELLOW);
                    else {
                        if (a/Float.parseFloat(w[1])<0.6)
                            textView.setBackgroundColor(Color.GREEN);
                        else {
                            if (a/Float.parseFloat(w[1])<0.65)
                                textView.setBackgroundColor(R.color.greensv);
                            else
                                textView.setBackgroundColor(Color.MAGENTA);
                        }
                    }
                }
            }
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        }
        int r=0;
        if (s.length>30)
            r=s.length-30;
        graph=(GraphView)findViewById(R.id.graph);
        dp=new DataPoint[s.length];
        if(!(sarray==null)){//постоение графика
            if (!(sarray.equals(""))){
                for (int i = r; i < s.length; i++) {
                    dp[i] = new DataPoint(i, Integer.parseInt(s[i]));
                }
                LineGraphSeries<DataPoint> lineGraphSeries=new LineGraphSeries<>(dp);
                graph.getViewport().setScrollable(true); // enables horizontal scrolling
                graph.getViewport().setScrollableY(true); // enables vertical scrolling
                graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                graph.getViewport().setScalableY(true);
                graph.addSeries(lineGraphSeries);
            }
        }
        TextView textView0=(TextView)findViewById(R.id.tv);
        textView0.setText(getResources().getStringArray(R.array.names)[num]);
        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView1.setText("Описание: "+getResources().getStringArray(R.array.opis)[num]);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        textView2.setText(getResources().getStringArray(R.array.videos)[num]);

        et=(EditText)findViewById(R.id.editText);
        final Button buttonADD=(Button)findViewById(R.id.button);
        buttonADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//создание меню
        getMenuInflater().inflate(R.menu.activity_main_screen,menu);//добавление меню
        return super.onCreateOptionsMenu(menu);//ответ метода
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action0: {Intent intent=new Intent(UprActivity.this,FragmentActivity.class);//объект перехода к Активити настроек
                startActivity(intent);
                return true;}
            case R.id.action_settings:{Intent intent=new Intent(UprActivity.this,SettingActivity1.class);//объект перехода к Активити настроек
                startActivity(intent);
                return true;}
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void dialog1(){
        AlertDialog.Builder builder1=new AlertDialog.Builder(this);
        builder1.setMessage("Вы выполнили упражнение '"+getResources().getStringArray(R.array.names)[num]+"' " +et.getText().toString()+" раз. " +
                "Вы потратили "+Integer.parseInt(et.getText().toString())*Double.parseDouble(weight[weight.length-1])/10+" калорий.");

        builder1.setNegativeButton("Хорошо", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int numberButton) {finish();}
        });
        builder1.show();
    }
    public void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Ввести результат?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int numberButton) {
                db = database.getWritableDatabase();
                try {
                    String[] a=new String[30];
                    BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("FileToRecord0")));
                    for (int i = 0; i < 30; i++) {
                        a[i]=br.readLine();
                    }
                    record=Integer.parseInt(a[num]);
                    br.close();
                    int res=Integer.parseInt(et.getText().toString());
                    if (res>record){
                        a[num]=Integer.toString(res);
                        BufferedWriter outputStream=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRecord0",Context.MODE_PRIVATE)));
                        for (int i = 0; i < 30; i++) {
                            outputStream.write(a[i]+"\n");
                        }
                        if(isOnline()){
                            new Sender().execute("write",log,"upr"+num,et.getText().toString()).get();
                        }
                        outputStream.close();
                    }
                } catch (IOException | InterruptedException | ExecutionException e) {
                    return;
                }
                if(s.length==30)
                    sarray=sarray.substring(sarray.indexOf(" ")+1)+et.getText().toString()+" ";
                else
                    if (sarray==null)
                        sarray=et.getText().toString()+" ";
                    else
                        sarray=sarray+et.getText().toString()+" ";
                cv.clear();
                cv.put("upr"+num,sarray);
                db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
                cv.clear();
                db.close();
                dialog1();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int numberButton) {}
        });
        builder.show();
    }

}
