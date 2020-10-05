package org.mbg.myboard2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class FragmentProfile extends Fragment {

    //**
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //**

    ViewGroup viewGroup;
    TextView profileEmail;
    TextView profileNickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container,false);
        db = FirebaseFirestore.getInstance();

        profileEmail=viewGroup.findViewById(R.id.textView7);
        profileNickname=viewGroup.findViewById(R.id.textView8);

        String userEmail=getUserEmail();
        profileEmail.setText("email : "+userEmail);


         DocumentReference docRef = db.collection("member").document(userEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String userNickname = (String)document.getString("nickname");
                            if (document != null) {
                                profileNickname.setText("nickname: "+userNickname);
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });

        Button logoutButton=viewGroup.findViewById(R.id.button_logout2);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).signOut();
            }});

        return viewGroup;



    }
    public String getUserEmail() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            return email;
        }
        else
            return null;
        // [END get_user_profile]
    }

    /*
    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]

        //로그아웃 하면 로그인 화면으로 돌아감
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        finish();
    }*/

}
