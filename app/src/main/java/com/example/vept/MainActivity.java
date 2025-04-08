package com.example.vept;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.ViewModelProvider;

import com.example.vept.sysops.L1.Permission;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePermissions();        // 권한 체크

        // ComposeView 생성
        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);

        // ViewModel 생성
        MainDesignViewModel viewModel = new ViewModelProvider(this).get(MainDesignViewModel.class);
        viewModel.loadDatabaseNames(this);

        // Kotlin의 정적 유틸리티 메서드 호출
        ComposableWrapper.setComposableContent(composeView, viewModel);
    }

    private void initializePermissions() {
        Permission permissionChecker = new Permission(this);
        permissionChecker.checkStoragePermission();
    }


}
