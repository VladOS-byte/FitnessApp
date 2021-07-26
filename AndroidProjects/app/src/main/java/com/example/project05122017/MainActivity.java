package com.example.project05122017;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

//открытие библиотек
public class MainActivity extends Activity {//Активити* для входа, авторитизации // *Активити: далее Activity — это компонент приложения, который выдает экран, и с которым пользователи могут взаимодействовать для выполнения каких-либо действий.
    EditText pass;//поле для ввода пароля
    EditText log;//поле для ввода логина
    String name;//имя пользоватеяля
    TextView tv;//поле для вывода ошибок
    Button buttonNew;//кнопка для создания нового пользователя
    Button buttonLogIn;//кнопка для авторитизации
    String realLog = "";//логин пользователя, считываемый из файла, с сервера
    String realPass = "";//пароль пользователя, считываемый из файла, с сервера
    boolean save = false;//переменная для кнопки "запомнить меня" (положение вкл/выкл=true/false)
    String mail;//e-mail пользователя, считываемый из файла, с сервера
    int hight;//рост пользователя, считываемый из файла, с сервера
    String weight;//вес пользователя, считываемый из файла, с сервера
    Switch sw;//кнопка "запомнить меня"
    int a = 0;//переменная для кнопки "запомнить меня" (кол-во нажатий
 Cipher cipher;

