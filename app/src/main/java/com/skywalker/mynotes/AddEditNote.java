package com.skywalker.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.room.Room;

public class AddEditNote extends Fragment {

    private NoteDatabase noteDatabase;
    private EditText noteContentEditText;
    private Note note;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteDatabase = Room.databaseBuilder(requireContext(), NoteDatabase.class, "note_db").build();
        noteContentEditText = view.findViewById(R.id.note_content_edit_text); // Ensure this matches the XML ID
        Button saveButton = view.findViewById(R.id.save_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        if (getArguments() != null) {
            note = (Note) getArguments().getSerializable("note_key");
            if (note != null) {
                noteContentEditText.setText(note.getContent());
                deleteButton.setVisibility(View.VISIBLE);
            }
        }

        saveButton.setOnClickListener(v -> saveNote());
        deleteButton.setOnClickListener(v -> deleteNote());
    }

    private void saveNote() {
        new Thread(() -> {
            String content = noteContentEditText.getText().toString().trim();
            if (note == null) {
                note = new Note();
                note.setContent(content);
                noteDatabase.noteDao().insert(note);
            } else {
                note.setContent(content);
                noteDatabase.noteDao().update(note);
            }
            requireActivity().runOnUiThread(() ->
                    Navigation.findNavController(requireView()).navigateUp());
        }).start();
    }

    private void deleteNote() {
        new Thread(() -> {
            if (note != null) {
                noteDatabase.noteDao().delete(note);
            }
            requireActivity().runOnUiThread(() ->
                    Navigation.findNavController(requireView()).navigateUp());
        }).start();
    }
}
