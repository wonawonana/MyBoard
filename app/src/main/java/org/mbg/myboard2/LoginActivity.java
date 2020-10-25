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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    //구글 로그인 연동

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton_google;

    //계정 로그인
    private EditText editTextemail;
    private EditText editTextpassword;
    private TextView goRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        signInButton_google = findViewById(R.id.signInButton);
        signInButton_google.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signIn_google();
            }
        });


        // Google 로그인을 앱에 통합합니다. 다음과 같이 GoogleSignInOptions 객체를 구성할 때 requestIdToken을 호출합니다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        mAuth = FirebaseAuth.getInstance();


        //~~~~~~~~~~~~~~~~~여기부터 계정 로그인~~~~~~~~~~~~~~~~~~
        editTextemail=findViewById(R.id.editTextTextEmailAddress);
        editTextpassword=findViewById(R.id.editTextTextPassword);
        Button signInButton_email=findViewById(R.id.button_login);
        signInButton_email.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userLogin(editTextemail.getText().toString(),editTextpassword.getText().toString());
            }
        });


        //~~~~~~~~~~~~~~회원가입 버튼~~~~~~~~~~~~~~
        goRegisterButton=findViewById(R.id.textViewRegister);
        goRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              RegisterUI();
            }
        });


    }



    //~~~~~~구글 계정 연동 ~~~~~~~~~//
    private void signIn_google() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    //사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서 Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증합니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Snackbar.make(findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(getApplicationContext(),"login success",Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        //시작하고 메인 화면

        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void RegisterUI() { //update ui code here
        //시작하고 메인 화면
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    private void WelcomeUI(FirebaseUser user) { //구글 첫 로그인 시 웰컴 화면
        //시작하고 메인 화면
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


    //~~~~계정 로그인~~~~~//

    //활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다
    //얘는 아직 안 썼음
    //TAG 는 대체 뭐람
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //사용자가 앱에 로그인하면 다음과 같이 사용자의 이메일 주소와 비밀번호를 signInWithEmailAndPassword에 전달합니다
    private void userLogin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "환영합니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }



}