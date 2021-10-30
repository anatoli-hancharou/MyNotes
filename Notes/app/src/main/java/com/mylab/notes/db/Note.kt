package com.mylab.notes.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Notes")
class Note(
    var title: String,
    var text: String? = "",
    var date: String,
    var tag: String? = "",
    @PrimaryKey(autoGenerate = true) var id: Int? = null
): Parcelable
