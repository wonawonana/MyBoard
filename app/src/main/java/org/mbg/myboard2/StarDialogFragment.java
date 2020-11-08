package org.mbg.myboard2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


public class StarDialogFragment extends DialogFragment implements View.OnClickListener {
    FirebaseFirestore db;

    public static final String TAG_EVENT_DIALOG="dialog_event";
    //getRating
    float num_game=0;
    float clean=0;
    float service=0;
    static int i=-1;
    RatingBar ratingBar1;
    boolean NUM_GAME= false;

    public StarDialogFragment() {}
    public static StarDialogFragment getInstance(int cafe_i){
        StarDialogFragment e= new StarDialogFragment();
        i=cafe_i;
        return e;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        db = FirebaseFirestore.getInstance();

        View v= inflater.inflate(R.layout.dialog_star, container);
        ratingBar1=(RatingBar) v.findViewById(R.id.ratingBar1);
        RatingBar ratingBar2=(RatingBar) v.findViewById(R.id.ratingBar2);
        RatingBar ratingBar3=(RatingBar) v.findViewById(R.id.ratingBar3);
        Button mConfirmBtn= (Button) v.findViewById(R.id.btn);
        ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                num_game = ratingBar1.getRating();
                mConfirmBtn.setText(num_game + "/" + clean + "/" + service);
            }
        });
        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                clean=ratingBar2.getRating();
                mConfirmBtn.setText(num_game+"/"+clean+"/"+service);
            }
        });
        ratingBar3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                service=ratingBar3.getRating();
                mConfirmBtn.setText(num_game+"/"+clean+"/"+service);
            }
        });

        mConfirmBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num_game > 0 && clean > 0 && service > 0) {
                    star();
                    addDB();        //DB에 추가
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "모든 항목을 평가해야 합니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    public void star(){
        //게임 수
        MapView.cafe_map.get(i).star_num_game=num_game;
        MapView.cafe_map.get(i).star_clean=clean;
        MapView.cafe_map.get(i).star_service=service;

    }

    public void addDB(){
        //MapView.cafe_map.get(i).id
        //1. db 에 존재하지 않는 id 일때 문서 추가 하고 num 1로 초기 설정
        //2. db 에 존재하는 id 일때 문서 업데이트

        CollectionReference PostRef = (CollectionReference) db.collection("cafe");
        PostRef
            .document(MapView.cafe_map.get(i).id)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //카페 존재합니다
                            //문서 업데이트 해줌 = 각 num++1 , 평균값
                            cafeDB cafedata=document.toObject(cafeDB.class);
                            float cleanAvg= cafedata.getStarClean();
                            float gameNumAvg=cafedata.getStarNumGame();
                            float serviceAvg=cafedata.getStarService();
                            int count=cafedata.getCount(); //체크한 인원수
                            cleanAvg=cleanAvg*count+MapView.cafe_map.get(i).star_clean;
                            cleanAvg/=(count+1);
                            gameNumAvg=gameNumAvg*count+MapView.cafe_map.get(i).star_num_game;
                            gameNumAvg/=(count+1);
                            serviceAvg=serviceAvg*count+MapView.cafe_map.get(i).star_service;
                            serviceAvg/=(count+1);

                            DocumentReference docRef=db.collection("cafe").document(MapView.cafe_map.get(i).id);
                            docRef.update("starClean",cleanAvg);
                            docRef.update("starNumGame",gameNumAvg);
                            docRef.update("starService",serviceAvg);
                            docRef.update("count", FieldValue.increment(1)); //인원++1
                           // Toast.makeText(getContext(), "update 완료", Toast.LENGTH_LONG).show();
                        } else {
                            //카페 존재 안함
                            // 추가해줌 = clean,게임많은지,서비스 ,, 각 인원
                            cafeDB cafe=new cafeDB(MapView.cafe_map.get(i).place_name,MapView.cafe_map.get(i).star_clean,
                                    MapView.cafe_map.get(i).star_num_game, MapView.cafe_map.get(i).star_service,1);
                            db.collection("cafe").document(MapView.cafe_map.get(i).id).set(cafe);
                            //Toast.makeText(getActivity(), "cafe 없어서 추가했음"+MapView.cafe_map.get(i).id, Toast.LENGTH_LONG).show();
                        }
                    } else {
                    }
                }
            });

    }

}