package com.mylab.notes.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Query("select * from notes order by date desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("select * from notes where id = :id")
    fun getNoteById(id: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("delete from notes")
    fun deleteAllNotes()
}