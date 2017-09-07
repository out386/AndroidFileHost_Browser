package browser.afh.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import browser.afh.types.Files;

/**
 * Created by J on 7/25/2017.
 */

public class FilesRetainFragment extends Fragment {
    private List<Files> files;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<Files> getFiles() {
        return files;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }
}
