package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoWindowDialog extends androidx.fragment.app.DialogFragment implements View.OnClickListener{
    public static final String TAG_EVENT_DIALOG="dialog_event";
    static int mI=-1;
    //db
    static float mAvg_num_game;
    static float mAvg_clean;
    static float mAvg_service;
    RatingBar ratingBar01;
    RatingBar ratingBar02;
    RatingBar ratingBar03;
    TextView textStar01;
    TextView textStar02;
    TextView textStar03;
    //api
    static String mPlaceName="";
    static String mAddress="";
    static String mPhone="";
    TextView textPlaceName;
    TextView textAddress;
    TextView textPhone;
    //별점 입력& 게임 입력
    Button input;


    public static InfoWindowDialog getInfoWindowDialog(int cafe_i,
                                                       float avg_num_game, float avg_clean, float avg_service,
                                                       String name, String addr, String phone){
        InfoWindowDialog info= new InfoWindowDialog();
        mI=cafe_i;
        //db
        mAvg_num_game=avg_num_game;
        mAvg_clean=avg_clean;
        mAvg_service=avg_service;
        //api
        mPlaceName=name;
        mAddress=addr;
        mPhone=phone;

        return info;
    }
    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.item_point, container);
        //db
        ratingBar01= (RatingBar) view.findViewById(R.id.ratingBar01);
        ratingBar02= (RatingBar) view.findViewById(R.id.ratingBar02);
        ratingBar03= (RatingBar) view.findViewById(R.id.ratingBar03);
        textStar01=(TextView)view.findViewById(R.id.textStar01);
        textStar02=(TextView)view.findViewById(R.id.textStar02);
        textStar03=(TextView)view.findViewById(R.id.textStar03);
        ratingBar01.setRating(mAvg_num_game);
        ratingBar02.setRating(mAvg_clean);
        ratingBar03.setRating(mAvg_service);
        int temp=0;
        temp=(int)(mAvg_num_game*10);
        textStar01.setText(""+temp/(float)10);
        temp=(int)(mAvg_clean*10);
        textStar02.setText(""+temp/(float)10);
        temp=(int)(mAvg_service*10);
        textStar03.setText(""+temp/(float)10);
        //api
        textPlaceName = (TextView) view.findViewById(R.id.textPlaceName);
        textAddress = (TextView) view.findViewById(R.id.textAddress);
        textPhone = (TextView) view.findViewById(R.id.textPhone);
        textPlaceName.setText(mPlaceName);
        if(mAddress.length()!=0) {
            textAddress.setText(" "+mAddress);
        }
        if(mPhone.length()!=0) {
            textPhone.setText(" "+mPhone);
        }

        //FirebaseFirestore db=FirebaseFirestore.getInstance();
        /*별점 입력& 게임 입력*/
        input=(Button) view.findViewById(R.id.button_input);
        input.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //info 닫기
                dismiss();
                DialogFragment d = DialogFragment.getInstance(mI);
                //별점창 띄우기
                d.show(getFragmentManager(), DialogFragment.TAG_EVENT_DIALOG);


            }
        });

        return view;
    }
    @Override
    public void onClick(View view) {
        dismiss();
    }
}