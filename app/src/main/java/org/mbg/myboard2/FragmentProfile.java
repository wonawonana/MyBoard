package org.mbg.myboard2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import android.content.Intent;
import android.net.Uri;


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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class FragmentProfile extends Fragment {

    //**
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //**

    ViewGroup viewGroup;
    private TextView profileEmail;
    private TextView profileNickname;

    private Button changeNickname;
    private Button AuthCeo;
    private Button AskButton;

    private String userNickname;
    private String userEmail;
    static final int REQUEST_CODE = 1;

    Uri uri;
    ImageButton imgCEO;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container,false);
        db = FirebaseFirestore.getInstance();

        profileEmail=viewGroup.findViewById(R.id.textView7);
        profileNickname=viewGroup.findViewById(R.id.textView8);
        AuthCeo=viewGroup.findViewById(R.id.AuthCeoButton);
        changeNickname=viewGroup.findViewById(R.id.NicknameChangebutton);
        AskButton=viewGroup.findViewById(R.id.AskButton);

        userEmail=getUserEmail();
        profileEmail.setText(userEmail);


        DocumentReference docRef = db.collection("member").document(userEmail);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    profileNickname.setText(snapshot.getString("nickname"));
                } else {
                    profileNickname.setText(null);
                }
            }
        });

         changeNickname.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 final LinearLayout linear=(LinearLayout)View.inflate(getActivity(),R.layout.dialog_nickname,null);
                 new AlertDialog.Builder(getActivity())
                         .setView(linear)
                         .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 EditText etNick=(EditText)linear.findViewById(R.id.nicknamechange);
                                 docRef.update("nickname",etNick.getText().toString());
                                 dialog.dismiss();
                             }
                         })
                         .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                             }
                         })
                         .show();
             }
         });

        AskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendEmail2(userEmail);
            }
        });

         AuthCeo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Dialog dialog=new Dialog(getActivity());
                 dialog.setContentView(R.layout.cafe_ceo_submit);
                 Button submitEmailAuth=(Button)dialog.findViewById(R.id.button4);

                 EditText etCafeName=(EditText)dialog.findViewById(R.id.cafenameEdit);
                 EditText etCafeAddr=(EditText)dialog.findViewById(R.id.cafeaddressEdit);
                 EditText etPhone=(EditText)dialog.findViewById(R.id.telleEdit);
                 imgCEO=(ImageButton)dialog.findViewById(R.id.imageButton);
                 //String imgUrl="";

                 imgCEO.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         Intent intent = new Intent(Intent.ACTION_PICK);
                         intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                         startActivityForResult(intent, REQUEST_CODE);
                     }
                 });

                 //전송 버튼 누르기
                 submitEmailAuth.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if("".equals(etCafeName.getText().toString())||
                         "".equals(etCafeAddr.getText().toString())||
                         "".equals(etPhone.getText().toString())
                         )
                             Toast.makeText(getContext(),"내용을 빠짐없이 입력해 주세요.",Toast.LENGTH_LONG).show();
                         else
                            sendEmail(userEmail,userNickname,
                                 etCafeName.getText().toString(),etCafeAddr.getText().toString(),etPhone.getText().toString(),uri);
                     }
                 });
                 dialog.show();
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

    public void sendEmail(String Email, String Nickname, String cafeName, String cafeAddr, String phone,Uri filePaths){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"myboardmbg@gmail.com"};
        email.setType("application/image");
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "보드게임 카페 사장 인증 요청");
        email.putExtra(Intent.EXTRA_STREAM, filePaths);
        email.putExtra(Intent.EXTRA_TEXT, "--아래 내용은 지우지 마시고 그대로 보내주세요--\n\n\n"+"가입 이메일 : "+Email
        +"\n요청 카페 이름 : "+cafeName+"\n요청 카페 주소 : "+cafeAddr+"\n본인 전화번호 : "+phone);

        startActivity(email);
    }
    public void sendEmail2(String Email){
        Intent email2 = new Intent(Intent.ACTION_SEND);
        email2.setType("plain/text");
        String[] address = {"myboardmbg@gmail.com"};
        email2.putExtra(Intent.EXTRA_EMAIL, address);
        email2.putExtra(Intent.EXTRA_SUBJECT, "MyBoard 앱 관련 문의");
        email2.putExtra(Intent.EXTRA_TEXT, "가입 이메일(지우지 마세요) : "+Email+"\n\n=======문의 내용=======");
        startActivity(email2);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE) {
            uri = data.getData();
            imgCEO.setColorFilter(Color.parseColor("#66CCCC"), PorterDuff.Mode.SRC_IN);
        }
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
