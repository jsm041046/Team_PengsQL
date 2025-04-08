package com.example.vept.pl.L4

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun PlannerMainDesign(
    viewModel: PlannerMainViewModel
){
    Row (
        modifier = Modifier
            .padding(30.dp)
    ){
        Column(modifier = Modifier.weight(8f)){
            Spacer(modifier = Modifier.height(35.dp))
            PlannerTitle()
            Spacer(modifier = Modifier.height(15.dp))
            CanvasExample(viewModel)
        }
    }
}

// 앱 이름
@Composable
fun PlannerTitle(){
    Text(
        text = "Planner",
        fontSize = 25.sp,
        style = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight(weight = 20)
        )
    )
}

//캔버스
@Composable
fun CanvasExample(viewModel: PlannerMainViewModel) {
    val mdiagrams by viewModel.dw.position.collectAsState()
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput("dragging") {
                detectDragGestures {

                        change, dragAmount ->
                    change.consume()

                    val sz : Int = mdiagrams.size - 1

                    for(i in 0..sz) {
                        val newOffset = mdiagrams[i].location + dragAmount
                        viewModel.dw.updatePointF(i, newOffset.x,newOffset.y)
                    }

                }
            }
            .onSizeChanged {
                /*pointerOffset.value.SetOffset(Offset(it.width / 2f, it.height / 2f))*/
            },
        onDraw = {
            mdiagrams.forEach {
                dia ->
                drawRect(
                    color = Color.Blue,
                    topLeft = dia.location,
                    size = Size(100.dp.toPx(), 100.dp.toPx())
                )
                drawText(textMeasurer,dia.name,topLeft = dia.location)
            }
        }
    )
}