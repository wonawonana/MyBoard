package org.mbg.myboard2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

import static android.content.Intent.getIntent;

public class MapView extends Fragment implements OnMapReadyCallback {

    ViewGroup viewGroup;
    //위치
    FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    //FragmentMap에서 전달받은 데이터- 카페, 위도&경도
    ArrayList<BoardCafe> cafe_map=new ArrayList<BoardCafe>();
    double latitude, longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_map,container,false);
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
        //사용자위치
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        CameraUpdate cameraUpdate= CameraUpdate.scrollTo(new LatLng(latitude,longitude)).animate(CameraAnimation.Fly);


        /*cafe_map -> markers*/
        ArrayList<Marker> markers=new ArrayList<Marker>();
        for(int m=0;m<cafe_map.size();m++) {
            //마커 생성
            Marker marker= new Marker();
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

        InfoWindow infoWindow= new InfoWindow();



    }
}