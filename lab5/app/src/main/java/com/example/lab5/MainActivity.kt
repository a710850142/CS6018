package com.example.lab5

// 导入所需的库
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab5.ui.theme.Lab5_sensorTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取传感器管理器
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // 获取重力传感器
        val gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        // 获取重力数据流
        val graFlow : Flow<FloatArray> = getGraData(gravity, sensorManager)
        // 创建ViewModel实例
        val vm = ViewModelProvider(this)[ScreenOrientationViewModel::class.java]

        setContent {
            Lab5_sensorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 收集重力传感器数据
                    val graReading by graFlow.collectAsStateWithLifecycle(
                        floatArrayOf(0.0f, 0.0f, 0.0f),
                        lifecycleOwner = this@MainActivity
                    )
                    // 显示重力读数
                    Text("Gravity reading: %.5f %.5f %.5f".format(graReading[0], graReading[1], graReading[2]))

                    // 获取屏幕方向
                    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    vm.screenOrientation.value = windowManager.defaultDisplay.rotation

                    BoxWithConstraints (
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 计算可移动的最大范围
                        val maxWidth = maxWidth.value - 50
                        val maxHeight = maxHeight.value - 50

                        // 记住球的位置
                        var position by remember { mutableStateOf(Offset(100f, 100f)) }

                        // 根据屏幕方向和重力数据更新球的位置
                        vm.screenOrientation.observe(this@MainActivity){o ->
                            position = when (o) {
                                1 -> {
                                    Offset(
                                        position.x + graReading[1] / 2,
                                        position.y + graReading[0] / 2
                                    )
                                }
                                3 -> {
                                    Offset(
                                        position.x - graReading[1] / 2,
                                        position.y - graReading[0] / 2
                                    )
                                }
                                else -> {
                                    Offset(
                                        position.x - graReading[0] / 2,
                                        position.y + graReading[1] / 2
                                    )
                                }
                            }
                        }

                        // 确保球的位置在屏幕范围内
                        position = Offset(
                            position.x.coerceIn(0f, maxWidth),
                            position.y.coerceIn(0f, maxHeight)
                        )

                        // 绘制球
                        marble(pos = position)
                    }
                }
            }
        }
    }
}

// 获取重力数据的函数
fun getGraData(graSensor: Sensor?, sensorManager: SensorManager) : Flow<FloatArray>{
    return channelFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event !== null) {
                    Log.e("Gravity sensor event", event.values.toString())
                    var success = channel.trySend(event.values.copyOf()).isSuccess
                    Log.e("Success?", success.toString())
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // 暂不处理精度变化
            }
        }
        sensorManager.registerListener(listener, graSensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

// 绘制球的可组合函数
@Composable
fun marble(pos: Offset) {
    Box(
        modifier = Modifier
            .offset(x = pos.x.dp, y = pos.y.dp)
            .size(50.dp)
            .background(Color.Magenta),
        contentAlignment = Alignment.Center
    ){
        // 球的内容为空
    }
}

// 屏幕方向ViewModel
class ScreenOrientationViewModel : ViewModel() {
    var screenOrientation = MutableLiveData<Int>()
}

