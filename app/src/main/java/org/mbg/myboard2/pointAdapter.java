package org.mbg.myboard2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.map.overlay.InfoWindow;


public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
    FirebaseFirestore db;
    private final Context mContext;
    private final ViewGroup mParent;
    String mAddr="", mPhone="", mLink="", mPlaceName="";
    int mi=-1;

    //별점
    static float rating01=(float)0.0;
    static float rating02=(float)0.0;
    static float rating03=(float)0.0;
    //별점_txt
    static String txtStar01="0.0";
    static String txtStar02="0.0";
    static String txtStar03="0.0";

    public pointAdapter(@NonNull Context context, ViewGroup parent,
                        String name, String addr, String phone, String link, int i_cafe_map)
    {
        super(context);
        mPlaceName=name;
        mContext = context;
        mParent = parent;
        mAddr=addr;
        mPhone=phone;
        mLink= link;
        mi=i_cafe_map;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow)
    {
        db = FirebaseFirestore.getInstance();

        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_point, mParent, false);
        TextView txtPlaceName = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAddr = (TextView) view.findViewById(R.id.txtAddress);
        TextView txtTel = (TextView) view.findViewById(R.id.txtTel);
        TextView txtLink = (TextView) view.findViewById(R.id.txtLink);

        //주소, 번호, 링크
        if(mPlaceName.length()!=0){
            txtPlaceName.setText(""+mPlaceName+"");
        }
        if(mAddr.length()!=0) {
            txtAddr.setText(" "+mAddr);
        }
        if(mPhone.length()!=0) {
            txtTel.setText(" "+mPhone);
        }

        if(mLink.length()!=0) {
            txtLink.setText(" "+mLink);
        }

        /*별점, 별점_txt*/
        RatingBar ratingBar01=(RatingBar) view.findViewById(R.id.ratingBar01);
        RatingBar ratingBar02=(RatingBar) view.findViewById(R.id.ratingBar02);
        RatingBar ratingBar03=(RatingBar) view.findViewById(R.id.ratingBar03);
        TextView textStar01= (TextView)view.findViewById(R.id.textStar01);
        TextView textStar02= (TextView)view.findViewById(R.id.textStar02);
        TextView textStar03= (TextView)view.findViewById(R.id.textStar03);
        //rating, txtStar 값을 cafe_map.get(i)에서 가져오기

        /*num_game 평균 별점: rating01 clean평균 별점 rating02 service 평균 별점 rationg03*/
        db.collection("cafe").document(MapView.cafe_map.get(mi).id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                cafeDB cafedata=document.toObject(cafeDB.class);
                                rating01=cafedata.getStarNumGame();
                                rating02=cafedata.getStarClean();
                                rating03=cafedata.getStarService();
                            } else {
                                rating01=0; rating02=0; rating03=0;
                            }
                        } else {
                        }
                    }
                });
        int temp=0;
        temp=(int)(rating01*10);
        rating01=temp/(float)10;
        temp=(int)(rating02*10);
        rating02=temp/(float)10;
        temp=(int)(rating03*10);
        rating03=temp/(float)10;

        txtStar01=rating01+"";
        txtStar02=rating02+"";
        txtStar03=rating03+"";
        ratingBar01.setRating(rating01);
        ratingBar02.setRating(rating02);
        ratingBar03.setRating(rating03);
        textStar01.setText(txtStar01);
        textStar02.setText(txtStar02);
        textStar03.setText(txtStar03);

        /*게임 이름*/
        TextView GAME= (TextView) view.findViewById(R.id.GAME);
        TextView game_list= (TextView) view.findViewById(R.id.game_list);
        //입력된 보드 게임이 없을 경우
        if(MapView.cafe_map.get(mi).input_game_name.equals("")){
            game_list.setText("*인증된 사용자만 게임을 입력할 수 있습니다");
        }
        else {
            String str_game_list = "";
            str_game_list += MapView.cafe_map.get(mi).input_game_name;
            game_list.setText(str_game_list);
        }


        return view;
    }
}