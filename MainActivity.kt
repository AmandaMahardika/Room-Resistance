package com.example.crudroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudroom.databinding.ActivityMainBinding
import com.example.crudroom.room.Constant
import com.example.crudroom.room.Note
import com.example.crudroom.room.NoteDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var noteAdapter: NoteAdapter

    val db by lazy { NoteDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }

    fun loadNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val note = db.noteDao().getNote()
            Log.d("MainActivity", "dbResponse: $note")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(note)
            }
        }
    }

    private fun setupListener() {
        binding.buttonCreate.setOnClickListener {
            intentEdit(0, Constant.TYPE_CREATE)
        }
    }

    fun intentEdit(noteId: Int, intentType: Int) {
        startActivity(
            Intent(applicationContext, EditActivity::class.java)
                .putExtra("intent_id", noteId)
                .putExtra("intent_type", intentType)
        )
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            arrayListOf(),
            object : NoteAdapter.OnAdapterListener{
                override fun onRead(note: Note) {
                    intentEdit(note.id, Constant.TYPE_READ)
                }

                override fun onUpdate(note: Note) {
                    intentEdit(note.id, Constant.TYPE_UPDATE)
                }

                override fun onDelete(note: Note) {
                    deleteDialog(note)
                }

                override fun onRead(note: ContactsContract.CommonDataKinds.Note) {
                    TODO("Not yet implemented")
                }

                override fun onUpdate(note: ContactsContract.CommonDataKinds.Note) {
                    TODO("Not yet implemented")
                }

                override fun onDelete(note: ContactsContract.CommonDataKinds.Note) {
                    TODO("Not yet implemented")
                }
            },
        )
        binding.listNote.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }

    private fun deleteDialog(note: Note) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin Hapus ${note.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
}

private fun NoteAdapter.setData(note: List<Note>) {

}
