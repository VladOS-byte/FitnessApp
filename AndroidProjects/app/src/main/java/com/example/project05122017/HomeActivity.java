package com.example.project05122017;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.lla).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}
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
            case R.id.action0: {Intent intent=new Intent(HomeActivity.this,FragmentActivity.class);//объект перехода к Активити настроек
                startActivity(intent);
                return true;}
            case R.id.action_settings:{Intent intent=new Intent(HomeActivity.this,SettingActivity1.class);//объект перехода к Активити настроек
                startActivity(intent);
                return true;}
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onClickMainScreen(View view) {//метод обработки нажэатий на кнопки
        switch (view.getId()){
            case R.id.buttonM: {//кнопка Статистика
                Intent intent=new Intent(this,WorkoutActivity.class);//объект перехода к Активити Статистики
                intent.putExtra("upr","1");
                startActivity(intent);break;}//запуск Активити
            case R.id.button2: {//кнопка Упражнения
                Intent intent=new Intent(this,WorkoutActivity.class);//переход к Упражнениям
                intent.putExtra("upr","0");
                startActivity(intent);break;}//запуск Активити
            case R.id.buttonExit: {//кнопка Выход
                dialog();break;}//запуск метода
            case R.id.button1: {//кнопка Бег
                if(isOnline()){//проверка подключения к сети Интернет
                    Intent intent=new Intent(this,MapsActivity.class);//Активити бега
                startActivity(intent);}//запуск Активити
                else
                    Toast.makeText(getApplicationContext(),"Нет подключения к сети Интернет",Toast.LENGTH_SHORT).show();//вывод ошибки на экран
                    break;}
            default:break;
        }

    }
    @Override
    public void onBackPressed(){//метод обработки клавиши Назад
        Intent i = new Intent(Intent.ACTION_MAIN);//объект перехода на главный экран устройства Android, приложение не завершается.
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void dialog(){//метожд создания диалогового окна
        AlertDialog.Builder al=new AlertDialog.Builder(this);//создание диалогового окна
        al.setTitle("Вы уверены, что хотите выйти из аккаунта?");//сообщение
        al.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {/**/}
        });//кнопка Назад
        al.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//кнопка Да
                exit();//метод выхода из аккаунта
            }

        });
        al.show();//показ диалогового окна
    }
    private void exit() {//метод выхода
        getApplicationContext().deleteFile("FileToRegistr");//удаление файла регистрации
        getApplicationContext().deleteFile("FileToRecord0");//удаление файла рекордов
        ToDoDataBase database= new ToDoDataBase(this);//подключение к базе
        SQLiteDatabase db = database.getWritableDatabase();//открыть БД для записи
        ContentValues cv=new ContentValues();
        for (int i = 0; i < 30; i++) {
            cv.put("upr"+i,"");
        }
        db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
        cv.clear();
        database.close();
        Intent intent=new Intent(this,MainActivity.class);//объект перехода к Активити Авторитизации
        startActivity(intent);//запуск Активити
    }

}

