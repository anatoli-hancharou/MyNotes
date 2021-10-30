package com.mylab.notes

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mylab.notes.db.NotesDatabase
import com.mylab.notes.db.Note
import com.mylab.notes.db.NoteRepository
import com.mylab.notes.viewModels.NoteViewModel
import com.mylab.notes.viewModels.NoteViewModelFactory
import kotlinx.android.synthetic.main.fragment_create_note.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

class SingleNoteFragment : Fragment() {
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(NoteRepository(NotesDatabase.getDatabase(requireContext()).noteDao()))
    }

    private var noteId = -1
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = requireArguments().getInt("noteId", -1)
            note = requireArguments().getParcelable("note")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        fun getNewNoteFragmentInstance() = SingleNoteFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (noteId != -1 && note != null) {

            noteTitle.setText(note?.title)
            noteDateTime.text = note?.date
            noteText.setText(note?.text)
            when (note?.tag) {
                "red" -> Tag.setColorFilter(Color.RED)
                "green" -> Tag.setColorFilter(Color.GREEN)
                "yellow"-> Tag.setColorFilter(Color.YELLOW)
                "black"-> Tag.setColorFilter(Color.BLACK)
            }
        }

        btnDelete.setOnClickListener { deleteNote() }

        Tag.setOnClickListener { showPopup(Tag) }

        btnEnd.setOnClickListener { if (noteId == -1) saveNote() else updateNote() }

        btnBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity, view)
        popup.inflate(R.menu.tag_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.tag_red -> {
                    Tag.setColorFilter(Color.RED);
                    Tag.contentDescription = "red"
                }
                R.id.tag_green -> {
                    Tag.setColorFilter(Color.GREEN);
                    Tag.contentDescription = "green"
                }
                R.id.tag_yellow -> {
                    Tag.setColorFilter(Color.YELLOW);
                    Tag.contentDescription = "yellow"
                }
                R.id.tag_black -> {
                    Tag.setColorFilter(Color.BLACK);
                    Tag.contentDescription = "black"
                }
            }
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }else{
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popup]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        popup.show()
    }

    private fun saveNote() {
        val note = Note(
            noteTitle.text.toString(),
            noteText.text.toString(),
            getDate(),
            Tag.contentDescription.toString()
        )

        if (note.text != "" || note.title != ""){
            noteViewModel.insert(note)
        }

        btnBack.callOnClick()
    }

    private fun updateNote(){
        if (note?.title != noteTitle.text.toString() ||
            note?.text != noteText.text.toString() ||
            note?.tag != Tag.contentDescription.toString()){

            note?.title = noteTitle.text.toString()
            note?.text = noteText.text.toString()

            note?.tag = Tag.contentDescription.toString()

            note?.date = getDate()

            if (note?.text == "" && note?.title == ""){
                noteViewModel.delete(note!!)
            }
            else{
                noteViewModel.update(note!!)
            }
        }

        btnBack.callOnClick()
    }

    private fun getDate(): String{
        val formatter = SimpleDateFormat("dd.MM.yy hh:mm")
        return formatter.format(Date())
    }

    private fun deleteNote(){
        noteViewModel.delete(note!!)
        requireActivity().supportFragmentManager.popBackStack()
    }

}