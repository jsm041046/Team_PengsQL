package com.example.vept;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vept.sysops.L1.SysOpsDB;

import java.util.List;

public class MainDesignViewModel extends ViewModel {
    private final MutableLiveData<List<String>> fileDataLiveData = new MutableLiveData<>();
    private SysOpsDB sysopsDB;
    public MainDesignViewModel() {
    }

    public LiveData<List<String>> getFileDataLiveData() {
        return fileDataLiveData;
    }

    public void loadDatabaseNames(Context context) {
        sysopsDB = new SysOpsDB(context);

        new Thread(() -> {
            List<String> databaseNames = sysopsDB.getDatabaseNames();
            fileDataLiveData.postValue(databaseNames);
        }).start();
    }
    public void deleteDatabase(Context context, String name) {
        sysopsDB = new SysOpsDB(context);
        sysopsDB.deleteDatabaseInfo(name);
        loadDatabaseNames(context); // 갱신
    }

}