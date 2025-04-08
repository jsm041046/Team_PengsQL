package com.example.vept.pl.L4;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.ViewModelProvider;

import com.example.vept.ComposableWrapper;

public class PlannerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ComposeView 생성
        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);

        // ViewModel 생성
        PlannerMainViewModel viewModel = new ViewModelProvider(this).get(PlannerMainViewModel.class);

        // Kotlin의 정적 유틸리티 메서드 호출
        ComposableWrapper.setComposableContent(composeView, viewModel);


    }
}