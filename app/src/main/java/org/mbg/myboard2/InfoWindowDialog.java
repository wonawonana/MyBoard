package org.mbg.myboard2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class InfoWindowDialog extends androidx.fragment.app.DialogFragment implements View.OnClickListener{
    public static final String TAG_EVENT_DIALOG="dialog_event";
    private BoardCafe mCafe;
    //api
    TextView textPlaceName;
    TextView textAddress;
    TextView textPhone;
    //rating
    RatingBar ratingBar01;
    RatingBar ratingBar02;
    RatingBar ratingBar03;
    TextView textStar01;
    TextView textStar02;
    TextView textStar03;
    //사장 입력
    TextView businessHour;
    TextView price;
    TextView cafeGame;
    TextView cafeGameList;

    //별점 버튼
    Button input;
    //사장 버튼
    Button owner;
    TextView announce;

    //좋아요
    Button favorite;

    //db
    FirebaseFirestore db;
    FirebaseUser user;
    String UserEmail;

    public InfoWindowDialog(BoardCafe cafe){
        mCafe=cafe;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.dialog_cafe, container);

        textPlaceName = (TextView) view.findViewById(R.id.textPlaceName);
        textAddress = (TextView) view.findViewById(R.id.textAddress);
        textPhone = (TextView) view.findViewById(R.id.textPhone);
        textPlaceName.setText(mCafe.place_name);
        if(mCafe.address_name.length()!=0) {
            textAddress.setText(" "+ mCafe.address_name);
        }
        if(mCafe.phone.length()!=0) {
            textPhone.setText(" "+mCafe.phone);
        }
        //rating
        ratingBar01= (RatingBar) view.findViewById(R.id.ratingBar01);
        ratingBar02= (RatingBar) view.findViewById(R.id.ratingBar02);
        ratingBar03= (RatingBar) view.findViewById(R.id.ratingBar03);
        textStar01=(TextView)view.findViewById(R.id.textStar01);
        textStar02=(TextView)view.findViewById(R.id.textStar02);
        textStar03=(TextView)view.findViewById(R.id.textStar03);
        //사장 입력
        businessHour=(TextView) view.findViewById(R.id.businessHour);
        price=(TextView) view.findViewById(R.id.price);
        cafeGame=(TextView)view.findViewById(R.id.cafeGame);
        cafeGameList=(TextView)view.findViewById(R.id.cafeGameList);

        //db
        db=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

        /*별점 입력*/
        input=(Button) view.findViewById(R.id.button_input);
        input.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //info 닫기
                dismiss();
                new Dialog_Input(mCafe).show(getFragmentManager(), Dialog_Input.TAG_EVENT_DIALOG);
            }
        });

        /*사장님*/
        owner=(Button) view.findViewById(R.id.button_owner);
        announce=(TextView)view.findViewById(R.id.announce);
        announce.setVisibility(View.INVISIBLE);
        owner.setVisibility(View.INVISIBLE);
        //info 닫기
        db.collection("CEO").document(UserEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //이 유저는 사장이 맞습니다.
                        if(document.getString("cafeId").equals(mCafe.id)){

                            owner.setVisibility(View.VISIBLE);
                        }
                        else{
                            //해당 카페 사장이 아닙니다.
                            announce.setVisibility(View.VISIBLE);
                            //Toast.makeText(getActivity(),"해당 카페 사장이 아닙니다.",Toast.LENGTH_LONG).show();
                        }
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        // Log.d(TAG, "No such document");
                        //이 유저는 사장이 아닙니다.
                        announce.setVisibility(View.VISIBLE);
                        //Toast.makeText(getActivity(),"사장 인증 후 이용하실 수 있습니다.",Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        owner.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //화면 창 띄우기
                dismiss();
                new Dialog_Owner(mCafe).show(getFragmentManager(), Dialog_Input.TAG_EVENT_DIALOG);
            }
        });

        /*좋아요*/
        //db=FirebaseFirestore.getInstance();
        favorite= (Button) view.findViewById(R.id.favorite);
        favorite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionReference mPostReference =
                        (CollectionReference) db.collection("member").document(UserEmail)
                                .collection("LikeCafe");
                mPostReference
                        .document(mCafe.id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(getContext(), "이미 추가된 카페입니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //내 컬랙션에 추가하지 않은 카페
                                        //카페 데이터 유저 db 에 추가
                                        BoardCafe boardCafe=mCafe;
                                        //Item item =new Item(1,"2","3");
                                        //mPostReference.document(MapView.cafe_map.get(mI).id).set(item);
                                        mPostReference.document(mCafe.id).set(boardCafe);
                                        //cafe DB에 추가하지 않은 카페일때
                                        db.collection("cafe").document(mCafe.id).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                //카페 db 에 이미 추가된 카페일때
                                                                //likeNum++
                                                                db.collection("cafe").document(mCafe.id).update("likeNum", FieldValue.increment(1));
                                                            } else {
                                                                //카페 db 에 추가하지 않은 카페일때
                                                                cafeDB cafe=new cafeDB(mCafe.place_name,0,
                                                                        0, 0,0, new ArrayList<>(), "", "",mCafe.address_name,
                                                                        mCafe.phone);
                                                                db.collection("cafe").document(mCafe.id).set(cafe);
                                                                //좋아요 field 추가
                                                                db.collection("cafe").document(mCafe.id).update("likeNum",1);
                                                            }
                                                        }
                                                        else{
                                                            Toast.makeText(getContext(),"",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        Toast.makeText(getActivity(), mCafe.place_name+"like list 에 추가했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //Log.d(TAG, "get failed with ", task.getException());

                                }
                            }
                        });

            }
        });



        return view;
    }
    @Override
    public void onClick(View view) {
        dismiss();
    }
}