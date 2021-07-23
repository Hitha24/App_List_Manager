package com.example.project1;


//import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

//import android.view.View;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;

public class Login extends AppCompatActivity {

    private EditText loginEmail, loginPwd;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser user = mAuth.getCurrentUser();
//            if (user!= null){
//                Intent intent = new Intent(Login.this, ActivityHome.class);
//                startActivity(intent);
//                finish();
//            }
        };

        Toolbar toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(" ");
        loader = new ProgressDialog(this);
        loginEmail = findViewById(R.id.loginEmail);
        loginPwd = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginButton);
        TextView loginQn = findViewById(R.id.loginPageQuestion);

        loginQn.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            final String email = loginEmail.getText().toString().trim();
            String password = loginPwd.getText().toString().trim();

            if (TextUtils.isEmpty(email)){
                loginEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)){
                loginPwd.setError("Password is required");
               // return;
            }else {
                loader.setMessage("Login in progress");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(Login.this, first.class);
                        startActivity(intent);
                        finish();
                    }else {
                        String error = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(Login.this, "Login failed" + error, Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });

            }
        });
    }


}

