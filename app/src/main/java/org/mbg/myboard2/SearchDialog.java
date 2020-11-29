package org.mbg.myboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
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
    FragmentManager mFragmentManager;

    ArrayList<BoardCafe> mSearch_result;
    public SearchDialog(ArrayList<BoardCafe> search_result, FragmentManager mFragmentManager){
        mSearch_result=search_result;
        this.mFragmentManager=mFragmentManager;
    }


    @Nullable
    @Override
    public  View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view;
        if(mSearch_result.size()!=0){
            view= inflater.inflate(R.layout.dialog_search, container);
            recyclerView  = (RecyclerView)view.findViewById(R.id.recyclerSearch);
            layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new searchAdapter(mSearch_result, getActivity(), mFragmentManager);
            recyclerView.setAdapter(mAdapter);
        }else{
            view= inflater.inflate(R.layout.dialog_search_null,container);
        }

        return view;
    }



    @Override
    public void onClick(View view) {
        dismiss();
    }
}