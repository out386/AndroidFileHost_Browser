package browser.afh.tools;

/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/*
 * This file is part of AFH Browser.
 *
 * AFH Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AFH Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AFH Browser. If not, see <http://www.gnu.org/licenses/>.
 */

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import browser.afh.BuildConfig;

public class CacheList {
    public static void write(List list, File file) {
        boolean isDeleted = false;
        if (file.exists())
            isDeleted = file.delete();
            if (!isDeleted){
                if (BuildConfig.DEBUG) Log.d(Constants.TAG,"Cachelist file found but deletion failed");
            }
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List read(File file) {
        List list = null;
        if (!file.exists()) {
            if (BuildConfig.DEBUG) Log.i(Constants.TAG, "read: File missing");
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            list = (List) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            Log.i(Constants.TAG, "read: " + e.toString());
        } catch (ClassNotFoundException e) {
            boolean isDeleted = file.delete();
            if (!isDeleted) if (BuildConfig.DEBUG) Log.d(Constants.TAG, "File deletion failed in function #read");
        }
        return list;
    }
}
