package org.mbg.myboard2;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {

    //**
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //**

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    private ArrayList<GameData> mDataset;       //class list

    private TextView searchText;
    private Spinner spinnerGenre;
    private Spinner spinnerTime;
    private Spinner spinnerNum;
    private ImageButton searchButton;
    private ImageButton searchButton2;

    ViewGroup viewGroup;

    private String genre;
    private int gnum;
    private String gtime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //**
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //**

        context=container.getContext();

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_search,container,false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.my_recycler_view);
        searchText=viewGroup.findViewById(R.id.editTextTextPersonName2);
        spinnerGenre=viewGroup.findViewById(R.id.spinner2);
        spinnerTime=viewGroup.findViewById(R.id.spinner3);
        spinnerNum=viewGroup.findViewById(R.id.spinner4);
        searchButton=viewGroup.findViewById(R.id.searchButton);
        searchButton2=viewGroup.findViewById(R.id.searchButton2);

        searchText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);



        //사이즈 고정
        recyclerView.setHasFixedSize(true);

        // 4-1 리사이클러뷰에 레이아웃매니저 객체 지정.
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
        recyclerView.setLayoutManager(layoutManager);


        //장르 , 시간 , 인원 정하기

        //spinner
        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genre =parent.getItemAtPosition(position).toString();
                if(position==0)
                    genre="전체";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genre="전체";
            }
        });
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        gtime="전체";
                        break;
                    case 1:
                        gtime="gtimeless30";
                        break;
                    case 2:
                        gtime="gtime30_60";
                        break;
                    case 3:
                        gtime="gtime60_90";
                        break;
                    case 4:
                        gtime="gtime90_120";
                        break;
                    case 5:
                        gtime="gtimemore120";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gtime="전체";
            }
        });
        spinnerNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gnum=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gnum=0;
            }
        });

        searchButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(genre=="전체"&&gtime=="전체"&&gnum==0)
                    searchAll();
                else if(gtime=="전체"&&gnum==0){
                    searchGenre(genre);}
                else if(gtime=="전체"&&genre=="전체")
                    searchNum(gnum);
                else if(gnum==0&&genre=="전체")
                    searchTime(gtime);
                else if(gtime=="전체")
                    searchGenre_Num(genre,gnum);
                else if(gnum==0)
                    searchGenre_Time(genre,gtime);
                else if(genre=="전체")
                    searchTime_Num(gtime,gnum);
                else
                    searchFunction(genre,gtime,gnum);
                //Toast.makeText(context,gtime,Toast.LENGTH_LONG).show();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!TextUtils.isEmpty(searchText.getText()))
                    searchName(searchText.getText().toString());
                else
                    Toast.makeText(context,"이름을 입력해 주세요",Toast.LENGTH_LONG).show();
            }
        });

        mDataset=new ArrayList<>();




        //int imgSet=R.drawable.dice;
        //Toast.makeText(context,Integer.toString(mDataset.size()),Toast.LENGTH_LONG).show();
        mAdapter = new gameAdapter(getActivity(),mDataset);
        recyclerView.setAdapter(mAdapter);


        return viewGroup;
    }

    public void searchName(String name){

        if(name.matches(".*[가-힣]+.*")){
            //한글

            db.collection("BoardGame").orderBy("gnameKOR").startAt(name).endAt(name+'\uf8ff')
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mDataset.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    GameData gameData=(document.toObject(GameData.class));
                                    mDataset.add(gameData);
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }
        else{
            db.collection("BoardGame").orderBy("gnameENG").startAt(name).endAt(name+'\uf8ff')
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mDataset.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    GameData gameData=(document.toObject(GameData.class));
                                    mDataset.add(gameData);
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }

    public void searchAll(){
        db.collection("BoardGame")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context,"Error loading document",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void searchGenre(String genre){
        db.collection("BoardGame").whereEqualTo("genre", genre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void searchNum(int gnum){
        db.collection("BoardGame").whereArrayContains("gnum",gnum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void searchTime(String gtime){
        db.collection("BoardGame").whereEqualTo(gtime, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void searchGenre_Num(String genre,int gnum){
        db.collection("BoardGame")
                .whereEqualTo("genre", genre).whereArrayContains("gnum",gnum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();   //리스트 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            //어댑터 갱신
                            mAdapter.notifyDataSetChanged();        //야발 이놈 때문이었군
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void searchTime_Num(String gtime,int gnum){
        db.collection("BoardGame")
                .whereArrayContains("gnum",gnum).whereEqualTo(gtime, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();   //리스트 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            //어댑터 갱신
                            mAdapter.notifyDataSetChanged();        //야발 이놈 때문이었군
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void searchGenre_Time(String genre,String gtime){
        db.collection("BoardGame")
                .whereEqualTo("genre", genre).whereEqualTo(gtime, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();   //리스트 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameData gameData=(document.toObject(GameData.class));
                                mDataset.add(gameData);
                            }
                            //어댑터 갱신
                            mAdapter.notifyDataSetChanged();        //야발 이놈 때문이었군
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void searchFunction(String genre,String gtime,int gnum){


        // 4-2 리사이클러뷰에 어댑터 객체 지정. 데이터 리스트를 전부 어댑터에 보냄
        // specify an adapter (see also next example)


        db.collection("BoardGame")
                .whereEqualTo("genre", genre).whereArrayContains("gnum",gnum).whereEqualTo(gtime, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataset.clear();   //리스트 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                //list 에 add
                                //GenericTypeIndicator<List<GameData>> t = new GenericTypeIndicator<List<GameData>>() {};
                                GameData gameData=(document.toObject(GameData.class));
                                /*GameData gameData1=new GameData(document.getString("genre"),document.getString("genres",
                                        document.getString("gnameENG"),document.getString("gnameKOR"),
                                        document.getList))*/
                                mDataset.add(gameData);
                            }
                            //어댑터 갱신
                            mAdapter.notifyDataSetChanged();        //야발 이놈 때문이었군
                        } else {
                            Toast.makeText(context,"게임이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
