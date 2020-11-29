package org.mbg.myboard2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;

import java.util.ArrayList;

public class cafeFavoriteAdapter extends RecyclerView.Adapter<cafeFavoriteAdapter.MyViewHolder> {

    private ArrayList<BoardCafe> mDataset;
    private Context context;
    //private int imgSet;
    FirebaseFirestore db;
    String UserEmail;
    BoardCafe temp;



    //생성자, 인스턴스 1개 생성
    public cafeFavoriteAdapter(Context context, ArrayList<BoardCafe> myDataset) {
        this.context=context;
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();       //...?여기다 넣어도 되는건가?
        //this.imgSet=imgSet;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

    }

    // 3-0 아이템 뷰를 저장하는 뷰홀더 클래스. (각 하나)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtCafeName; //카페 이름
        public TextView txtCafeAddr;   //카페 주소
        public ImageButton deleteButton;
        public ImageButton mapButton;
        public TextView txtCafeLikeNum;

        //생성자
        public MyViewHolder(View v) {
            super(v);
            // 뷰 객체에 대한 참조. (hold strong reference)
            // 내 생각에 이거 각 아이템 뷰 내용을 참조하는듯
            txtCafeName = v.findViewById(R.id.place_name_search);
            txtCafeAddr = v.findViewById(R.id.address_name_search);
            deleteButton = v.findViewById(R.id.button);
            mapButton=(ImageButton)v.findViewById(R.id.mapButton_search);
            //txtCafeLikeNum=v.findViewById(R.id.textView25);
        }
    }

    // Create new views (invoked by the layout manager)
    // 3-2 뷰홀더와 아이템뷰 xml 연결
    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    //뷰 홀더 생성
    @Override
    public  cafeFavoriteAdapter.MyViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //어떤 뷰? 아이템 뷰 네모네모(만들어 놓은거)
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_favorite, parent, false);

        //뷰 홀더 객체 생성 <- 아이템 뷰 하나 생성자로 전달
        //드디어 뷰 홀더에 뷰를 전달합니다.
        cafeFavoriteAdapter.MyViewHolder vh = new cafeFavoriteAdapter.MyViewHolder(v);
        return vh;  //하나의 아이템 뷰 참조하는 뷰 홀더 하나
    }

    @Override
    public void onBindViewHolder(final cafeFavoriteAdapter.MyViewHolder holder, int position){
        String cafeId= (String) mDataset.get(position).getId();
        String cafeName = (String) mDataset.get(position).getPlace_name();
        String cafeAddr = (String) mDataset.get(position).getAddress_name();
        BoardCafe cafe=(BoardCafe)mDataset.get(position);
        //temp= (BoardCafe) mDataset.get(position);

        //이걸 MapView로 전달하면 좋을 텐데
        Double cafeY=(Double)mDataset.get(position).getY();
        Double cafeX=(Double)mDataset.get(position).getX();

        holder.txtCafeName.setText(cafeName);
        holder.txtCafeAddr.setText(cafeAddr);

        //holder.txtCafeLikeNum.setText((String)mDataset.get(position).getLikeNum());


        //버튼 누르면 바로 해당 위치로 리턴하는걸 여기다 쓰는게 좋을 듯.
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapView.drawerLayout.closeDrawer(GravityCompat.START);
                //map_ 이동
                MapView.map.moveCamera(CameraUpdate.scrollTo(new LatLng(cafeY, cafeX)));
                MapView.map.setMaxZoom(21);
            }
        });
        //삭제 눌렀을때
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                cafeDelete(mDataset.get(position).getId().toString());
            }
        });

    }

    void cafeDelete(String id){
        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeCafe");
        mPostReference
                .document(id)
                .delete();
    }

    // Return the size of your dataset (invoked by the layout manager)
    //전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}