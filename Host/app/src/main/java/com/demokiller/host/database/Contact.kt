package com.demokiller.host.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Contact(@PrimaryKey var uid: Int = 0, @ColumnInfo(name = "name") var name: String? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Contact
        if (uid != other.uid) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

}