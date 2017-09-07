package com.example.root.garminblecompteur;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by cyrilstern1 on 24/08/2017.
 */

public class FileService {
    private static FileService fileService;
    private String pathSdStorage;

    private File setFolderName(String folderName) {
        File filetest = Environment.getExternalStorageDirectory();
        final String MEDIA_PATH = Environment.getExternalStorageDirectory()
                .getPath() + "/";
        Log.i("pathEnvironnement", String.valueOf(Environment.getExternalStorageDirectory().listFiles().length));
        String secStore = System.getenv("SECONDARY_STORAGE");
        return new File(secStore + "/" + folderName);
    }

    public FileService() {
    }

    public static FileService getInstance() {
        Log.i("folderPath", "instanciation");

        return (fileService != null) ? fileService : new FileService();
    }

    protected ArrayList<FileContainer> getListFile(String folderName) {
        ArrayList<FileContainer> arrayListFileContainer = new ArrayList<>();
        File filePointer = setFolderName(folderName);
        Log.i("folderPathgetListFile", String.valueOf(filePointer.listFiles().length));


        for (int i = 0; i < filePointer.listFiles().length; i++) {
            Log.i("folderPath", filePointer.listFiles()[i].getName());
            arrayListFileContainer.add(new FileContainer(filePointer.listFiles()[i].getName(), filePointer.listFiles()[i].getAbsolutePath()));
        }
        return arrayListFileContainer;
    }

}
