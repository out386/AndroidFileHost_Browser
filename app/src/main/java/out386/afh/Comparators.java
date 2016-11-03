package out386.afh;

import java.util.Comparator;

/**
 * Created by Js on 11/4/2016.
 */

class Comparators {
    static Comparator <AfhFiles> byUploadDate = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            return -(f1.upload_date.compareTo(f2.upload_date));
        }
    };
    static Comparator <AfhFiles> byFileName = new Comparator<AfhFiles>() {
        @Override
        public int compare(AfhFiles f1, AfhFiles f2) {
            return (f1.filename.compareTo(f2.filename));
        }
    };
}
