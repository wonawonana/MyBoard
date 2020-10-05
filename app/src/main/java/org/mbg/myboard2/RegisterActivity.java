package org.mbg.myboard2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth
    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerEmail=findViewById(R.id.editTextTextEmailAddress2);
        registerPassword=findViewById(R.id.editTextTextPassword2);

        Button registerButton=findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                makeUser(registerEmail.getText().toString(),registerPassword.getText().toString());
            }
        });



    }

    private void updateUI(FirebaseUser user) { //update ui code here
        //시작하고 welcome 화면

        if (user != null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void makeUser(String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if(password.length()<6)
                                Toast.makeText(RegisterActivity.this, "비밀번호는 6자리 이상이어야 합니다.",
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일 입니다.",
                                        Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

}