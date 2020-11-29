package org.mbg.myboard2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MapView extends Fragment implements OnMapReadyCallback{
    ViewGroup viewGroup;
    //위치
    FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    //db
    FirebaseFirestore db;
    String UserEmail;
    //navigation view
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private RecyclerView list;
    private RecyclerView.Adapter recyclerAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;
    SearchView searchView;
    //search_map
    String search_query="";

    /*static*/
    //favorite
    static private ArrayList<BoardCafe> mDataset;
    static ArrayList<BoardCafe> mDataset_marker;
    static ArrayList<Marker> markers_favorite;
    static ArrayList<InfoWindowDialog_favorite> infoWindowDialogs_favorite;
    //NaverMap 객체
    static NaverMap map;
    //FragmentMap에서 전달받은 데이터- 카페, 위도&경도
    static  double latitude, longitude;
    static ArrayList<BoardCafe> cafe_map=new ArrayList<BoardCafe>();
    static ArrayList<Marker> markers;
    static ArrayList<InfoWindowDialog> infoWindowDialogs;
    static DrawerLayout drawerLayout;

    static public ArrayList<BoardCafe> get_mDataset(){
        return  mDataset;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        /*db*/
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

        viewGroup = (ViewGroup) inflater.inflate(R.layout.map,container,false);
        /*drawerLayout: main_content & navigation view*/
        drawerLayout = (DrawerLayout) viewGroup.findViewById(R.id.dl_main_drawer_root);
        /*main_content*/
        toolbar=(Toolbar) viewGroup.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        //search_view
        searchView=(SearchView)viewGroup.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search search= new Search(query, getActivity(), getChildFragmentManager());
                try {
                    search.searchBoardCafeData();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        /*navigation view*/
        navigationView = (NavigationView) viewGroup.findViewById(R.id.nv_main_navigation_root) ;
        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        //리사이클러뷰
        recyclerView  = (RecyclerView)viewGroup.findViewById(R.id.recycler_map);
        layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
        recyclerView.setLayoutManager(layoutManager);
        //mDataset: 좋아요한 카페 리스트, ArrayList<BoardCafe>
        mDataset=new ArrayList<>();
        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeCafe");
        mPostReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable  QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(),"Error loading document",Toast.LENGTH_LONG).show();
                            return;
                        }
                        mDataset.clear();
                        for (QueryDocumentSnapshot doc : snapshot) {    //?
                            BoardCafe likeData=(doc.toObject(BoardCafe.class));
                            mDataset.add(likeData);
                            //Toast.makeText(getContext(),"추가됨",Toast.LENGTH_SHORT).show();;
                        }
                        //어답터 갱신
                        mAdapter.notifyDataSetChanged();
                    }
                });
        mAdapter = new cafeFavoriteAdapter(getActivity(),mDataset);
        recyclerView.setAdapter(mAdapter);

        /*데이터 받기*/
        cafe_map=getArguments().getParcelableArrayList("list");
        latitude= getArguments().getDouble("latitude", 0.0);
        longitude=getArguments().getDouble("longitude",0.0);
        /*네이버지도*/
        //사용자 현재 위치
        locationSource=new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return viewGroup;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //NaverMap 객체
        map=naverMap;
        /*사용자위치*/
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
        naverMap.moveCamera(cameraUpdate);
        /*cafe_map -> markers*/
        markers= new ArrayList<Marker>();
        for (int m = 0; m < cafe_map.size(); m++) {
            //마커 생성
            Marker marker = new Marker();
            //마커 위치 지정: board_cafe y,x
            marker.setPosition(new LatLng(cafe_map.get(m).y, cafe_map.get(m).x));
            //마커 캡션 지정: board_cafe place_name
            marker.setCaptionText(cafe_map.get(m).place_name);
            marker.setCaptionColor(Color.rgb(0, 100, 0));
            //마커 크기 지정
            marker.setWidth(70);
            marker.setHeight(100);
            //리스트에 마커 add
            markers.add(marker);
            //맵에 표시
            markers.get(m).setMap(naverMap);
        }
        /*mDataset_marker: mDataset과 cafe_map에서 겹치는 것 없애기*/
        //mDataset_marker=mDataset;
        mDataset_marker=new ArrayList<>();
        for(int a=0;a<mDataset.size();a++){
            mDataset_marker.add(mDataset.get(a));
        }
        for(int d=0;d<mDataset_marker.size();d++){
            for(int c=0;c<cafe_map.size();c++){
                if(cafe_map.get(c).getId().equals(mDataset_marker.get(d).id)){
                    mDataset_marker.remove(d);
                    d--;
                    break;
                }
            }
        }
        /*mDataset_marker -> markers*/
        markers_favorite= new ArrayList<Marker>();
        for (int m = 0; m < mDataset_marker.size(); m++) {
            //마커 생성
            Marker marker = new Marker();
            //마커 위치 지정: board_cafe y,x
            marker.setPosition(new LatLng(mDataset_marker.get(m).y, mDataset_marker.get(m).x));
            //마커 캡션 지정: board_cafe place_name
            marker.setCaptionText(mDataset_marker.get(m).place_name);
            marker.setCaptionColor(Color.rgb(0, 100, 0));
            //마커 크기 지정
            marker.setWidth(70);
            marker.setHeight(100);
            //리스트에 마커 add
            markers_favorite.add(marker);
            //맵에 표시
            markers_favorite.get(m).setMap(naverMap);
        }
        /*보드게임이 입력된 카페는 빨간색으로 마킹: markers&cafe_map*/
        for(int i=0;i<markers.size();i++) {
            int finalI = i;
            db.collection("cafe").document(cafe_map.get(i).id)
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
                                        markers.get(finalI).setIcon(MarkerIcons.RED);

                                    }
                                }


                            } else {
                                //Log.d(TAG, "Current data: null");
                                //data 가 null 일때
                            }
                        }
                    });
        }
        /*보드게임이 입력된 카페는 빨간색으로 마킹: markers_favorite & mDataset*/
        for(int i=0;i<markers_favorite.size();i++) {
            int finalI = i;
            db.collection("cafe").document(mDataset_marker.get(i).id)
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
                                        markers_favorite.get(finalI).setIcon(MarkerIcons.RED);
                                    }
                                }


                            } else {
                                //Log.d(TAG, "Current data: null");
                                //data 가 null 일때
                            }
                        }
                    });
        }

        /*infoWindowDialog*/
        infoWindowDialogs= new ArrayList<InfoWindowDialog>();
        for(int i=0;i<markers.size();i++) {
            infoWindowDialogs.add(new InfoWindowDialog(i));
        }

        /*마킹 클릭 -> db 읽어오기 -> 인포윈도우 띄우기: markers& cafe_map*/
        for (int i = 0; i < markers.size(); i++) {
            int finalI = i;
            markers.get(i).setOnClickListener(overlay -> {
                //setRating
                db.collection("cafe").document(cafe_map.get(finalI).id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        cafeDB cafedata = document.toObject(cafeDB.class);
                                        /*infoDialog 열 때 별점 세팅*/
                                        infoWindowDialogs.get(finalI).ratingBar01.setRating(cafedata.getStarNumGame());
                                        infoWindowDialogs.get(finalI).textStar01.setText(
                                                ""+(int)(cafedata.getStarNumGame()*10)/(float)10);
                                        infoWindowDialogs.get(finalI).ratingBar02.setRating(cafedata.getStarClean());
                                        infoWindowDialogs.get(finalI).textStar02.setText(
                                                ""+(int)(cafedata.getStarClean()*10)/(float)10);
                                        infoWindowDialogs.get(finalI).ratingBar03.setRating(cafedata.getStarService());
                                        infoWindowDialogs.get(finalI).textStar03.setText(
                                                ""+(int)(cafedata.getStarService()*10)/(float)10);
                                        /*infoDialog 열 때 이용시간&이용가격&게임종류 세팅*/
                                        infoWindowDialogs.get(finalI).businessHour.setText(
                                                cafedata.getBusinessHour());
                                        infoWindowDialogs.get(finalI).price.setText(
                                                cafedata.getPrice());

                                        if(cafedata.getCafeGameList().size()!=0) {
                                            //infoWindowDialogs.get(finalI).cafeGame.setText("보드게임 종류");
                                            ArrayList<String> cafeGameList= cafedata.getCafeGameList();
                                            String str_cafeGameList="";
                                            for(int i=0;i<cafeGameList.size();i++){
                                                str_cafeGameList+=cafeGameList.get(i)+"\n";
                                            }
                                            infoWindowDialogs.get(finalI).cafeGameList.setText(
                                                    str_cafeGameList
                                            );
                                        }

                                    } else {

                                    }
                                } else {
                                }
                            }
                        });

                infoWindowDialogs.get(finalI).show(getFragmentManager(), InfoWindowDialog.TAG_EVENT_DIALOG);

                return false;
            });
        }


        /*infoWindowDialog_favorite*/
        infoWindowDialogs_favorite= new ArrayList<InfoWindowDialog_favorite>();
        for(int i=0;i<markers_favorite.size();i++) {
            infoWindowDialogs_favorite.add(new InfoWindowDialog_favorite(i));
        }
        /*마킹 클릭 -> db 읽어오기 -> 인포윈도우 띄우기: markers_favorite & mDataset*/
        for (int i = 0; i < markers_favorite.size(); i++) {
            int finalI = i;
            markers_favorite.get(i).setOnClickListener(overlay -> {
                //setRating
                db.collection("cafe").document(mDataset_marker.get(finalI).id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        cafeDB cafedata = document.toObject(cafeDB.class);
                                        /*infoDialog 열 때 별점 세팅*/
                                        infoWindowDialogs_favorite.get(finalI).ratingBar01.setRating(cafedata.getStarNumGame());
                                        infoWindowDialogs_favorite.get(finalI).textStar01.setText(
                                                ""+(int)(cafedata.getStarNumGame()*10)/(float)10);
                                        infoWindowDialogs_favorite.get(finalI).ratingBar02.setRating(cafedata.getStarClean());
                                        infoWindowDialogs_favorite.get(finalI).textStar02.setText(
                                                ""+(int)(cafedata.getStarClean()*10)/(float)10);
                                        infoWindowDialogs_favorite.get(finalI).ratingBar03.setRating(cafedata.getStarService());
                                        infoWindowDialogs_favorite.get(finalI).textStar03.setText(
                                                ""+(int)(cafedata.getStarService()*10)/(float)10);
                                        /*infoDialog 열 때 이용시간&이용가격&게임종류 세팅*/
                                        infoWindowDialogs_favorite.get(finalI).businessHour.setText(
                                                cafedata.getBusinessHour());
                                        infoWindowDialogs_favorite.get(finalI).price.setText(
                                                cafedata.getPrice());
                                        ArrayList<String> cafeGameList= cafedata.getCafeGameList();
                                        String str_cafeGameList="";
                                        for(int i=0;i<cafeGameList.size();i++){
                                            str_cafeGameList+=cafeGameList.get(i)+"\n";
                                        }
                                        infoWindowDialogs_favorite.get(finalI).cafeGameList.setText(
                                                str_cafeGameList
                                        );


                                    } else {

                                    }
                                } else {
                                }
                            }
                        });
                infoWindowDialogs_favorite.get(finalI).show(getFragmentManager(), InfoWindowDialog.TAG_EVENT_DIALOG);

                return false;
            });
        }

    }


}