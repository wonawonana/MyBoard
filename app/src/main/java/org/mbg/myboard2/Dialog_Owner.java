package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Dialog_Owner extends androidx.fragment.app.DialogFragment implements View.OnClickListener {
    public static final String TAG_EVENT_DIALOG="dialog_event";
    int mI = -1;
    EditText gameName;
    EditText businessHour;
    EditText price;
    Button button_gameNum;
    Button button_businessHour;
    Button button_price;
    FirebaseFirestore db;
    String UserEmail;
    FirebaseUser user;


    public Dialog_Owner(int i) {
        mI = i;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_owner, container);
        gameName= (EditText)view.findViewById(R.id.gameName);
        businessHour= (EditText)view.findViewById(R.id.businessHour);
        price=(EditText)view.findViewById(R.id.price);
        button_gameNum=(Button)view.findViewById(R.id.button_gameName);
        button_businessHour=(Button)view.findViewById(R.id.button_businessHour);
        button_price=(Button)view.findViewById(R.id.button_price);



        db=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

        button_gameNum.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> input= new ArrayList<String>();
                String str_gameName=gameName.getText().toString();
                int temp=0;
                for(int i=0;i<str_gameName.length();i++){
                    //,
                    if(str_gameName.charAt(i) == ','){
                        if(str_gameName.substring(temp, i).trim().length() != 0){
                            //temp부터 ,전까지
                            input.add(str_gameName.substring(temp, i).trim());
                            //Toast.makeText(getActivity(), ""+str_gameName.substring(temp, i).trim(), Toast.LENGTH_LONG).show();
                            // ,다음부터 다시 시작
                            temp=i+1;
                        }
                        //emptyString일 경우
                        else{
                            //Toast.makeText(getActivity(), "emptyString", Toast.LENGTH_LONG).show();
                            // ,다음부터 다시 시작
                            temp=i+1;
                        }
                    }
                    //마지막
                    else if(i==str_gameName.length()-1){
                        if(str_gameName.substring(temp, i+1).trim().length() != 0){
                            //temp부터 마지막까지
                            input.add(str_gameName.substring(temp, i+1).trim());
                            //Toast.makeText(getActivity(), ""+str_gameName.substring(temp, i+1).trim(), Toast.LENGTH_LONG).show();
                        }
                        //emptyString일 경우
                        else{
                            //Toast.makeText(getActivity(), "emptyString", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                if (input.size() != 0) {
                    CollectionReference PostRef = (CollectionReference) db.collection("cafe");
                    PostRef
                            .document(MapView.cafe_map.get(mI).id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //카페 존재합니다
                                            //Toast.makeText(getActivity(),gameName.getText()+", 카페 정보 입력", Toast.LENGTH_SHORT).show();
                                            DocumentReference docRef=db.collection("cafe").document(MapView.cafe_map.get(mI).id);
                                            for(int i=0;i<input.size();i++) {
                                                docRef.update("cafeGameList", FieldValue.arrayUnion(input.get(i)));
                                            }
                                            dismiss();

                                        } else {
                                            //카페 존재 안함
                                            // 추가해줌 = clean,게임많은지,서비스 ,, 각 인원
                                            cafeDB cafe=new cafeDB(MapView.cafe_map.get(mI).place_name,0,
                                                    0, 0, 0, input, "", "");
                                            db.collection("cafe").document(MapView.cafe_map.get(mI).id).set(cafe);
                                            //Toast.makeText(getActivity(), "cafe 없어서 추가했음"+MapView.cafe_map.get(i).id, Toast.LENGTH_LONG).show();
                                            dismiss();
                                        }
                                    } else {
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "공백은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_businessHour.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (businessHour.getText().toString().trim().length() != 0) {
                    CollectionReference PostRef = (CollectionReference) db.collection("cafe");
                    PostRef
                            .document(MapView.cafe_map.get(mI).id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //카페 존재합니다
                                            //Toast.makeText(getActivity(),businessHour.getText()+", 카페 정보 입력", Toast.LENGTH_SHORT).show();
                                            //문서 업데이트 해줌 = 각 num++1 , 평균값
                                            DocumentReference docRef = db.collection("cafe").document(MapView.cafe_map.get(mI).id);
                                            docRef.update("businessHour", businessHour.getText().toString().trim());
                                            dismiss();

                                        } else {
                                            //카페 존재 안함
                                            // 추가해줌 = clean,게임많은지,서비스 ,, 각 인원
                                            cafeDB cafe = new cafeDB(MapView.cafe_map.get(mI).place_name, 0,
                                                    0, 0, 0, new ArrayList<>(), businessHour.getText().toString().trim(), "");
                                            db.collection("cafe").document(MapView.cafe_map.get(mI).id).set(cafe);
                                            dismiss();
                                        }
                                    } else {
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "공백은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_price.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (price.getText().toString().trim().length() != 0) {
                    CollectionReference PostRef = (CollectionReference) db.collection("cafe");
                    PostRef
                            .document(MapView.cafe_map.get(mI).id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //카페 존재합니다
                                            //Toast.makeText(getActivity(),businessHour.getText()+", 카페 정보 입력", Toast.LENGTH_SHORT).show();
                                            //문서 업데이트 해줌 = 각 num++1 , 평균값
                                            DocumentReference docRef = db.collection("cafe").document(MapView.cafe_map.get(mI).id);
                                            docRef.update("price", price.getText().toString().trim());
                                            dismiss();

                                        } else {
                                            //카페 존재 안함
                                            // 추가해줌 = clean,게임많은지,서비스 ,, 각 인원
                                            cafeDB cafe = new cafeDB(MapView.cafe_map.get(mI).place_name, 0,
                                                    0, 0, 0, new ArrayList<>(), "", price.getText().toString().trim());
                                            db.collection("cafe").document(MapView.cafe_map.get(mI).id).set(cafe);
                                            dismiss();
                                        }
                                    } else {
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "공백은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}