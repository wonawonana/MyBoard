package org.mbg.myboard2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;

public class FragmentLike extends Fragment {

    FirebaseFirestore db;
    String UserEmail;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    private ArrayList<LikeData> mDataset;       //class list

    ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

        context=container.getContext();
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_like,container,false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.like_recycler_view);
        //사이즈 고정
        recyclerView.setHasFixedSize(true);

        // 4-1 리사이클러뷰에 레이아웃매니저 객체 지정.
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
        recyclerView.setLayoutManager(layoutManager);

        mDataset=new ArrayList<>();

        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeGame");
        mPostReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable  QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(context,"Error loading document",Toast.LENGTH_LONG).show();
                            return;
                        }
                        mDataset.clear();
                        for (QueryDocumentSnapshot doc : snapshot) {    //?
                                LikeData likeData=new LikeData(doc.getId(),doc.getString("memo"));
                                mDataset.add(likeData);
                        }
                        //어답터 갱신
                        mAdapter.notifyDataSetChanged();
                    }
                });



        mAdapter = new likeAdapter(getActivity(),mDataset);
        recyclerView.setAdapter(mAdapter);

        return viewGroup;
    }

}
