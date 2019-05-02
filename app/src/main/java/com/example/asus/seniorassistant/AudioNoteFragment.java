package com.example.asus.seniorassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioNoteFragment extends Fragment {

    private FirebaseAuth auth;
    private RecyclerView rvAudioNoteList;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference audioNoteDatabase;
    private FirebaseRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audionote, container, false);
        getActivity().setTitle("Audio Note");
        rvAudioNoteList = (RecyclerView) view.findViewById(R.id.rvAudioNoteList);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvAudioNoteList.setLayoutManager(linearLayoutManager);
        rvAudioNoteList.setHasFixedSize(true);
        setHasOptionsMenu(true);
        fetch();

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            audioNoteDatabase = FirebaseDatabase.getInstance().getReference().child("audioNotes").child(auth.getCurrentUser().getUid());

        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.note_btnCreatenote:
                Intent intent = new Intent(getActivity(), RecordAudioActivity.class);
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("audioNotes")
                .child(auth.getInstance().getCurrentUser().getUid());

        FirebaseRecyclerOptions<AudioNoteModel> options =
                new FirebaseRecyclerOptions.Builder<AudioNoteModel>()
                        .setQuery(query, new SnapshotParser<AudioNoteModel>() {
                            @NonNull
                            @Override
                            public AudioNoteModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                AudioNoteModel anm = new AudioNoteModel();
                                if(snapshot.exists()) {
                                    anm = new AudioNoteModel(snapshot.child("title").getValue().toString(),
                                            snapshot.child("timestamp").getValue().toString(),
                                            snapshot.child("duration").getValue().toString());

                                }
                                return anm;
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<AudioNoteModel, AudioNoteViewHolder>(options) {
            @Override
            public AudioNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.audionote_layout, parent, false);

                return new AudioNoteViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(AudioNoteViewHolder holder, final int position, AudioNoteModel model) {
                final String audioNoteId = getRef(position).getKey();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm ");
                String dateString = formatter.format(new Date(Long.parseLong(model.getNoteTime())));




                String sDuration = String.valueOf(model.getDuration());


                holder.setNoteTitle(model.getNoteTitle());
                holder.setNoteTime(dateString);
                holder.setDuration(sDuration+"sec(s)");





                holder.cvNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ViewRecordedAudio.class);
                        intent.putExtra("audioNoteId",audioNoteId);
                        startActivity(intent);
                    }
                });
            }

        };
        rvAudioNoteList.setAdapter(adapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
