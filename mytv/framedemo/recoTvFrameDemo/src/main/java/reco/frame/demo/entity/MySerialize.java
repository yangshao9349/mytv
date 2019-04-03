package reco.frame.demo.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import model.Menu;

public class MySerialize {

    public static String serialize(Object obj) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    /**
     * 反序列化对象
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerialization(String str) throws IOException,
            ClassNotFoundException {

        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        Object obj = (Object) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();

        return obj;
    }

    public static String getObject(String key, Context content) {
        SharedPreferences sp = content.getSharedPreferences("db", 0);
        return sp.getString(key, null);
    }

    public static void saveObject(String key, Context content, String str) {
        SharedPreferences sp = content.getSharedPreferences("db", 0);
        Editor edit = sp.edit();
        edit.putString(key, str);
        edit.commit();
    }


}
