package org.mbg.myboard2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


public class DialogFragment extends androidx.fragment.app.DialogFragment implements View.OnClickListener {
    FirebaseFirestore db;

    public static final String TAG_EVENT_DIALOG="dialog_event";
    //getRating
    float num_game=0;
    float clean=0;
    float service=0;
    static int i=-1;
    boolean NUM_GAME= false;
    String str_game_name="";
    static Context mapContext;
    RatingBar ratingBar1;
    RatingBar ratingBar2;
    RatingBar ratingBar3;
    static boolean input_clicked=false;



    public static DialogFragment getInstance(int cafe_i){
        DialogFragment e= new DialogFragment();
        i=cafe_i;

        return e;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){

        View v= inflater.inflate(R.layout.dialog, container);

        /*별점 입력*/
        ratingBar1=(RatingBar) v.findViewById(R.id.ratingBar1);
        ratingBar2=(RatingBar) v.findViewById(R.id.ratingBar2);
        ratingBar3=(RatingBar) v.findViewById(R.id.ratingBar3);
        Button mConfirmBtn= (Button) v.findViewById(R.id.button_star);

        //'별점 입력' 버튼 클릭
        mConfirmBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar1.getRating() > 0 && ratingBar2.getRating() > 0 && ratingBar3.getRating() > 0) {
                    addDB(ratingBar1.getRating(), ratingBar2.getRating(),ratingBar3.getRating());        //DB에 추가
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "모든 항목을 평가해야 합니다", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*게임 입력*/
        EditText game_name= (EditText) v.findViewById(R.id.gameName);
        Button input_button= (Button)v.findViewById(R.id.button_game_name);
        input_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_game_name= game_name.getText().toString();
                input_game_name();
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }


    public void input_game_name(){
        //게임 수
        if(!str_game_name.equals("")) {
            if(!str_game_name.trim().equals("")) {
                MapView.cafe_map.get(i).input_game_name=str_game_name;
            }
        }


    }
    public void addDB(float newGameNum, float newClean, float newService){
        db = FirebaseFirestore.getInstance();
        /*db에 별점 입력*/
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
                                float gameNumAvg=cafedata.getStarNumGame();
                                float cleanAvg= cafedata.getStarClean();
                                float serviceAvg=cafedata.getStarService();
                                int count=cafedata.getCount(); //체크한 인원수
                                gameNumAvg=gameNumAvg*count+newGameNum;
                                gameNumAvg/=(count+1);
                                cleanAvg=cleanAvg*count+newClean;
                                cleanAvg/=(count+1);
                                serviceAvg=serviceAvg*count+newService;
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
                                cafeDB cafe=new cafeDB(MapView.cafe_map.get(i).place_name,newClean,
                                        newGameNum, newService,1);
                                db.collection("cafe").document(MapView.cafe_map.get(i).id).set(cafe);
                                //Toast.makeText(getActivity(), "cafe 없어서 추가했음"+MapView.cafe_map.get(i).id, Toast.LENGTH_LONG).show();
                            }
                        } else {
                        }
                    }
                });



    }



}