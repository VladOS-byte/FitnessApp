package com.example.project05122017;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class WeightActivity extends AppCompatActivity {//Активити статистики веса
    EditText et;//поле для ввода новых данных
    Button btn;//кнопка отправки данных
    GraphView graph;//поле для построения графика
    String[] file=new String[7];//массив для данных из файла
    ArrayList<String> sar=new ArrayList<>();//список для веса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}

        et=(EditText)findViewById(R.id.editText);//поле для ввода данных (ссылка на объект экрана)
        btn=(Button)findViewById(R.id.button);//кнопка отправки новых данных(ссылка на объект экрана)
        graph=(GraphView)findViewById(R.id.graph0);//поле для построения графика
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));//открытие потока для чтения файла регистрации
            for (int i = 0; i < 7; i++) {//считывание всего файла
                file[i]=br.readLine();//добавление считанного в массив
            }
            sar.addAll(Arrays.asList(file[6].split(" ")).subList(0, file[6].split(" ").length));//добавление в список веса
            br.close();//закрытие потока
            DataPoint[] dp=new DataPoint[sar.size()];//массив точек для графика
        if(!(file[6].equals(""))&&!(file[6].equals(" "))){//если список не пуст
            for (int i = 0; i < sar.size(); i++) {//добавление всех элементов списка
                dp[i] = new DataPoint(i, Double.parseDouble(sar.get(i)));//добавление новой точки в массив
            }
            LineGraphSeries<DataPoint> lineGraphSeries=new LineGraphSeries<>(dp);//передача массива точек в список точек(адаптор)
            graph.getViewport().setScalable(true); // горизантальное приближение (ZOOM) и пролистывание
            graph.getViewport().setScalableY(true); // вертикальное приближение (ZOOM) и пролистывание
            graph.addSeries(lineGraphSeries);//передача списка точек в объект экрана (построение графика)
        }

        } catch (IOException ignored) {}//игнорирование ошибок при чтении
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//обработка нажатия на кнопку отправить
                dialog();//запуск метода
            }
        });
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
    public void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);//создание диалогового окна
        builder.setMessage("Ввести данные?");//сообщение
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int numButton) {//кнопка согласия
                if (isOnline()){//проверка подключения
                    sar.add(et.getText().toString());//добавление в список нового элемента
                    StringBuilder ask= new StringBuilder();//строковая переменная для составления запроса на сервер
                    for (int i = 0; i < sar.size(); i++) {//составление строки запроса
                        ask.append(sar.get(i)).append(" ");//запись данных через разделитель
                    }
                    String answer="";//строка для ответа сервера
                    try {
                        answer = new Sender().execute("write",file[1],"weight",ask.toString()).get();//отправление запроса на сервер и получение ответа
                        if(answer.equals("OK")){//если ответ сервера удовлетворительный
                            BufferedWriter fis=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRegistr",Context.MODE_PRIVATE)));//открытие потока для записи файла Регистрации
                            for (int i = 0; i < 6; i++) {//сначала запись неизмененных, ранее извлеченных данных
                                fis.write(file[i]+"\n");//запись данных с переходом на новую строку
                            }
                            fis.write(ask.toString()+"\n");//запись строки веса
                            fis.close();//закрытие потока
                            Toast.makeText(getApplicationContext(),"Отправлено",Toast.LENGTH_SHORT).show();//вывод сообщения на экран
                            finish();//закрытие Активити и возврат к FragmentActivity
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Не отправлено",Toast.LENGTH_SHORT).show();//вывод сообщения
                    } catch (InterruptedException | ExecutionException | IOException ignored) {}//игнорирование ошибок отправки данных на сервер и считывания
                }
                else
                    Toast.makeText(getApplicationContext(),"Отсутствует подключение к сети Интернет",Toast.LENGTH_SHORT).show();//вывод сообщения
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {//кнопка Нет, обработка нажатия
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}//закрытие окна
        });
        builder.show();//показ диалогового окна
    }
}
