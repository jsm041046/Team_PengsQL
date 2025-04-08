package com.example.vept.pl.L4

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NPosition(val location: Offset, val name: String)

class DiagramWrapper(private val diagram: List<Diagram>) {
    private val _position = MutableStateFlow(diagram.map { NPosition(Offset(it.x, it.y),it.name) })
    val position: StateFlow<List<NPosition>> = _position

    private fun updateFromJava(index: Int) {
        val newPositions = _position.value.toMutableList()
        newPositions[index] = NPosition(Offset(diagram[index].x, diagram[index].y),newPositions[index].name)
        _position.value = newPositions
    }

    fun updatePointF(index: Int, x: Float, y: Float) {
        diagram[index].setPosition(x, y)
        updateFromJava(index)
    }
}