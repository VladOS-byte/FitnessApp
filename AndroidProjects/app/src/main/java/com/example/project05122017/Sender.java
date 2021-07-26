package com.example.project05122017;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


class Sender extends AsyncTask<String,Integer,String>{//класс отправления
    @Override
    protected String doInBackground(String... command0) {//вне фонового режима метод отправления на сервер переданных данных
        try {
        Socket connection = new Socket("185.228.232.167", 1014);//создание соединения с сервером по IP и порту
        ObjectOutputStream os = new ObjectOutputStream(connection.getOutputStream());//поток отправления данных
        ObjectInputStream is = new ObjectInputStream(connection.getInputStream());//поток принятия данных
        os.writeObject(command0);//отправление команды
        os.flush();//очищение потока
        if(!connection.isClosed()){//если соединение не закрыто
            String answer = is.readUTF();//принятие данных
            if (!answer.equals("")){//если данные не пусты
                os.writeObject("quit");//отправка команды выхода
                os.flush();//очистка потока
                os.close();//закрытие потока
                is.close();//закрытие потока
                return answer;//возвращение ответа сервера
            }
        }
        os.close();//закрытие потока
        is.close();//закрытие потока
    } catch (IOException ignored){}//игнорирование исключений
        return "ERROR ANSWER";//возвращение ошибки отправки
    }
}
class Sender0 extends AsyncTask<String,Integer,ArrayList<String>>{//класс отправления
    @Override
    protected ArrayList<String> doInBackground(String... command0) {//вне фонового режима метод отправления на сервер переданных данных
        ArrayList<String> answer=new ArrayList<>();
        answer.add("ERROR ANSWER");
        try {
            Socket connection = new Socket("185.228.232.167", 1014);//создание соединения с сервером по IP и порту
            ObjectOutputStream os = new ObjectOutputStream(connection.getOutputStream());//поток отправления данных
            ObjectInputStream is = new ObjectInputStream(connection.getInputStream());//поток принятия данных
            os.writeObject(command0);//отправление команды
            os.flush();//очищение потока
            if(!connection.isClosed()){//если соединение не закрыто
                answer.clear();
                answer = (ArrayList<String>) is.readObject();//принятие данных
                os.close();//закрытие потока
                is.close();//закрытие потока
            }
            os.close();//закрытие потока
            is.close();//закрытие потока
        } catch (IOException | ClassNotFoundException ignored){}//игнорирование исключений

        return answer;
    }


}