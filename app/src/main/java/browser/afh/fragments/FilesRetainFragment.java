package browser.afh.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import browser.afh.types.Files;

/**
 * Created by J on 7/25/2017.
 */

public class FilesRetainFragment extends Fragment {
    private ArrayList<Files> files;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ArrayList<Files> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<Files> files) {
        this.files = files;
    }
}
