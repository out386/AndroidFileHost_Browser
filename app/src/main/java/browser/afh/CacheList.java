package browser.afh;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

class CacheList {
    static void write(List list, File file) {
        File listFile = file;
        if(listFile.exists())
            listFile.delete();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(listFile));
            oos.writeObject(list);
            oos.close();
        } catch (IOException e) {}
    }
    static List read(File file) {
        List list = null;
        if(!file.exists()){
            Log.i("TAG", "read: File missing");
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            list = (List) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch(IOException e) {
            Log.i("TAG", "read: " + e.toString());
        }
        catch(ClassNotFoundException e){
            file.delete();
        }
        return list;
    }
}
