package com.example.vept.sysops.L1;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SysOpsDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sysops.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_FOLDER = "databases/sysops/";
    private static final String PREFS_NAME = "SysOpsDBPrefs";
    private static final String KEY_DB_INITIALIZED = "database_initialized";

    private final Context mContext;
    private final String dbPath;
    private SQLiteDatabase db;

    public SysOpsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        this.dbPath = context.getFilesDir() + "/" + DATABASE_FOLDER + DATABASE_NAME;
        initializeDatabase();
    }

    private void initializeDatabase() {
        if (!isDatabaseInitialized()) {
            copyDatabaseFromAssets();
            setDatabaseInitialized();
        }
        openDatabase();
    }


    private void copyDatabaseFromAssets() {
        try {
            InputStream input = mContext.getAssets().open("databases/sysops/" + DATABASE_NAME);
            File outFile = new File(dbPath);
            outFile.getParentFile().mkdirs();
            OutputStream output = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            input.close();
            Log.d("SysOpsDB", "Database copied successfully.");
        } catch (IOException e) {
            Log.e("SysOpsDB", "Error copying database", e);
        }
    }

    private void openDatabase() {
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            Log.e("SysOpsDB", "Error opening database", e);
        }
    }

    public List<String> getDatabaseNames() {
        List<String> databaseNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();  // 읽기 모드 DB

        Cursor cursor = db.rawQuery("SELECT database_name FROM databases_info", null);

        if (cursor.moveToFirst()) {
            do {
                databaseNames.add(cursor.getString(0));  // 첫 번째 컬럼 (database_name) 가져오기
            } while (cursor.moveToNext());
        }

        cursor.close();
        return databaseNames;
    }

    public void deleteDatabaseInfo(String databaseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("databases_info", "database_name = ?", new String[]{databaseName});
    }


    public boolean isDatabaseInitialized() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DB_INITIALIZED, false);
    }

    public void setDatabaseInitialized() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DB_INITIALIZED, true).apply();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // databases_info 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS databases_info (" +
                "database_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "database_name TEXT NOT NULL UNIQUE, " +
                "original_path TEXT NOT NULL, " +
                "interior_path TEXT NOT NULL, " +
                "path_conn_check TEXT NOT NULL DEFAULT 'true');");

        // diagram 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS diagram (" +
                "diagram_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "diagram_name TEXT NOT NULL UNIQUE, " +
                "original_path TEXT NOT NULL, " +
                "interior_path TEXT NOT NULL, " +
                "path_conn_check TEXT NOT NULL DEFAULT 'true');");

        // order_hierarchy 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS order_hierarchy (" +
                "order_hierarchy_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "entity_type TEXT NOT NULL CHECK(entity_type IN ('database', 'diagram')), " +
                "entity_id INTEGER NOT NULL, " +
                "order_priority INTEGER DEFAULT 0, " +
                "FOREIGN KEY(entity_id) REFERENCES databases_info(database_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(entity_id) REFERENCES diagram(diagram_id) ON DELETE CASCADE);");

        // update_priority_after_update 트리거 생성
        db.execSQL("DROP TRIGGER IF EXISTS update_priority_after_update;");
        db.execSQL("CREATE TRIGGER update_priority_after_update " +
                "AFTER UPDATE ON order_hierarchy " +
                "FOR EACH ROW " +
                "BEGIN " +
                "    UPDATE order_hierarchy " +
                "    SET order_priority = order_priority + 1 " +
                "    WHERE order_hierarchy_id = NEW.order_hierarchy_id; " + // 특정 행만 업데이트하도록 수정
                "END;");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
    }
}

