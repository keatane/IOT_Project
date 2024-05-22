package com.island.iot

//import androidx.room.Dao
//import androidx.room.Database
//import androidx.room.Delete
//import androidx.room.Entity
//import androidx.room.Insert
//import androidx.room.PrimaryKey
//import androidx.room.Query
//import androidx.room.RoomDatabase
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


//@Entity
//data class User(
//    @PrimaryKey
//    val userId: Int,
//    val token: String,
//)
//
//@Dao
//interface UserDAO {
//    @Query("SELECT * FROM user")
//    suspend fun get(): User
//
//    @Insert
//    suspend fun insert(users: User)
//
//    @Delete
//    suspend fun delete(user: User)
//}
//
//@Database(entities = [User::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDAO
//}


class LocalDataSource(private val sp: SharedPreferences) {
    val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()
    //val userDAO = db.userDao()

    suspend fun saveToken(token: String) {
        Log.d("AppTestingBeautiful", token)
        //userDAO.insert(User())
    }

    suspend fun saveUserId(userId: Int) {
        Log.d("APpTEsting bejsgfyd", userId.toString())
    }

    suspend fun readToken(): String? {
        return sp.getString("token", null)
    }

    suspend fun readUserId(): Int? {
        val value = sp.getInt("number", -1)
        if (value == -1) return null
        return value
    }
}