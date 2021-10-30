package com.mylab.notes

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mylab.notes.db.NotesDatabase
import com.mylab.notes.db.Note
import com.mylab.notes.db.NoteRepository
import com.mylab.notes.viewModels.NoteViewModel
import com.mylab.notes.viewModels.NoteViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import java.lang.reflect.Method

class HomeFragment : Fragment() {
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(NoteRepository(NotesDatabase.getDatabase(requireContext()).noteDao()))
    }

    var adapter: SingleNoteAdapter = SingleNoteAdapter()
    val notes: LiveData<List<Note>> by lazy { noteViewModel.allNotes }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName)
            commit()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setListener(listener)

        notes.observe(viewLifecycleOwner, {
            adapter.setNotes(notes.value as ArrayList<Note>)
            recycler_view.adapter = adapter
        })

        createNoteBtn.setOnClickListener {
            replaceFragment(SingleNoteFragment.getNewNoteFragmentInstance())
        }

        btnFilter.setOnClickListener {
            showPopup(btnFilter)
        }

        search_view.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(searchText: String?): Boolean {

                val foundNotes = ArrayList<Note>()
                if (notes.value != null && searchText != null) {

                    for (note in notes.value!!) {
                        if (note.title.lowercase(Locale.getDefault()).contains(searchText.toString())) {
                            foundNotes.add(note)
                        }
                    }
                }

                adapter.setNotes(foundNotes)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(activity, view)
        popup.inflate(R.menu.filter_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            val foundNotes = ArrayList<Note>()
            when (item!!.itemId) {
                R.id.title_ask -> {
                    notes.value!!.sortedBy { el -> el.title }.toCollection(foundNotes)
                }
                R.id.title_desk -> {
                    notes.value!!.sortedByDescending { el -> el.title }.toCollection(foundNotes)
                }
                R.id.red -> {
                    notes.value!!.filter { el -> el.tag == "red" }.toCollection(foundNotes)
                }
                R.id.green -> {
                    notes.value!!.filter { el -> el.tag == "green" }.toCollection(foundNotes)
                }
                R.id.yellow -> {
                    notes.value!!.filter { el -> el.tag == "yellow" }.toCollection(foundNotes)
                }
                R.id.black -> {
                    notes.value!!.filter { el -> el.tag == "black" }.toCollection(foundNotes)
                }
            }
            adapter.setNotes(foundNotes)
            adapter.notifyDataSetChanged()
            true
        })

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

    private val listener = object : SingleNoteAdapter.OnItemClickListener {

        override fun onClicked(noteId: Int?) {

            if (noteId != null) {
                var note: Note? = null
                for (n in notes.value!!) {
                    if (n.id == noteId) {
                        note = n
                    }
                }
                if (note != null) {
                    val bundle = Bundle()
                    bundle.putParcelable("note", note)
                    bundle.putInt("noteId", noteId)

                    val fragment = SingleNoteFragment.getNewNoteFragmentInstance()
                    fragment.arguments = bundle

                    replaceFragment(fragment)
                }
            }
        }
    }
}
