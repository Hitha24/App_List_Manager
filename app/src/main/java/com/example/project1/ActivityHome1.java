package com.example.project1;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Modell.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class ActivityHome1 extends AppCompatActivity {
    private DatabaseReference nDatabase;
    private FirebaseAuth nAuth;
    private RecyclerView recyclerView1;
    private TextView totalsumResult;
    private String type;
    private  int amount;
    private String note;
    private String post_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);

        Toolbar toolbar1 = findViewById(R.id.home_toolbar1);
        setSupportActionBar(toolbar1);
        totalsumResult=findViewById(R.id.total_amount);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Daily shopping list");
        nAuth=FirebaseAuth.getInstance();
        FirebaseUser nUser=nAuth.getCurrentUser();
        String uId=nUser.getUid();
        nDatabase= FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        nDatabase.keepSynced(true);
        recyclerView1=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(layoutManager);
        nDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalamount=0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    Data data=snap.getValue(Data.class);
                    totalamount+= data.getAmount();
                    String sttotal=String.valueOf(totalamount+".00");
                    totalsumResult.setText(sttotal);

                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });


        FloatingActionButton fab_btn = findViewById(R.id.fab1);

        fab_btn.setOnClickListener(v -> customDialog());
    }



    private void customDialog(){
        AlertDialog.Builder mydialog= new AlertDialog.Builder(ActivityHome1.this);
        LayoutInflater inflater=LayoutInflater.from(ActivityHome1.this);
        View myview=inflater.inflate(R.layout.input_data,null);
        AlertDialog dialog=mydialog.create();
        dialog.setView(myview);
        EditText type=myview.findViewById(R.id.edt_type);
        EditText amount=myview.findViewById(R.id.edt_amount);
        EditText note=myview.findViewById(R.id.edt_note);
        Button btnSave1=myview.findViewById(R.id.btn_save1);
        btnSave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType=type.getText().toString().trim();
                String mAmount=amount.getText().toString().trim();
                String mNote=note.getText().toString().trim();

                int  amt=Integer.parseInt(mAmount);
                if (TextUtils.isEmpty(mType)) {
                    type.setError("Field Required");
                    return;
                }
                if (TextUtils.isEmpty(mAmount)) {
                    amount.setError("Amount Required");
                    return;
                }
                if (TextUtils.isEmpty(mNote)) {
                    note.setError("Note Required");
                    return;
                }
                String id=nDatabase.push().getKey();
                String date= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(mType,amt,mNote,date,id);
                nDatabase.child(id).setValue(data);
                Toast.makeText(ActivityHome1.this,"",Toast.LENGTH_SHORT).show();



                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = null;
        recyclerView1.setAdapter(adapter);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(nDatabase, Data.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position, @NonNull final Data model) {
                viewHolder.setDate(model.getDate());
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setAmount(model.getAmount());
                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        updateData();

                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(view);
            }

        };
        recyclerView1.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        public MyViewHolder(View itemView) {
            super(itemView);
            myview=itemView;
        }
        public void setType(String type){
            TextView mType=myview.findViewById(R.id.type);
            mType.setText(type);
        }
        public void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note);
            mNote.setText(note);
        }
        public void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }
        public void setAmount(int amount){
            TextView mAmount=myview.findViewById(R.id.amount);
            String stam=String.valueOf(amount);
            mAmount.setText(stam);
        }


    }
    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_input, null);
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();

        final EditText edt_Type= view.findViewById(R.id.edt_type_udp);
        final EditText edt_Amount = view.findViewById(R.id.edt_amount_udp);
        final EditText edt_Note = view.findViewById(R.id.edt_note_udp);

        edt_Type.setText(type);
        edt_Type.setSelection(type.length());

        edt_Amount.setText(String.valueOf(amount));
        edt_Amount.setSelection(String.valueOf(amount).length());

        edt_Note.setText(note);
        edt_Note.setSelection(note.length());


        Button btnUpdate = view.findViewById(R.id.btn_SAVE_upd);
        Button btnDelete = view.findViewById(R.id.btn_delete_upd);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edt_Type.getText().toString().trim();
                String mAmount = String.valueOf(amount);
                mAmount=edt_Amount.getText().toString().trim();
                note=edt_Note.getText().toString().trim();

                int intamount=Integer.parseInt(mAmount);

                String date = DateFormat.getDateInstance().format(new Date());
                Data data=new Data(type,intamount,note,date,post_key);
                nDatabase.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(ActivityHome1.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(ActivityHome1.this, "update failed "+err, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDatabase.child(post_key).removeValue();
                dialog.dismiss();

            }
        });


        dialog.show();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                nAuth.signOut();
                Intent intent  = new Intent(ActivityHome1.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            case R.id.backhome:

                Intent intent1= new Intent(ActivityHome1.this, first.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

