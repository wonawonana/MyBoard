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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoWindowDialog_home_false extends androidx.fragment.app.DialogFragment implements View.OnClickListener{
    public static final String TAG_EVENT_DIALOG="dialog_event";
    private cafeDB mCafe_ranked;
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



    public InfoWindowDialog_home_false(cafeDB cafe_ranked){
        mCafe_ranked=cafe_ranked;
    }

    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.dialog_cafe_home, container);
        //api
        textPlaceName = (TextView) view.findViewById(R.id.textPlaceName_home);
        textAddress = (TextView) view.findViewById(R.id.textAddress_home);
        textPhone = (TextView) view.findViewById(R.id.textPhone_home);
        //api설정
        textPlaceName.setText(mCafe_ranked.getCafeName());
        if(mCafe_ranked.getAddress_name().length()!=0) {
            textAddress.setText(" "+ mCafe_ranked.getAddress_name());
        }
        if(mCafe_ranked.getPhone().length()!=0) {
            textPhone.setText(" "+mCafe_ranked.getPhone());
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
        ratingBar01.setRating(mCafe_ranked.getStarNumGame());
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

        favorite= (Button) view.findViewById(R.id.favorite_home);
        favorite.setVisibility(View.INVISIBLE);

        return view;
    }
    @Override
    public void onClick(View view) {
        dismiss();
    }
}