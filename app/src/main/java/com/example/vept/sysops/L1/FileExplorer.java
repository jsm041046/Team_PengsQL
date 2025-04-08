package com.example.vept.sysops.L1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileExplorer {
    private Context context;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private Uri selectedFileUri; // Uri 타입으로 변경
    private SysOpsDB sysOpsDB;

    public FileExplorer(Context context, ActivityResultLauncher<Intent> filePickerLauncher) {
        this.context = context;
        this.filePickerLauncher = filePickerLauncher;
        this.sysOpsDB = new SysOpsDB(context); // SysOpsDB 인스턴스 초기화
    }

    public void setSelectedFileUri(Uri uri) {
        this.selectedFileUri = uri;
    }

    public void classifyAndSave() {
        fileClassify(); // 외부에서 호출 가능하도록 래핑
    }

    public void setFilePickerLauncher(ActivityResultLauncher<Intent> filePickerLauncher) {
        this.filePickerLauncher = filePickerLauncher;
    }

    public Uri getSelectedFileUri() {
        return selectedFileUri;
    }

    public void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 모든 파일 형식 허용
        filePickerLauncher.launch(intent);
    }

    // 파일명 가져오기
    private static String getRealFileName(Context context, Uri uri) {
        String result = null;
        Cursor cursor = null;

        try {
            String[] projection = {OpenableColumns.DISPLAY_NAME};
            cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                result = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    // 데이터베이스 파일을 내부 저장소로 복사하고, 정보를 SysOpsDB에 저장
    public void saveDatabaseFile(Uri dbUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // 파일 이름 추출
            String fileName = getRealFileName(context, dbUri);
            if (fileName != null && fileName.endsWith(".db")) {
                // 저장할 내부 경로 설정
                File databaseDir = new File(context.getFilesDir(), "database");
                if (!databaseDir.exists()) {
                    databaseDir.mkdirs(); // 디렉터리가 없으면 생성
                }

                File dbFile = new File(databaseDir, fileName);
                inputStream = context.getContentResolver().openInputStream(dbUri);
                outputStream = new FileOutputStream(dbFile);

                // 파일 복사
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                // 복사 완료 로그 출력
                Log.d("FileExplorer", "Database file saved to: " + dbFile.getAbsolutePath());

                // 복사한 파일 정보를 SysOpsDB에 저장
                saveDatabaseInfoToDB(fileName, dbUri.toString(), dbFile.getAbsolutePath());

            } else {
                Log.d("FileExplorer", "Selected file is not a .db file.");
            }
        } catch (IOException e) {
            Log.e("FileExplorer", "Error saving database file", e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                Log.e("FileExplorer", "Error closing streams", e);
            }
        }
    }

    // 데이터베이스 정보를 SysOpsDB에 저장
    private void saveDatabaseInfoToDB(String databaseName, String originalPath, String interiorPath) {
        SQLiteDatabase db = sysOpsDB.getWritableDatabase();

        // 1. 데이터베이스 정보 삽입
        ContentValues values = new ContentValues();
        values.put("database_name", databaseName);
        values.put("original_path", originalPath);
        values.put("interior_path", interiorPath);

        long rowId = db.insert("databases_info", null, values);
        if (rowId != -1) {
            Log.d("FileExplorer", "Database info saved to SysOpsDB, row ID: " + rowId);

            // 2. order_hierarchy에 데이터 삽입 (entity_type = "database"와 entity_id = database_id)
            ContentValues orderHierarchyValues = new ContentValues();
            orderHierarchyValues.put("entity_type", "database");
            orderHierarchyValues.put("entity_id", rowId); // 삽입된 database_id를 사용
            orderHierarchyValues.put("order_priority", 0); // 기본 값으로 0 설정

            long orderHierarchyRowId = db.insert("order_hierarchy", null, orderHierarchyValues);
            if (orderHierarchyRowId != -1) {
                Log.d("FileExplorer", "Order hierarchy info saved, row ID: " + orderHierarchyRowId);
            } else {
                Log.e("FileExplorer", "Failed to insert order hierarchy info.");
            }

        } else {
            Log.e("FileExplorer", "Failed to insert database info into SysOpsDB.");
        }
    }


    // 파일 확장자 분류
    private void fileClassify() {
        if (selectedFileUri != null) {
            // 파일 이름 가져오기
            String fileName = getRealFileName(context, selectedFileUri);

            if (fileName != null) {
                if (fileName.endsWith(".db")) {
                    Log.d("FileExplorer", "Selected file is a .db file: " + fileName);
                    saveDatabaseFile(selectedFileUri);                    // .db 파일 처리 로직

                } else if (fileName.endsWith(".dbia")) {
                    Log.d("FileExplorer", "Selected file is a .dbia file: " + fileName);
                    // .dbia 파일 처리 로직
                } else {
                    Log.d("FileExplorer", "Selected file is of an unknown type: " + fileName);
                    // 기타 파일 형식 처리 로직
                }
            } else {
                Log.d("FileExplorer", "Could not retrieve file name.");
            }
        } else {
            Log.d("FileExplorer", "No file selected.");
        }
    }

}
