package com.skywalker.mynotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final List<Note> notes;
    private final NoteClickListener noteClickListener;

    public interface NoteClickListener {
        void onNoteClick(Note note);
    }

    public NotesAdapter(List<Note> notes, NoteClickListener noteClickListener) {
        this.notes = notes;
        this.noteClickListener = noteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteTextView.setText(note.getContent());
        holder.itemView.setOnClickListener(v -> noteClickListener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTextView;

        NoteViewHolder(View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.note_text);
        }
    }
}
