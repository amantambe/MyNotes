package com.skywalker.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class NotesList extends Fragment {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NoteDatabase noteDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        view.findViewById(R.id.add_note_button).setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_notesListFragment_to_addEditNoteFragment));

        noteDatabase = Room.databaseBuilder(requireContext(), NoteDatabase.class, "note_db").build();

        loadNotes();

        return view;
    }

    private void loadNotes() {
        new Thread(() -> {
            List<Note> notes = noteDatabase.noteDao().getAllNotes();
            requireActivity().runOnUiThread(() -> {
                adapter = new NotesAdapter(notes, note -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note", note);
                    Navigation.findNavController(requireView()).navigate(R.id.action_notesListFragment_to_addEditNoteFragment, bundle);
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
