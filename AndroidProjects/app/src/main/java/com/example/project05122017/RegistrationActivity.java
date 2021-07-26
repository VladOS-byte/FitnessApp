package com.example.project05122017;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

//открытие библиотек
public class RegistrationActivity extends Activity {//Активити регистрации
    int a=0;//переменная для кнопки "запомнить меня" (кол-во нажатий)
    Boolean save=false;//переменная для кнопки "запомнить меня" (положение вкл/выкл=true/false)
    EditText[] editTexts=new EditText[6];//массив полей ввода для персональных данных
    Button buttonOk;//кнопка ок - (зарегистрировать)
    Cipher cipher;
    TextView tv;//поле для вывода ошибок
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        SecretKeySpec secretKeySpec=new SecretKeySpec("VladOScriptkeyss".getBytes(),"AES");
        try {
            try {
                cipher=Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException ignored) {

        }


        try{
            int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();br.close();
            findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);
        } catch (IOException ignored) {}

        tv=(TextView)findViewById(R.id.tv1);
        TextView textView=(TextView)findViewById(R.id.textView);//поле для текста
        textView.setText(R.string.polite);//текст
        Linkify.addLinks(textView, Linkify.ALL);//возможность нажать на текст(ссылка)
        editTexts[0] = (EditText) findViewById(R.id.editText0);//поле для ввода логина(ссылка на объект экрана)
        editTexts[1] = (EditText) findViewById(R.id.editText1);//поле для ввода пароля(ссылка на объект экрана)
        editTexts[2] = (EditText) findViewById(R.id.editText2);//поле для ввода роста(ссылка на объект экрана)
        editTexts[3] = (EditText) findViewById(R.id.editText3);//поле для ввода веса(ссылка на объект экрана)
        editTexts[4] = (EditText) findViewById(R.id.editText4);//поле для ввода имени(ссылка на объект экрана)
        editTexts[5] = (EditText) findViewById(R.id.editText5);//поле для ввода эл. почты(ссылка на объект экрана)
        editTexts[0].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {//реализация перехода к вводу пароля по нажатию клавиши Enter, слушатель нажатия на клавиатуру
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {//проверка нажатия на клавиатуру и является ли клавиша клавиатуры Enter
                    editTexts[1].setSelection(editTexts[1].getText().length());//переход указателя к последнему символу пароля
                    return true;}//ответ метода (верно) => переход
                return false;}
        });//ответ метода (неверно)
        editTexts[1].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {editTexts[4].setSelection(editTexts[4].getText().length());return true;}
                return false;}
        });//-\\-к вводу имени
        editTexts[2].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {editTexts[3].setSelection(editTexts[3].getText().length());return true;}
                return false;}
        });//-\\-к вводу веса
        editTexts[3].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {editTexts[5].setSelection(editTexts[5].getText().length());return true;}
                return false;}
        });//-\\-к вводу эл. почты
        editTexts[4].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {editTexts[2].setSelection(editTexts[2].getText().length());return true;}
                return false;}
        });//-\\-к вводу роста
        buttonOk = (Button) findViewById(R.id.buttonOk);//кнопка Зарегистрировать(ссылка на объект экрана)
        buttonOk.setClickable(false);//запрет на нажатие- возможность нажать на кнопку только после нажатия на кнопку Я согласен
        editTexts[5].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {if(buttonOk.isClickable()&&keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER)) {
                try {
                    Registr();//-\\-запуск метода регистрации
                } catch (BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                return true;
            }
                return false;}
        });

    }
    public boolean isOnline() {//метод проверки подключения к сети (см. MainActivity - Активити авторитизации
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (NullPointerException e) {
            return false;
        }
    }
    public void Registrate(View v) throws BadPaddingException, IllegalBlockSizeException {//метод реализации нажатий на объекты экрана
        switch (v.getId()) {//перечисление всех кнопок (по ID)
            case R.id.textView:{
                AlertDialog.Builder adb = new AlertDialog.Builder(this);//создание диалогового окна
                adb.setTitle("Политика Конфиденциальности");// заголовок
                adb.setMessage(R.string.Privacy);// сообщение
                adb.setPositiveButton("Я согласен с Политикой Конфиденциальности", new DialogInterface.OnClickListener() {// кнопка положительного ответа
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buttonOk.setClickable(true);//кнопка для регистрации доступна для нажатия
                    }
                });
                adb.setNegativeButton("Назад",new DialogInterface.OnClickListener(){// кнопка отрицательного ответа
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {/*выход из диалогового окна*/ }
                });
                adb.show();//показать окно
                break;//завершение прохода по вариантам (ID)
            }
            case R.id.switch0:{
                a++;//+1 нажатие на клавишу
                save=a%2!=0;//присвоить true, если а нечетно
                break;
            }
            case R.id.buttonOk:{
                Registr();//запуск метода регистрации
                break;
                }}

        }
    public void Registr() throws BadPaddingException, IllegalBlockSizeException {//метод Регистрации
        Boolean er=true;//переменная для хранения наличия ошибок
        String login = editTexts[0].getText().toString();//переменная для хранения логина
        String password = editTexts[1].getText().toString();//переменная для хранения пароля
        int hight = Integer.parseInt(editTexts[2].getText().toString());//переменная для хранения роста
        String weight = editTexts[3].getText().toString();//переменная для хранения веса
        String name = editTexts[4].getText().toString();//переменная для хранения имени
        String mail = editTexts[5].getText().toString();//переменная для хранения эл. почты
        if (editTexts[1].getText().toString().length()<8) {//проверка длины пароля
            tv.setText("Пароль должен иметь не менее 8 символов");//для достижения сложности пароля, он должен быть >8 символов
            editTexts[1].setBackgroundResource(R.color.colorAccent);//поле становится красным
            return;//прерывается регистрация
        }
        if(!editTexts[5].getText().toString().contains("@")){//проверка наличия "собаки"
            editTexts[5].setBackgroundResource(R.color.colorAccent);//поле становится красным
            tv.setText("Некорректный ввод e-mail");
            return;//прерывается регистрация
        }
        for (int i = 0; i < 5; i++) {//перебор всех полей для вода
            if(editTexts[i].getText().toString().contains(";")||editTexts[i].getText().toString().contains(" ")||editTexts[i].getText().toString().contains("-")){
                tv.setText("Поле не должно содержать символы ';','-',' '!");//вывод ошибки
                editTexts[i].setBackgroundResource(R.color.colorAccent);//поле становится красным
                return;//прерывается регистрация
            }
            if (editTexts[i].getText().toString().equals(""))//проверка на пустоту поля
            {
                er=false;//ошибка
                editTexts[i].setBackgroundResource(R.color.colorAccent);//если поле пустое, оно становится красным
            }
        }
        if(!er){
            return;//если хотя бы одно поле не заполнено - регистрация прерывается
        }
        else{
            tv=(TextView)findViewById(R.id.tv1);//поле для вывода ошибок
            if (isOnline()){//проверка подключения к сети
                String s="";//переменная для ответа сервера
                try {
                    s = new Sender().execute("writeNEW",login, Arrays.toString(cipher.doFinal(password.getBytes())),mail,weight,Integer.toString(hight),name).get();//запрос на сервер о записи нового пользователя
                    if(s.equals("ERROR")){//если сервер выдает ошибку
                        tv.setText("Логин уже используется другим пользователем");//вывод ошибки
                        return;//завершение метода регистрации
                    }
                    if(s.equals("ERROR ANSWER")){//если при отправке в объекте класса Sender произошла ошибка
                        tv.setText("Север не доступен");//вывод ошибки
                        return;//завершение метода регистрации
                    }
                } catch (InterruptedException | ExecutionException e) {//исключение вызванное ошибкой передачи данных объекту класса Sender или серверу
                    tv.setText("Сервер не доступен");//вывод ошибки
                    return;//завершение метода регистрации
                }
            }
            else
                tv.setText("Нет подключения к сети Интернет! Пожалуйста, попробуйте снова после подключения.");//вывод ошибки
            try {
                BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRecord0", Context.MODE_APPEND)));//поток для записи в файл рекордов
                for (int i = 0; i < 30; i++) {//запись всех 30 рекордов
                    outputStream.write(Integer.toString(0)+"\n");//запись в файл нуля(достижений пока нет)
                }
                outputStream.close();//закрытие потока
            } catch (IOException ignored) {//игнорирование ошибок записи
            }
            try {
                BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRegistr", Context.MODE_APPEND)));//поток для записи в файл регистрации
                if(save){//нужно ли сохранять персональные данные в файл
                    outputStream.write("1"+"\n");
                }
                else
                    outputStream.write("0"+"\n");//если ноль, то все данные сотрутся
                outputStream.write(login+"\n"+ Arrays.toString(cipher.doFinal(password.getBytes())) +"\n"+name+"\n"+mail+"\n"+hight+"\n"+weight);//последовательная запись перс. данных в файл с переходом на новую строчку
                outputStream.close();//закрытие потока ввода
            } catch (IOException ignored){}//игнорирование ошибок ввода
            ToDoDataBase database= new ToDoDataBase(this);//подключение к базе
            SQLiteDatabase db = database.getWritableDatabase();//открыть БД для записи
            ContentValues cv=new ContentValues();
            for (int i = 0; i < 30; i++) {
                cv.put("upr"+i,"");
            }
            db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
            cv.clear();
            database.close();
            Toast.makeText(getApplicationContext(), "Привет, "+name+"!", Toast.LENGTH_LONG).show();//приветствие
            Intent intent1 = new Intent(this, HomeActivity.class);//объект перехода к домашнему Активити
            startActivity(intent1);//запуск Активити
        }}
    }