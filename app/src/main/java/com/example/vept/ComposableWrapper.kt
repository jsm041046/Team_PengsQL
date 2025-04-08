package com.example.vept

import androidx.compose.ui.platform.ComposeView
import com.example.vept.pl.L4.PlannerMainDesign
import com.example.vept.pl.L4.PlannerMainViewModel

object ComposableWrapper {
    @JvmStatic
    fun setComposableContent(composeView: ComposeView, viewModel: MainDesignViewModel) {
        composeView.setContent {
            MainDesign(viewModel)
        }
    }
    @JvmStatic
    fun setComposableContent(composeView: ComposeView, viewModel: PlannerMainViewModel) {
        composeView.setContent {
            PlannerMainDesign(viewModel)
        }
    }
}
