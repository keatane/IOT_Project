package com.island.iot

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow


@Entity
data class User(
    @PrimaryKey
    val userId: Int,
    val token: String,
)

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun get(): Flow<List<User>>

    @Insert
    suspend fun insert(users: User)

    @Delete
    suspend fun delete(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
}


class LocalDataSource(db: AppDatabase) {
    val _userDAO = db.userDao()
    val user = _userDAO.get()

    suspend fun setUser(user: User) {
        _userDAO.insert(user)
    }

    suspend fun deleteUser(user: User) {
        _userDAO.delete(user)
    }
}