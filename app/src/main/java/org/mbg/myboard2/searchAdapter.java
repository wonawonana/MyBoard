package org.mbg.myboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;

import java.util.ArrayList;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.MyViewHolder>{
    private ArrayList<BoardCafe> mSearch_result;
    Context mContext;

    public searchAdapter(ArrayList<BoardCafe> mSearch_result, Context mContext){
        this.mSearch_result = mSearch_result;
        this.mContext=mContext;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtCafeName; //카페 이름
        public TextView txtCafeAddr;   //카페 주소
        public ImageButton mapButton;

        //생성자
        public MyViewHolder(View v) {
            super(v);
            // 뷰 객체에 대한 참조. (hold strong reference)
            // 내 생각에 이거 각 아이템 뷰 내용을 참조하는듯
            txtCafeName = v.findViewById(R.id.place_name_search);
            txtCafeAddr = v.findViewById(R.id.address_name_search);
            mapButton=(ImageButton)v.findViewById(R.id.mapButton_search);
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
        //String cafeId= (String) mSearch_result.get(position).
        String cafeName = (String) mSearch_result.get(position).getPlace_name();
        String cafeAddr = (String) mSearch_result.get(position).getAddress_name();
        //BoardCafe cafe=(BoardCafe)mDataset.get(position);
        //temp= (BoardCafe) mDataset.get(position);

        //이걸 MapView로 전달하면 좋을 텐데
        Double cafeY=(Double)mSearch_result.get(position).getY();
        Double cafeX=(Double)mSearch_result.get(position).getX();

        holder.txtCafeName.setText(cafeName);
        holder.txtCafeAddr.setText(cafeAddr);

        //버튼 누르면 바로 해당 위치로 리턴하는걸 여기다 쓰는게 좋을 듯.
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search.searchDialog.dismiss();
                MapView.map.moveCamera(CameraUpdate.scrollTo(new LatLng(cafeY, cafeX)));
                MapView.map.setMaxZoom(20);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSearch_result.size();
    }





}