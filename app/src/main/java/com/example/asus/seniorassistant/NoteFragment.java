package com.example.asus.seniorassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

public class NoteFragment extends Fragment {

    private FirebaseAuth auth;
    private RecyclerView rvNoteList;

    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference noteDatabase;
    private FirebaseRecyclerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        rvNoteList = (RecyclerView) view.findViewById(R.id.rvNoteList);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvNoteList.setLayoutManager(linearLayoutManager);
        rvNoteList.setHasFixedSize(true);
        fetch();


        getActivity().setTitle("Note");
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            noteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(auth.getCurrentUser().getUid());

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
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
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
                .child("Notes")
                .child(auth.getInstance().getCurrentUser().getUid());

        FirebaseRecyclerOptions<NoteModel> options =
                new FirebaseRecyclerOptions.Builder<NoteModel>()
                        .setQuery(query, new SnapshotParser<NoteModel>() {
                            @NonNull
                            @Override
                            public NoteModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                NoteModel nm = new NoteModel();
                                if(snapshot.exists()) {
                                    nm = new NoteModel(snapshot.child("title").getValue().toString(),
                                            snapshot.child("timestamp").getValue().toString(),
                                            snapshot.child("image").getValue().toString());

                                }
                                return nm;
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(options) {
            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_layout, parent, false);

                return new NoteViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(NoteViewHolder holder, final int position, NoteModel model) {
                final String noteId = getRef(position).getKey();
                
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
                String dateString = formatter.format(new Date(Long.parseLong(model.getNoteTime())));


                holder.setNoteTitle(model.getNoteTitle());
                holder.setNoteTime(dateString);
                Picasso.get().load(model.getImage()).into(holder.imageView);




                holder.cvNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), DeleteEditNote.class);
                        intent.putExtra("noteId",noteId);
                        startActivity(intent);
                    }
                });
            }

        };
        rvNoteList.setAdapter(adapter);
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
