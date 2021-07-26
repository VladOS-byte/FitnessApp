package com.example.project05122017;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class WorkoutActivity extends Activity {//Активити упражнений
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String upr=getIntent().getStringExtra("upr");
        setContentView(R.layout.activity_workout);
        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}

        final ArrayAdapter<String> adapter;
        final String[] s=getResources().getStringArray(R.array.names);
        try {
            final String[] x = new Sender().execute("findLog","").get().split("Ќ");

        if(upr.equals("0")){
            adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,s);
        }else{

            adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,x);
        }
        ListView listView=(ListView)findViewById(R.id.lv);//поле для списка упражнений (создание ссылки на объект экрана)
        listView.setAdapter(adapter);//передача адаптера с названиями упражнений на экран
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//обработка нажатий на объкт списка
                Intent intent;
                if(upr.equals("0")) {
                    intent = new Intent(WorkoutActivity.this, UprActivity.class);//объект перехода к Активити упражнения
                    for (int j = 0; j < s.length; j++) {
                        if (s[j].equals(adapter.getItem(i))){
                            intent.putExtra("name", "" + j);//передача в Активити упражнения номера упражнения в массиве
                            break;}
                    }

                }else{
                    intent=new Intent(getApplicationContext(),LogActivity.class);
                intent.putExtra("log",adapter.getItem(i));}
                startActivity(intent);//запуск активити
            }
        });
        SearchView sv=(SearchView)findViewById(R.id.sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });} catch (InterruptedException | ExecutionException ignored) {finish();}
    }
    @Override
    public void onBackPressed(){//метод обработки клавиши Назад
        Intent i = new Intent(WorkoutActivity.this,HomeActivity.class);//объект перехода на главный экран устройства Android, приложение не завершается.
        startActivity(i);
    }
}
