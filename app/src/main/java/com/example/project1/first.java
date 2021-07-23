package com.example.project1;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class first extends AppCompatActivity {

    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private FirebaseAuth nAuth;
    private Button btn_4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nAuth=FirebaseAuth.getInstance();
        Toolbar toolbar1 = findViewById(R.id.home_toolbar1);
        setSupportActionBar(toolbar1);
        FirebaseUser nUser=nAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btn_1=findViewById(R.id.tbtn);
        btn_2 = findViewById(R.id.cbtn);
        btn_3=findViewById(R.id.ttbtn);


        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(first.this, ActivityHome.class);
                startActivity(intent);

            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(first.this, count.class));
            }
        });
        btn_3.setOnClickListener(v -> {
            Intent intent = new Intent(first.this, ActivityHome1.class);
            startActivity(intent);

        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                nAuth.signOut();
                Intent intent  = new Intent(first.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            case R.id.backhome:

                Intent intent1= new Intent(first.this, Login.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}















