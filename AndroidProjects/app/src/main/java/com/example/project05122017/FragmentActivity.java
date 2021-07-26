package com.example.project05122017;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FragmentActivity extends Activity {
    ListView lv;//поле для вывода списка
    ArrayList<String> rec=new ArrayList<>();//массив, который напрямую добавляется в список на экране
    ArrayList<String> rec0= new ArrayList<>();//-\\-
    Button btn;//кнопка перехода к списку рекордов среди всех пользователей
    Button btn0;//кнопка перехода к списку своих рекордов
    Boolean er=true;//переменная для хранения наличия ошибки
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
    TextView tv;
    String[] s;
    boolean a=false;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment);
        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}

        tv=(TextView)findViewById(R.id.tv);
        btn=(Button)findViewById(R.id.btn);//кнопка переход к общей статистике(ссылка на объект экрана)
        btn0=(Button)findViewById(R.id.btn0);//кнопка переход к своей статистике(ссылка на объект экрана)
        Button btn1=(Button)findViewById(R.id.btnw);//кнопка перехода к статистике веса (создание ссылки на объект экрана)
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//обработка нажатия на кнопку
                Intent intent=new Intent(FragmentActivity.this,WeightActivity.class);//переход к Активити статистики веса
                startActivity(intent);//запуск Активити
            }
        });
        if (isOnline()){//проверка подключения к сети
            try {
                s=new Sender().execute("forUpr").get().split(" ");//отправка запроса на сервер и запись в массив общих рекордов
                if (s[0].equals("ERROR ANSWER")){//если отправка запроса вызвала ошибку
                    er=false;//ошибка имеется
                }
                else{
                    for (int i = 0; i < 30; i++) {
                        rec0.add(getResources().getStringArray(R.array.names)[i]+"  |  "+s[i]);
                    }//ошибки нет, в массив передается полученный массив рекордов
                }

            } catch (InterruptedException | ExecutionException ignored) {}//игнорирование ошибок

        }
        try {
            BufferedReader fis=new BufferedReader(new InputStreamReader(openFileInput("FileToRecord0")));//открытие файла рекордов
            for (int i = 0; i < 30; i++) {//считывание последовательно всех строк данных файла
                rec.add(getResources().getStringArray(R.array.names)[i]+"  |  "+fis.readLine());//добавление в массив данных
            }
            fis.close();//закрытие потока
        } catch (IOException ignored) {//игнорирование ошибок считывания
        }
        lv=(ListView)findViewById(R.id.lv);//поле для списка(создание ссылки на объект экрана)
        lv.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,rec));//передача списка личных рекордов на экран
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//обработка нажатий на объкт списка
                if (a){Intent intent=new Intent(FragmentActivity.this,LogActivity.class);//объект перехода к Активити упражнения
                intent.putExtra("log",s[i].split("--")[1]);//передача в Активити упражнения номера упражнения в массиве
                startActivity(intent);}//запуск активити
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {//обработчик нажатия на кнопку
            @Override
            public void onClick(View view) {
                lv.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,rec));//передача списка личных рекордов на экран
                tv.setText("");
                a=false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//обработчик нажатия на кнопку
                if (isOnline()&&er){//проверка наличия ошибок и подключения к сети
                    lv.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,rec0));//передача списка общиих рекордов на экран
                    tv.setText("Логин");
                    a=true;
                }
                else
                    Toast.makeText(getApplicationContext(),"Нет подключения к серверу.",Toast.LENGTH_SHORT).show();
                    //вывод ошибки на экран
            }
        });

    }

}
