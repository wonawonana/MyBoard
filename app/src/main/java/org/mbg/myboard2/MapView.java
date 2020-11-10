package org.mbg.myboard2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MapView extends Fragment implements OnMapReadyCallback{

    ViewGroup viewGroup;
    //위치
    FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    //FragmentMap에서 전달받은 데이터- 카페, 위도&경도
    static ArrayList<BoardCafe> cafe_map=new ArrayList<BoardCafe>();
    double latitude, longitude;
    //search_map
    String search_query="";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //지도 xml
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_map,container,false);
        //사용자 현재 위치
        locationSource=new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //데이터 받기
        cafe_map=getArguments().getParcelableArrayList("list");
        latitude= getArguments().getDouble("latitude", 0.0);
        longitude=getArguments().getDouble("longitude",0.0);

        //네이버지도
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
        /*사용자위치*/
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude)).animate(CameraAnimation.Fly);

        /*cafe_map -> markers*/
        ArrayList<Marker> markers = new ArrayList<Marker>();
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

        /*마크 클릭 -> InfoWindowDialog*/
        ArrayList<InfoWindowDialog> list_info= new ArrayList<InfoWindowDialog>();
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        for (int i = 0; i < markers.size(); i++) {
            int finalI = i;
            markers.get(i).setOnClickListener(overlay -> {
                db.collection("cafe")
                        .document(MapView.cafe_map.get(finalI).id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        cafeDB cafedata = document.toObject(cafeDB.class);
                                        MapView.cafe_map.get(finalI).avg_num_game = cafedata.getStarNumGame();
                                        MapView.cafe_map.get(finalI).avg_clean = cafedata.getStarClean();
                                        MapView.cafe_map.get(finalI).avg_service = cafedata.getStarService();
                                    }
                                }
                            }
                        });
                InfoWindowDialog info=InfoWindowDialog.getInfoWindowDialog(finalI,
                        cafe_map.get(finalI).avg_num_game, cafe_map.get(finalI).avg_clean, cafe_map.get(finalI).avg_service,
                        cafe_map.get(finalI).place_name, cafe_map.get(finalI).address_name, cafe_map.get(finalI).phone
                );
                //별점창 띄우기
                info.show(getFragmentManager(), InfoWindowDialog.TAG_EVENT_DIALOG);
                //별점 값 받아오기
                return true;
            });
        }
        /*SearchView*/
        //SearchView 객체
        SearchView searchView = (SearchView) viewGroup.findViewById(R.id.search_map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getActivity(), "search_map: "+query, Toast.LENGTH_LONG).show();
                search_query = query;

                //MapView의 인스턴스
                //MapView tf= new MapView();
                SearchCafe searchCafe = new SearchCafe();

                //Bundle- FragmentMap에서 MapView로 데이터 넘기기
                Bundle bundle = new Bundle(1);
               /* bundle.putParcelableArrayList("list", board_cafe_list);
                bundle.putDouble("latitude", Double.parseDouble(y));
                bundle.putDouble("longitude", Double.parseDouble(x));*/
                bundle.putString("search_query", search_query);
                searchCafe.setArguments(bundle);
                //MapView로 전환
                ((MainActivity) getActivity()).replaceSearch(searchCafe);

                //CameraUpdate cameraUpdate= CameraUpdate.scrollTo(new LatLng(cafe_map.get(cafe_map.size()-1).y, cafe_map.get(cafe_map.size()-1).x)).animate(CameraAnimation.Fly);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


    }


}