package com.example.lab4

// 导入必要的库和模块
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.lab4.ui.theme.Lab4Theme
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import com.google.gson.Gson

// 主活动类
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        // 初始化 ViewModel
                        val vm: JokeViewModel by viewModels{  JokeViewModelFactory((application as JokeApplication).jokeRepository)}
                        val currentJoke by vm.currentJoke.observeAsState()

                        // 设置协程作用域
                        val scope = rememberCoroutineScope()
                        var job : Job? by remember {
                            mutableStateOf(null)
                        }

                        // 搜索区域
                        SearchArea{
                            job = scope.launch {
                                val joke = getJoke()
                                vm.checkJoke(joke.value)
                            }
                        }

                        // 显示当前笑话
                        JokeDataDisplay(currentJoke)

                        Text("Previous Joke",
                            fontSize = 12.em,
                            lineHeight = 1.em)

                        Spacer(modifier = Modifier.padding(16.dp))

                        // 显示之前的笑话列表
                        val allJoke by vm.allJoke.observeAsState()
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp),){
                            for(data in allJoke ?: listOf()){
                                item{
                                    JokeDataDisplay(data = data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 显示笑话数据的可组合函数
@Composable
fun JokeDataDisplay(data: JokeData?, modifier: Modifier = Modifier) {
    Surface(color=MaterialTheme.colorScheme.surface) {
        Text(
            text = if (data != null) "Date: ${data.timestamp} \n ${data.joke}\n" else "No Jokes",
            modifier = modifier
        )
    }
}

// 搜索区域的可组合函数
//@Composable
//fun SearchArea(onClick: (joke: String) -> Unit){
//    Row(modifier = Modifier.padding(16.dp)) {
//        var joke: String by remember { mutableStateOf("default joke") }
//        OutlinedTextField(
//            value = joke,
//            onValueChange = { joke = it },
//            label = { Text("Joke") }
//        )
//        Button(onClick = {onClick(joke)}) {
//            Text("Get Joke")
//        }
//    }
//}
@Composable
fun SearchArea(onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        // 显示默认笑话的文本
//        Text(
//            text = "Default Joke: Why did the scarecrow win an award? He was outstanding in his field!",
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )

        // 获取笑话的按钮
        Button(onClick = onClick) {
            Text("Get New Joke")
        }
    }
}

// 预览函数
@Preview(showBackground = false)
@Composable
fun SearchAreaPreview(){
    Lab4Theme {
        SearchArea(onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun JokeDataDisplayPreview() {
    Lab4Theme {
        JokeDataDisplay(JokeData(Date(), "Why don't scientists trust atoms? Because they make up everything!"))
    }
}

// 从API获取笑话的挂起函数
suspend fun getJoke() : JString {
    return withContext(Dispatchers.IO) {
        // 构建API URL
        val url: Uri = Uri.Builder().scheme("https")
            .authority("api.chucknorris.io")
            .appendPath("jokes")
            .appendPath("random").build()

        // 建立连接并获取数据
        val conn = URL(url.toString()).openConnection() as HttpURLConnection
        conn.connect()
        val gson = Gson()
        val result = gson.fromJson(
            InputStreamReader(conn.inputStream, "UTF-8"),
            JString::class.java
        )
        Log.e("data!", gson.toJson(result).toString())
        result
    }
}

// 数据类
data class JString (var value: String)