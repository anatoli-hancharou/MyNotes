package com.mylab.notes.db;

import androidx.lifecycle.LiveData;


class NoteRepository(private val noteDao : NoteDao) {
    val allNotes : LiveData<List<Note>> = noteDao.getAllNotes()

    fun insertNote(note: Note){
        noteDao.insert(note)
    }

    fun updateNote(note: Note){
        noteDao.update(note)
    }

    fun deleteNote(note: Note){
        noteDao.delete(note)
    }
}