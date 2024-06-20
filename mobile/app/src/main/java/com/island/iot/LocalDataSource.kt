package com.island.iot

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


@Entity
data class User(
    @PrimaryKey
    val userId: Int,
    val token: String,
    val selectedJugIndex: Int = 0,
)

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun get(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: User)

    @Delete
    suspend fun delete(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
}

interface LocalDataSource {
    val user: Flow<User?>
    suspend fun setUser(user: User)
    suspend fun deleteUser(user: User)
}

class LocalDataSourceFake : LocalDataSource {
    override val user: Flow<User?> = flowOf(null)
    override suspend fun setUser(user: User) {}
    override suspend fun deleteUser(user: User) {}
}


class LocalDataSourceImpl(context: Context) : LocalDataSource {
    private val _db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database"
    ).build()
    private val _userDAO = _db.userDao()
    private val _user = _userDAO.get()
    override val user = _user.map { if (it.isEmpty()) null else it.first() }

    override suspend fun setUser(user: User) {
        _userDAO.insert(user)
    }

    override suspend fun deleteUser(user: User) {
        _userDAO.delete(user)
    }
}