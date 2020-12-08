package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class InfoWindowDialog_home extends androidx.fragment.app.DialogFragment implements View.OnClickListener{
    public static final String TAG_EVENT_DIALOG="dialog_event";
    //int mI;
    //private BoardCafe mCafe;
    //private cafeDB mCafe_ranked;
    //private String mCafe_ranked_id;
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
    //좋아요
    Button favorite;
    //db
    FirebaseFirestore db;
    FirebaseUser user;
    String UserEmail;



    public InfoWindowDialog_home(BoardCafe cafe){
        mCafe=cafe;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        //db
        db= FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();
        View view= inflater.inflate(R.layout.dialog_cafe_home, container);
        //api
        textPlaceName = (TextView) view.findViewById(R.id.textPlaceName_home);
        textAddress = (TextView) view.findViewById(R.id.textAddress_home);
        textPhone = (TextView) view.findViewById(R.id.textPhone_home);
        //api설정
        textPlaceName.setText(mCafe.place_name);
        if(mCafe.address_name.length()!=0) {
            textAddress.setText(" "+ mCafe.address_name);
        }
        if(mCafe.phone.length()!=0) {
            textPhone.setText(" "+mCafe.phone);
        }
        //rating
        ratingBar01= (RatingBar) view.findViewById(R.id.ratingBar01_home);
        ratingBar02= (RatingBar) view.findViewById(R.id.ratingBar02_home);
        ratingBar03= (RatingBar) view.findViewById(R.id.ratingBar03_home);
        textStar01=(TextView)view.findViewById(R.id.textStar01_home);
        textStar02=(TextView)view.findViewById(R.id.textStar02_home);
        textStar03=(TextView)view.findViewById(R.id.textStar03_home);
        //카페정보
        businessHour=(TextView) view.findViewById(R.id.businessHour_home);
        price=(TextView) view.findViewById(R.id.price_home);
        cafeGame=(TextView)view.findViewById(R.id.cafeGame_home);
        cafeGameList=(TextView)view.findViewById(R.id.cafeGameList_home);
        //rating & 카페정보 설정
        db.collection("cafe").document(mCafe.id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                cafeDB cafedata = document.toObject(cafeDB.class);
                                /*별점 세팅*/
                                ratingBar01.setRating(cafedata.getStarNumGame());
                                textStar01.setText(
                                        ""+(int)(cafedata.getStarNumGame()*10)/(float)10);
                                ratingBar02.setRating(cafedata.getStarClean());
                                textStar02.setText(
                                        ""+(int)(cafedata.getStarClean()*10)/(float)10);
                                ratingBar03.setRating(cafedata.getStarService());
                                textStar03.setText(
                                        ""+(int)(cafedata.getStarService()*10)/(float)10);
                                /*이용시간&이용가격&게임종류 세팅*/
                                businessHour.setText(
                                        cafedata.getBusinessHour());
                                price.setText(
                                        cafedata.getPrice());

                                if(cafedata.getCafeGameList().size()!=0) {
                                    //infoWindowDialogs.get(finalI).cafeGame.setText("보드게임 종류");
                                    ArrayList<String> array_str_cafeGameList= cafedata.getCafeGameList();
                                    String str_cafeGameList="";
                                    for(int i=0;i<array_str_cafeGameList.size();i++){
                                        str_cafeGameList+=array_str_cafeGameList.get(i)+"\n";
                                    }
                                    cafeGameList.setText(
                                            str_cafeGameList
                                    );
                                }

                            } else {

                            }
                        } else {
                        }
                    }
                });
      /*  ratingBar01.setRating(mCafe_ranked.getStarNumGame());
        textStar01.setText(""+(int)(mCafe_ranked.getStarNumGame()*10)/(float)10);
        ratingBar02.setRating(mCafe_ranked.getStarClean());
        textStar02.setText(""+(int)(mCafe_ranked.getStarClean()*10)/(float)10);
        ratingBar03.setRating(mCafe_ranked.getStarService());
        textStar03.setText(""+(int)(mCafe_ranked.getStarService()*10)/(float)10);
        businessHour.setText(  mCafe_ranked.getBusinessHour());
        price.setText( mCafe_ranked.getPrice());
        if(mCafe_ranked.getCafeGameList().size()!=0) {
            //infoWindowDialogs.get(finalI).cafeGame.setText("보드게임 종류");
            ArrayList<String> array_str_cafeGameList= mCafe_ranked.getCafeGameList();
            String str_cafeGameList="";
            for(int i=0;i<array_str_cafeGameList.size();i++){
                str_cafeGameList+=array_str_cafeGameList.get(i)+"\n";
            }
            cafeGameList.setText(
                    str_cafeGameList
            );
        }
*/

        /*좋아요*/
        favorite= (Button) view.findViewById(R.id.favorite_home);
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