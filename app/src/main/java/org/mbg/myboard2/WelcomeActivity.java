package org.mbg.myboard2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    // Access a Cloud Firestore instance from your Activity
   FirebaseFirestore db;

    private EditText nickname;
    private CheckBox A1;    //게임장르-전체게임
    private CheckBox A2;    //전략게임
    private CheckBox A3;    //테마 집중형 게임
    private CheckBox A4;    //튜닝 가능 게임
    private CheckBox A5;    //게임 시간-전체시간
    private CheckBox A6;    //
    private CheckBox A7;    //
    private CheckBox A8;    //
    private CheckBox A9;
    private CheckBox A10;

   private Button submitButton;

   private TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //여기부터 제가 실험을 하나 좀 해보겠습니다.
        db = FirebaseFirestore.getInstance();

        nickname=findViewById(R.id.nickname);

        A1=findViewById(R.id.checkBoxA1);
        A2=findViewById(R.id.checkBoxA2);
        A3=findViewById(R.id.checkBoxA3);
        A4=findViewById(R.id.checkBoxA4);
        A5=findViewById(R.id.checkBoxA5);
        A6=findViewById(R.id.checkBoxA6);
        A7=findViewById(R.id.checkBoxA7);
        A8=findViewById(R.id.checkBoxA8);
        A9=findViewById(R.id.checkBoxA9);
        A10=findViewById(R.id.checkBoxA10);

        //textView6=findViewById(R.id.textView6);

        submitButton=findViewById(R.id.buttonsubmit);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //회원 정보가 서버로 들어갑니다
                ArrayList tagGenre=new ArrayList();
                if(A1.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A1.getText().toString());
                if(A2.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A2.getText().toString());
                if(A3.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A3.getText().toString());
                if(A4.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A4.getText().toString());
                if(A5.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A5.getText().toString());
                if(A6.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A6.getText().toString());
                if(A7.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A7.getText().toString());
                if(A8.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A8.getText().toString());
                if(A9.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A9.getText().toString());
                if(A10.isChecked())   //체크 박스가 체크 된 경우
                    tagGenre.add(A10.getText().toString());

                String email=getUserEmail();
                addCustomClass(nickname.getText().toString(),email,tagGenre);

            }
        });

    }

/*
    public String getUserid() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            //String name = user.getDisplayName();
            //String email = user.getEmail();
            // Uri photoUrl = user.getPhotoUrl();
            // Check if user's email is verified
            ///boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            //String uid2=FirebaseUser.getIdToken();
            String uid = user.getUid();
            return uid;
            //String uid2=FirebaseUser.getIdToken();
        }
        else
            return null;
        // [END get_user_profile]
    }*/
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


    public void filtering4(){
        List list=new ArrayList();
        list.add("마르코 폴로2");
        list.add("위자드");

        CollectionReference gameRef=db.collection("BoardGame");
        gameRef.whereEqualTo("이름",list)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getString("이름"));
                                System.out.println(document.getString("장르"));
                                System.out.println(document.getString("게임 최소 시간"));
                                System.out.println(document.getString("게임 최대 시간"));
                                System.out.println(document.getString("게임 최소 인원"));
                                System.out.println(document.getString("게임 최대 인원"));

                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

/*
    public void filtering3(){
        //복합 쿼리 이용해서 많은 문서(게임) 가져오기
        //복합 쿼리는 무조건 색인을 미리 설정해 놔야 가능

        db.collection("BoardGame")
                .whereEqualTo("게임 최대 시간", "60")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getString("이름"));
                                System.out.println(document.getString("장르"));
                                System.out.println(document.getString("게임 최소 시간"));
                                System.out.println(document.getString("게임 최대 시간"));
                                System.out.println(document.getString("게임 최소 인원"));
                                System.out.println(document.getString("게임 최대 인원"));
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
*/
    /*
    public void filtering2(){
        //특정 게임 하나만 가져오기
        //커스텀 객체 유형을 사용하는 것이 더 편리한 경우가 많습니다. 데이터 추가 가이드에서 각 도시를 정의하는 데 사용한 City 클래스를 정의했습니다. 다음과 같이 문서를 City 객체로 되돌릴 수 있습니다.
        //이렇게 데이터 다루는게 나을지도
        DocumentReference docRef = db.collection("cities").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Item item = documentSnapshot.toObject(Item.class);
                //이 정보를 이제 다른 곳으로 보내야 하는데 말이죠
            }
        });
    } */

/*
    public void filtering1(){

        // Create a reference to the cities collection
        DocumentReference docRef = db.collection("BoardGame").document("마르코 폴로2");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        System.out.println(document.getString("이름"));
                        System.out.println(document.getString("장르"));
                        System.out.println(document.getString("게임 최소 시간"));
                        System.out.println(document.getString("게임 최대 시간"));
                        System.out.println(document.getString("게임 최소 인원"));
                        System.out.println(document.getString("게임 최대 인원"));
                    } else {
                        //Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext(),"no such document",Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_LONG).show();
                }
            }
        });

        // Create a query against the collection.
        //게임 시간이 60분 검색 -> 최소시간<=60 and 최대시간>=60


        Task<QuerySnapshot> query = gameRef.whereLessThanOrEqualTo("게임 최소 시간", "60")
                .whereGreaterThanOrEqualTo("게임 최대 시간","60")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                //System.out.println(document.getId());
                                //list.add(new Item(document.getId(), document.getString("장르"),document.getString("게임 최소 시간"),document.getString("게임 최대 시간")));


                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                            //System.out.println("error");

                        }
                    }
                });


    }*/

    private void updateUI() { //update ui code here
        //시작화면으로 들어가기
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void addCustomClass(String nickname,String email,ArrayList tagGenre) {
        // [START add_custom_class]
        //City city = new City("Los Angeles", "CA", "USA", false, 5000000L, Arrays.asList("west_coast", "sorcal"));
        /*List list=new ArrayList();
        Map map =new HashMap();
        map.put("Gmemo","이 게임은 답이 없다");
        map.put("Gname","광기의 저택");
        list.add(map);
        map=new HashMap();
        map.put("Gmemo","firestore 데이터 제대로 들어가는지 확인");
        map.put("Gname","루미큐브");
        list.add(map);*/
        User user;
        if(tagGenre==null)
            user=new User(email,nickname);
        else
            user=new User(email,nickname,tagGenre);
        db.collection("member").document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"가입을 환영합니다!",Toast.LENGTH_LONG).show();
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.w(TAG, "Error writing document", e);
                Toast.makeText(getApplicationContext(),"Error writing document",Toast.LENGTH_LONG).show();
            }
        });

        /*
        User user=new User(list,"","email.com","firestore");
        db.collection("member").document("User").set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(getApplicationContext(),"DocumentSnapshot successfully written!",Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getApplicationContext(),"Error writing document",Toast.LENGTH_LONG).show();
                    }
                });
        // [END add_custom_class]*/
    }


}