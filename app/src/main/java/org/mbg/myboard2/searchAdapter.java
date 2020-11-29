package org.mbg.myboard2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.HashMap;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.MyViewHolder>{
    private ArrayList<BoardCafe> mSearch_result;
    Context mContext;

    FragmentManager mFragmentManager;

    //db
    FirebaseFirestore db;
    FirebaseUser user;
    String UserEmail;

    public searchAdapter(ArrayList<BoardCafe> mSearch_result, Context mContext, FragmentManager mFragmentManager){
        this.mSearch_result = mSearch_result;
        this.mContext=mContext;
        this.mFragmentManager=mFragmentManager;
        //db
        db=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtCafeName; //카페 이름
        public TextView txtCafeAddr;   //카페 주소
        public ImageButton mapButton;
        public Button favorite;

        //생성자
        public MyViewHolder(View v) {
            super(v);
            // 뷰 객체에 대한 참조. (hold strong reference)
            // 내 생각에 이거 각 아이템 뷰 내용을 참조하는듯
            txtCafeName = v.findViewById(R.id.place_name_search);
            txtCafeAddr = v.findViewById(R.id.address_name_search);
            mapButton=(ImageButton)v.findViewById(R.id.mapButton_search);
            favorite=(Button)v.findViewById(R.id.favorite_search);
        }
    }
    @NonNull
    @Override
    public searchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_search, parent, false);
        searchAdapter.MyViewHolder vh_search = new searchAdapter.MyViewHolder(v);
        return vh_search;
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.MyViewHolder holder, int position) {
        String cafeName = (String) mSearch_result.get(position).getPlace_name();
        String cafeAddr = (String) mSearch_result.get(position).getAddress_name();

        //이걸 MapView로 전달하면 좋을 텐데
        Double cafeY=(Double)mSearch_result.get(position).getY();
        Double cafeX=(Double)mSearch_result.get(position).getX();

        holder.txtCafeName.setText(cafeName);
        holder.txtCafeAddr.setText(cafeAddr);

        //버튼 누르면 바로 해당 위치로 리턴하는걸 여기다 쓰는게 좋을 듯.
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*마킹*/
                Marker marker = new Marker();
                //마커 위치 지정: board_cafe y,x
                marker.setPosition(new LatLng(cafeY, cafeX));
                //마커 캡션 지정: board_cafe place_name
                marker.setCaptionText(cafeName);
                marker.setCaptionColor(Color.rgb(0, 100, 0));
                //마커 크기 지정
                marker.setWidth(70);
                marker.setHeight(100);
                //맵에 표시
                marker.setMap(MapView.map);
                db.collection("cafe").document(mSearch_result.get(position).id)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    //Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    ArrayList<String> gamelist= (ArrayList<String>) snapshot.get("cafeGameList");
                                    if(gamelist!=null ){
                                        if(gamelist.size()>0){
                                            marker.setIcon(MarkerIcons.RED);

                                        }
                                    }


                                } else {
                                    //Log.d(TAG, "Current data: null");
                                    //data 가 null 일때
                                }
                            }
                        });
                /*다이얼로그*/
                InfoWindowDialog_search infoWindowDialog_search= new InfoWindowDialog_search(mSearch_result.get(position));
                marker.setOnClickListener(overlay -> {
                    db.collection("cafe").document(mSearch_result.get(position).id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            cafeDB cafedata = document.toObject(cafeDB.class);
                                            /*infoDialog 열 때 별점 세팅*/
                                            infoWindowDialog_search.ratingBar01.setRating(cafedata.getStarNumGame());
                                            infoWindowDialog_search.textStar01.setText(
                                                    ""+(int)(cafedata.getStarNumGame()*10)/(float)10);
                                            infoWindowDialog_search.ratingBar02.setRating(cafedata.getStarClean());
                                            infoWindowDialog_search.textStar02.setText(
                                                    ""+(int)(cafedata.getStarClean()*10)/(float)10);
                                            infoWindowDialog_search.ratingBar03.setRating(cafedata.getStarService());
                                            infoWindowDialog_search.textStar03.setText(
                                                    ""+(int)(cafedata.getStarService()*10)/(float)10);
                                            /*infoDialog 열 때 이용시간&이용가격&게임종류 세팅*/
                                            infoWindowDialog_search.businessHour.setText(
                                                    cafedata.getBusinessHour());
                                            infoWindowDialog_search.price.setText(
                                                    cafedata.getPrice());

                                            if(cafedata.getCafeGameList().size()!=0) {
                                                //infoWindowDialogs.get(finalI).cafeGame.setText("보드게임 종류");
                                                ArrayList<String> cafeGameList= cafedata.getCafeGameList();
                                                String str_cafeGameList="";
                                                for(int i=0;i<cafeGameList.size();i++){
                                                    str_cafeGameList+=cafeGameList.get(i)+"\n";
                                                }
                                                infoWindowDialog_search.cafeGameList.setText(
                                                        str_cafeGameList
                                                );
                                            }

                                        } else {

                                        }
                                    } else {
                                    }
                                }
                            });
                    infoWindowDialog_search.show(mFragmentManager, InfoWindowDialog.TAG_EVENT_DIALOG);

                    return false;
                });



                Search.searchDialog.dismiss();
                MapView.map.moveCamera(CameraUpdate.scrollTo(new LatLng(cafeY, cafeX)));
                MapView.map.setMaxZoom(20);
            }
        });
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,String> map=new HashMap<>();

                CollectionReference mPostReference =
                        (CollectionReference) db.collection("member").document(UserEmail)
                                .collection("LikeCafe");
                mPostReference
                        .document(mSearch_result.get(position).id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(mContext, "이미 추가된 카페입니다.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        //내 컬랙션에 추가하지 않은 카페
                                        //카페 데이터 유저 db 에 추가
                                        map.put("placeName", mSearch_result.get(position).place_name);
                                        map.put("addressName", mSearch_result.get(position).address_name);
                                        BoardCafe boardCafe=mSearch_result.get(position);
                                        //Item item =new Item(1,"2","3");
                                        //mPostReference.document(MapView.cafe_map.get(mI).id).set(item);
                                        mPostReference.document(mSearch_result.get(position).id).set(boardCafe);

                                        //cafe DB에 추가하지 않은 카페일때
                                        db.collection("cafe").document(mSearch_result.get(position).id).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                //카페 db 에 이미 추가된 카페일때
                                                                //likeNum++
                                                                db.collection("cafe").document(mSearch_result.get(position).id).update("likeNum", FieldValue.increment(1));
                                                            } else {
                                                                //카페 db 에 추가하지 않은 카페일때
                                                                cafeDB cafe=new cafeDB(mSearch_result.get(position).place_name,0,
                                                                        0, 0,0, new ArrayList<>(), "", "",mSearch_result.get(position).address_name,
                                                                        mSearch_result.get(position).phone);
                                                                db.collection("cafe").document(mSearch_result.get(position).id).set(cafe);
                                                                //좋아요 field 추가
                                                                db.collection("cafe").document(mSearch_result.get(position).id).update("likeNum",1);
                                                            }
                                                        }
                                                        else{
                                                            Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        Toast.makeText(mContext, mSearch_result.get(position).place_name+"like list 에 추가했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //Log.d(TAG, "get failed with ", task.getException());

                                }
                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return mSearch_result.size();
    }





}