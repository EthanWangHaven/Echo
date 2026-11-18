package cn.wangce.lumi

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import cn.wangce.lumi.ui.music.MusicScreen
import cn.wangce.lumi.ui.theme.DarkGradientBottom
import cn.wangce.lumi.ui.theme.DarkGradientMid
import cn.wangce.lumi.ui.theme.DarkGradientTop
import cn.wangce.lumi.ui.theme.LightGradientBottom
import cn.wangce.lumi.ui.theme.LightGradientMid
import cn.wangce.lumi.ui.theme.LightGradientTop
import cn.wangce.lumi.ui.theme.LumiAccent
import cn.wangce.lumi.ui.theme.LumiAccentTheme
import cn.wangce.lumi.ui.theme.LumiTheme
import cn.wangce.lumi.ui.theme.LocalDarkTheme
import dagger.hilt.android.AndroidEntryPoint

// Echo 单页入口：沿用原音乐路由的鼠尾草绿强调色、0.8 密度缩放与天空渐变背景
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumiTheme {
                EchoRoot()
            }
        }
    }
}

@Composable
private fun EchoRoot() {
    val dark = LocalDarkTheme.current
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (dark) {
                    Brush.verticalGradient(listOf(DarkGradientTop, DarkGradientMid, DarkGradientBottom))
                } else {
                    Brush.verticalGradient(listOf(LightGradientTop, LightGradientMid, LightGradientBottom))
                },
            ),
    ) {
        val base = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(base.density * 0.8f, base.fontScale),
        ) {
            LumiAccentTheme(accent = LumiAccent.Sage) {
                MusicScreen(onBack = { (context as? Activity)?.finish() })
            }
        }
    }
}