    final String filename = "FileToRegistr";//имя файла регистрации
    int[] images=new int[]{R.drawable.im,R.drawable.foot,R.drawable.cosmos,R.drawable.images};
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SecretKeySpec secretKeySpec=new SecretKeySpec("VladOScriptkeyss".getBytes(),"AES");
        try {
            try {
                cipher=Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        } catch (InvalidKeyException ignored) {

        }


        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput("fileBackground")));
            String b=br.readLine();
            br.close();
            if(Integer.parseInt(b)>=0){
                findViewById(R.id.ll).setBackgroundResource(images[Integer.parseInt(b)]);}
            else
                try {

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("fileBackground", Context.MODE_PRIVATE)));
                    bw.write("0");bw.close();findViewById(R.id.ll).setBackgroundResource(images[0]);
                } catch (IOException ignored) {}
        } catch (IOException i) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("fileBackground", Context.MODE_PRIVATE)));
                bw.write("0");bw.close();
                findViewById(R.id.ll).setBackgroundResource(images[0]);
            } catch (IOException ignored) {}
        }


        ShimmerTextView stv=(ShimmerTextView)findViewById(R.id.shim);//поле названия приложения
        Shimmer shimmer=new Shimmer().setDirection(Shimmer.ANIMATION_DIRECTION_RTL).setDuration(1000);//объект переливания текста
        shimmer.start(stv);//запуск переливания
        log=(EditText)findViewById(R.id.editText1);//поле для ввода логина(ссылка на объект экрана)
        pass=(EditText)findViewById(R.id.editText0);//поле для ввода пароля(ссылка на объект экрана)
        log.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {//реализация перехода к вводу пароля по нажатию клавиши Enter, слушатель нажатия на клавиатуру
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER))//проверка нажатия на клавиатуру и является ли клавиша клавиатуры Enter
                {
                    pass.setSelection(pass.getText().length());//переход указателя к последнему символу пароля
                    return true;//ответ метода (верно) => переход
                }
                return false;//ответ метода (неверно)
            }
        });
        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {//аналогично для поля ввода пароля
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (i == KeyEvent.KEYCODE_ENTER))
                {
                    try {LogIn();}//переход к методу авторитизации
                    catch (InterruptedException ignored) {} catch (BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        //из файла
        sw=(Switch)findViewById(R.id.switch0);//клавиша "запомнить меня"
        try{
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(openFileInput(filename)));//поток для чтения файла регистрации (будет открыт, если существует. Иначе - исключение
                if(Objects.equals(bufferedReader.readLine(), "1")){//если файл не нужно стереть
                realLog=bufferedReader.readLine();//считывание логина и внос в переменную
                log.setText(realLog);//передача полю ввода пароля считанного логина (упрощение повторной авторитизации для пользователя)
                log.setSelection(log.getText().length());//переход указателя в конец логина
                realPass=bufferedReader.readLine();//считывание пароля и внос в переменную
                name=bufferedReader.readLine();}//считывание имени и внос в переменную
                else
                    getApplicationContext().deleteFile(filename);//удаляем файл регистрации
            bufferedReader.close();//закрытие потока ввода
        } catch (IOException ignored) {

        }//игнорирование исключений(ошибок) - они могут возникнуть, если файл не сущетсвует. Тогда мы просто пропускаем выше пеерчисленные действия
        tv=(TextView)findViewById(R.id.tv0);//поле для вывода ошибок(ссылка на объект экрана)
        buttonNew = (Button) findViewById(R.id.buttonNew);//кнопка регистрации(ссылка на объект экрана)
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);//конпка автоитизации(ссылка на объект экрана)
    }
    public boolean isOnline(){//метод - проверка подключеня к сети
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);//объект класса для проверки подключения к сети Интернет
        try{
            NetworkInfo netInfo=cm.getActiveNetworkInfo();//статус подключения к сети
            return netInfo!=null&&netInfo.isConnectedOrConnecting();//true, если есть подключение к сети
        }
        catch (NullPointerException e){//исключение ( поытка присвоения некорректных данных)
            return false;
        }

    }
    public void onClickMain(View view) throws InterruptedException, BadPaddingException, IllegalBlockSizeException {//метод реализации нажатий на кнопки
        switch (view.getId()){//перечисление всех кнопок (по ID)
            case R.id.switch0://"Запомнить меня"
            {
                a++;//+1 нажатие на клавишу
                save=a%2!=0;//присвоить true, если а нечетно
                break;//завершение прохода по вариантам (ID)
            }
            case R.id.buttonLogIn:{//клавиша авторитизации
                LogIn();//запуск метода
                break;}//завершение прохода по вариантам (ID)
            case R.id.buttonNew:{//клавиша регистрации
                if(isOnline()){//проверка подключения
                    ToDoDataBase database= new ToDoDataBase(this);//подключение (создание БД) к БД результатов на устройстве
                    SQLiteDatabase db = database.getWritableDatabase();//открытие БД для записи
                    ContentValues cv=new ContentValues();
                    for (int i = 0; i < 30; i++) {
                        cv.put("upr"+i,"");
                    }
                    db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
                    cv.clear();
                    database.close();//отключение
                    getApplicationContext().deleteFile("FileToRecord0");//удаление файла рекордов
                    getApplicationContext().deleteFile("FileToRegistr");//удаление файла регистрации
                    Intent intent1 = new Intent(this, RegistrationActivity.class);//объект для перехода к Активити регистрации
                    startActivity(intent1);}
                else
                    tv.setText("Нет подключения к сети Интернет! Пожалуйста, попробуйте снова после подключения.");//Вывод ошибки на экран
                break;}//завершение прохода по вариантам (ID)
            default:
                break;
        }
    }

    public void LogIn() throws InterruptedException, BadPaddingException, IllegalBlockSizeException {//метод авторитизации
        buttonLogIn.setClickable(false);//запрет нажатия на клавишу снова
        String[] s;//массив полученных данных с сервера
        if (!log.getText().toString().equals("")&& !log.getText().toString().contains(";")){//если логин не введен или используется запрещенный символ (разделитель)
            if (isOnline()){//проверка подключения
                try {
                    s=new Sender().execute("readAll",log.getText().toString()).get().split(";");//отправка через вспомогательный объект класса Sender запроса на вывод всей информации по введенному логину. Разделение полученных данных и передача их в массив
                    if (s[0].equals("ERROR ANSWER")){//есил сервер не ответил (см. Sender)
                        tv.setText("Сервер не доступен");//вывод ошибки на экран
                        buttonLogIn.setClickable(true);//клавиша снова доступна для нажатия
                        return;//завершение метода
                    }
                    if (s[0].equals("ERROR")){//сервер не нашел нужного логина
                        tv.setText("Не сущетсвует введенного логина");//-\\-
                        buttonLogIn.setClickable(true);//-\\-
                        return;//-\\-
                    }
                    realLog=s[0];//получение логина из массива
                    realPass=s[1];//получение пароля из массива
                    mail=s[2];//получение e-mail(эл. почты) из массива
                    weight=s[3];//получение веса из массива
                    hight=Integer.parseInt(s[4]);//получение высоты из массива
                    name=s[5];//получение имени из массива
                    try{
                        BufferedReader buf=new BufferedReader(new InputStreamReader(openFileInput("FileToRegistr")));//поток для чтения файла регистрации (будет открыт, если существует. Иначе - исключение)
                        buf.readLine();//пропуск флажка сохранения
                        if (log.getText().toString().equals(buf.readLine())){//проверка эквивалентности введенного логина и логина из файла(вход другого пользователя)
                            buf.close();//закрытие потока
                            buf=new BufferedReader(new InputStreamReader(openFileInput("FileToRecord0")));//поток для чтения файла рекордов (будет открыт, если существует. Иначе - исключение)
                            StringBuilder ask= new StringBuilder();//переменная для набольших введенных когда-либо данных
                            for (int i = 0; i < 30; i++) {//цикл для 30 упражнений
                                String b=buf.readLine();//считанный рекорд
                                if (Integer.parseInt(b)<=Integer.parseInt(s[i+6]))//если считанный рекорд меньше, чем на сервере(=>на сервере более актуальные данные)
                                    ask.append(s[i + 6]).append(",");//добавление рекорда в строку
                                else//в файле более актуальные данные или данные эквивалентны
                                    ask.append(b).append(",");//добавление рекорда в строку, разделение
                            }
                            buf.close();//закрытие потока
                            String[] mas= ask.toString().split(",");//создание массива для рекордов
                            //можно было изначально присваивать данные в массив. Сделано так, во избежание ошибок с методом Join формирования строки (вставки прожмежточных символов между объектами массива) для отправки на сервер. Метод Join не предустановлен и пожтому не будет использоваться в моем приложении
                            //возможность упрощения кода учтена
                            new Sender().execute("writeAllRecords",log.getText().toString(), ask.toString()).get();//отправка всех рекордов по логину на сервер
                            BufferedWriter pw=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRecord0",Context.MODE_PRIVATE)));//открытие потока для перезаписи в файл рекордов
                            for (int i = 0; i < 30; i++) {//проход по всем 30 упражнениям
                                pw.write(mas[i]+"\n");//запись в файл и переход на новую строку
                            }
                            pw.close();//закрытие потока
                        }
                        else {
                            buf.close();
                            ToDoDataBase database= new ToDoDataBase(this);//подключение к базе
                            SQLiteDatabase db = database.getWritableDatabase();//открыть БД для записи
                            ContentValues cv=new ContentValues();
                            for (int i = 0; i < 30; i++) {
                                cv.put("upr"+i,"");
                            }
                            db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
                            cv.clear();
                            database.close();
                            BufferedWriter pw=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRecord0",Context.MODE_PRIVATE)));//открытие потока для записи в файл для рекордов
                            for (int i = 0; i < 30; i++) {//проход по всем 30 упражнениям
                                pw.write(s[i+6]+"\n");//запись в файл и переход на новую строку
                            }
                            pw.close();//закрытие потока записи
                        }
                    }
                    catch (FileNotFoundException e){//исключение - файл не найден => первый вход существующего пользователя
                        BufferedWriter pw=new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRecord0",Context.MODE_PRIVATE)));//открытие потока для записи в файл для рекордов
                        for (int i = 0; i < 30; i++) {//проход по всем 30 упражнениям
                            pw.write(s[i+6]+"\n");//запись в файл и переход на новую строку
                        }
                        pw.close();//закрытие потока записи
                        ToDoDataBase database= new ToDoDataBase(this);//подключение к базе
                        SQLiteDatabase db = database.getWritableDatabase();//открыть БД для записи
                        ContentValues cv=new ContentValues();
                        for (int i = 0; i < 30; i++) {
                            cv.put("upr"+i,"");
                        }
                        db.update("UprList",cv,"id=?",new String[]{Integer.toString(1)});
                        cv.clear();
                        database.close();
                    }
                    catch (ExecutionException ignored){}//игнорирование ошибок отправки, чтения и записи - корректные данные ошибок не вызывают
                } catch (ExecutionException | IOException ignored) {}
            }
            else{
                if (realLog.equals("")){//если нет подключения к сети и файл регистрации пуст - первый вход
                    tv.setText("Нет подключения к сети Интеренет.");//вывод сообщения на экран
                    buttonLogIn.setClickable(true);//кнопка доступна
                    return;}//завершение метода
            }
        }
        else{
            tv.setText("Поле логина должно быть заполнено и не должно содержать символа ';'");
            buttonLogIn.setClickable(true);//кнопка доступна для нажатия
            return;}//вывод ошибки
        if (log.getText().toString().equals(realLog)){
            if(Arrays.toString(cipher.doFinal(pass.getText().toString().getBytes())).equals(realPass)) {
                //отдельная проверка логин  пароль
                try {
                    getApplicationContext().deleteFile("FileToRegistr");
                    BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(openFileOutput("FileToRegistr", Context.MODE_APPEND)));//поток для записи в файл
                    if (save) {//нужно ли сохранять
                        outputStream.write("1" + "\n");//файл удалится, если 0
                    } else{
                        outputStream.write("0" + "\n");}
                    outputStream.write(realLog + "\n" + realPass + "\n" + name + "\n" + mail + "\n" + hight + "\n" + weight + "\n");
                    //последовательная запись персональных данных в файл регистрации
                    outputStream.close();//закрытие потока
                    Intent intent = new Intent(this, HomeActivity.class);//объект перехода к домашнему Активити
                    startActivity(intent);//запуск Активити
                    Toast.makeText(getApplicationContext(), "Привет, " + name + "!", Toast.LENGTH_SHORT).show();//Приветствие
                } catch (IOException ignored) {
                }//игнорирование ошибок записи
            }
            else{
                tv.setText("Неверный пароль! Попробуйте еще раз!");//вывод ошибки
            }
        }
        else{
            tv.setText("Неверный логин! Попробуйте еще раз!");//вывод ошибки(нужно, если пользователь оффлайн)
        }
        buttonLogIn.setClickable(true);//кнопка доступна для нажатия
    }
}
