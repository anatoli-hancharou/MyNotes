package com.mylab.notes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        var instance: NotesDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): NotesDatabase {
           // context.deleteDatabase("notes.db");
            if (instance == null) {
                instance = Room.databaseBuilder(context, NotesDatabase::class.java,
                    "my-notes.db"
                )
                    .addCallback(roomCallback)
                    .build()
            }
            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: NotesDatabase) {
            val noteDao = db.noteDao()
                noteDao.insert(Note("title 1", getDate(), "Description 1", "black"))
        }

        private fun getDate(): String{
            val formatter = SimpleDateFormat("dd.MM.yy hh:mm")
            return formatter.format(Date())
        }
    }
}