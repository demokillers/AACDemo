package com.demokiller.host.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact?)

    @Query("SELECT * FROM contact ORDER BY uid DESC")
    fun getContactByUid(): DataSource.Factory<Int, Contact>
}