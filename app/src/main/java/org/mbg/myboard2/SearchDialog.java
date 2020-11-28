package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;

import java.util.ArrayList;

public class SearchDialog extends androidx.fragment.app.DialogFragment implements View.OnClickListener{
    public static final String TAG_EVENT_DIALOG="dialog_event";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView list;
    private RecyclerView.Adapter recyclerAdapter;

    ArrayList<BoardCafe> mSearch_result;


    /*String str_place_name;
    String str_address_name;
    double y;
    double x;
    TextView place_name;
    TextView address_name;
    Button location;*/
    /*public SearchDialog(String place, String address, double y, double x){
        str_place_name=place;
        str_address_name=address;
        this.y=y;
        this.x=x;
    }*/
    public SearchDialog(ArrayList<BoardCafe> search_result){
        mSearch_result=search_result;
    }


    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_search, container);
        recyclerView  = (RecyclerView)view.findViewById(R.id.recyclerSearch);
        layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new searchAdapter(mSearch_result, getActivity());
        recyclerView.setAdapter(mAdapter);
/*



        place_name=(TextView)view.findViewById(R.id.searched_name);
        address_name=(TextView)view.findViewById(R.id.searched_address);
        location=(Button)view.findViewById(R.id.location);

        place_name.setText(str_place_name);
        address_name.setText(str_address_name);

        location.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                MapView.map.moveCamera(CameraUpdate.scrollTo(new LatLng(y,x)));
            }
        });*/
        return view;
    }



    @Override
    public void onClick(View view) {
        dismiss();
    }
}