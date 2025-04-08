package com.example.vept

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vept.ed.L4.EditorMainActivity
import com.example.vept.pl.L4.PlannerMainActivity
import com.example.vept.pl.L4.PlannerMainDesign
import com.example.vept.sysops.L1.FileExplorer


@Composable
fun MainDesign(
    viewModel: MainDesignViewModel
){
    Row (
        modifier = Modifier
            .padding(30.dp)
    ){
        Column(modifier = Modifier.weight(8f)){
            Spacer(modifier = Modifier.height(35.dp))
            Title()
            Spacer(modifier = Modifier.height(15.dp))
            FileList(viewModel = viewModel)
        }
        Spacer(modifier = Modifier.width(25.dp))
        Column(modifier = Modifier.weight(2f)){
            Spacer(modifier = Modifier.height(85.dp))
            TmpDesignButtonPack(viewModel)
            Spacer(modifier = Modifier.height(15.dp))
            BottomButton(viewModel = viewModel)
        }
    }
}




// 앱 이름
@Composable
fun Title(){
    Text(
        text = "Peng's QL",
        fontSize = 25.sp,
        style = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight(weight = 20)
        )
    )
}


@Composable
fun FileList(viewModel: MainDesignViewModel) {
    val fileData by viewModel.fileDataLiveData.observeAsState(emptyList())
    val context = LocalContext.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier
                .border(1.dp, Color.LightGray, RectangleShape)
                .fillMaxWidth()
        ) {
            fileData.forEach { fileName ->
                Row(modifier = Modifier.padding(10.dp)) {
                    Text(text = fileName, modifier = Modifier.weight(3f))
                    Text(text = "-", modifier = Modifier.weight(2f)) // 크기정보 없으면 대시

                    Button(onClick = {
                        val intent = Intent(context, EditorMainActivity::class.java)
                        intent.putExtra(EditorMainActivity.EXTRA_DATABASE_NAME, fileName)
                        context.startActivity(intent)
                    }) {
                        Text("편집")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        AlertDialog.Builder(context)
                            .setTitle("삭제 확인")
                            .setMessage("'$fileName'을 삭제하시겠습니까?")
                            .setPositiveButton("삭제") { _, _ ->
                                viewModel.deleteDatabase(context, fileName)
                            }
                            .setNegativeButton("취소", null)
                            .show()
                    }) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}




// 버튼123 개별
@Composable
fun TmpDesignButton(
    viewModel: MainDesignViewModel,
    name: String // button nameD
){
    val context = LocalContext.current
    Button(
        onClick = {
                val intent = Intent(context, PlannerMainActivity::class.java)
                context.startActivity(intent)
            }
    ) {
        Text(
            text = name
        )
    }
}

// 버튼123 묶음
@Composable
fun TmpDesignButtonPack(
    viewModel: MainDesignViewModel
){
    Column(){
        TmpDesignButton(viewModel,"버튼1")
        TmpDesignButton(viewModel,"버튼2")
        TmpDesignButton(viewModel,"버튼3")
    }
}

// 파일 추가 버튼
@Composable
fun BottomButton(viewModel: MainDesignViewModel) {
    val context = LocalContext.current
    val activity = context as AppCompatActivity // 파일 선택 결과 처리를 위한 Activity 필요
    val fileExplorer = remember { FileExplorer(context, null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                fileExplorer.setSelectedFileUri(uri)
                fileExplorer.classifyAndSave()
                viewModel.loadDatabaseNames(context) // 리스트 갱신
            }
        }
    }

    // FileExplorer에 런처 설정
    LaunchedEffect(Unit) {
        fileExplorer.setFilePickerLauncher(launcher)
    }

    Button(onClick = {
        fileExplorer.openFileExplorer()
    }) {
        Text("+")
    }
}
