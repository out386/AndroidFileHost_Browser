package browser.afh.tools;


import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;

import java.text.DecimalFormat;

public class Utils {
    public static String sizeFormat(long size) {
        if(size <= 0)
            return null;
        float newSize = size;
        String unit = " B";
        if (newSize > 1024) {
            unit = " KiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " MiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " GiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " TiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = ". Wrong size.";
            newSize = 0;
        }
        return (new DecimalFormat("#0.00").format(newSize) + unit);
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getStringColor(Resources resources, int key) {
        return Integer.toHexString(resources.getColor(key));
    }

    public static int parseColor(String string){
        return Color.parseColor(string);
    }
}
