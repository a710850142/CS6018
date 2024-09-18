package com.example.lab4

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 定义Room数据库
@Database(entities= [JokeData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JokeDatabase : RoomDatabase() {
    // 定义获取JokeDAO的抽象方法
    abstract fun jokeDao(): JokeDAO

    companion object {
        // 使用volatile确保INSTANCE在所有线程中都是最新的
        @Volatile
        private var INSTANCE: JokeDatabase? = null

        // 获取数据库实例的函数，使用单例模式
        fun getDatabase(context: Context): JokeDatabase {
            return INSTANCE ?: synchronized(this) {
                // 如果实例不存在，创建数据库
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JokeDatabase::class.java,
                    "joke_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 定义数据访问对象（DAO）接口
@Dao
interface JokeDAO {

    // 插入新的笑话数据
    @Insert
    suspend fun addJokeData(data: JokeData)

    // 查询最新的一条笑话
    @Query("SELECT * from joke ORDER BY timestamp DESC LIMIT 1")
    fun latestJoke() : Flow<JokeData>

    // 查询所有笑话，按时间戳降序排列
    @Query("SELECT * from joke ORDER BY timestamp DESC")
    fun allJoke() : Flow<List<JokeData>>
}