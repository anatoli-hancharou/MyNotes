package com.mylab.notes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mylab.notes.db.Note
import com.mylab.notes.db.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel (private val repo: NoteRepository) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repo.allNotes

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repo.insertNote(note)
    }
    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repo.updateNote(note)
    }
    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repo.deleteNote(note)
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("")
    }
}