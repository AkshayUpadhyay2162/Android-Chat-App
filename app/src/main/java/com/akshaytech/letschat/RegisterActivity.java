package com.akshaytech.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password;
    Button register;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.Username);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        register = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textUsername = username.getText().toString();
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();
                if(TextUtils.isEmpty(textUsername)||TextUtils.isEmpty(textEmail)||TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if(textPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password must be of at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    Register(textUsername,textEmail,textPassword);
                }
            }
        });
    }

    private void Register(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String Userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(Userid);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("id",Userid);
                    hashMap.put("username",username);
                    hashMap.put("imageURL","default");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 Intent intent = new Intent(RegisterActivity.this,StartActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                 startActivity(intent);
                                 finish();
                                 Toast.makeText(RegisterActivity.this, "Registered Successfully...", Toast.LENGTH_SHORT).show();
                             }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Registration failed...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
