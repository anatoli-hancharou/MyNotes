package com.mylab.notes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mylab.notes.db.Note
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class SingleNoteAdapter() : RecyclerView.Adapter<SingleNoteAdapter.NotesViewHolder>() {
    private var notes :ArrayList<Note> = ArrayList()

    // For resource optimization
    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardTitle: TextView? = null;
        var cardText: TextView? = null;

        init {
            cardTitle = view.findViewById(R.id.cardTitle)
            cardText = view.findViewById(R.id.cardText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        notes[position].title.ifEmpty { notes[position].title = notes[position].date }

        holder.cardTitle?.text = notes[position].title
        holder.cardText?.text = notes[position].text
        if (notes[position].tag.isNullOrEmpty()) {
            notes[position].tag = "black"
        }
        when (notes[position].tag) {
            "red" -> holder.itemView.my_tag.setColorFilter(Color.RED)
            "green" -> holder.itemView.my_tag.setColorFilter(Color.GREEN)
            "yellow" -> holder.itemView.my_tag.setColorFilter(Color.YELLOW)
            "black" -> holder.itemView.my_tag.setColorFilter(Color.BLACK)
        }
        holder.itemView.cardView.setOnClickListener {
            listener!!.onClicked(notes[position].id)
        }
    }

    override fun getItemCount() = notes.size

    fun setNotes(notes: ArrayList<Note>){
        this.notes = notes
    }

    fun getNotes(notes: ArrayList<Note>) : ArrayList<Note> = this.notes

    interface OnItemClickListener{
        fun onClicked(noteId: Int?)
    }

    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener?){
        listener.also { this.listener = it }
    }
}