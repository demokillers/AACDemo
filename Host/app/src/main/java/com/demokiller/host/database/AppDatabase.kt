package com.demokiller.host.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.lang.Exception


@Database(entities = [Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

object DatabaseUtil {
    var instance: AppDatabase? = null
    fun getInstance(context: Context? = null): AppDatabase {
        if (instance == null) {
            synchronized(AppDatabase::class.java) {
                if (context == null) {
                    throw Exception("context can not null first used")
                }
                if (instance == null) {
                    instance = create(context)
                }
            }
        }
        return instance!!
    }

    private fun create(context: Context): AppDatabase {
        return Room.databaseBuilder<AppDatabase>(context, AppDatabase::class.java, "contact-db").build()
    }
}