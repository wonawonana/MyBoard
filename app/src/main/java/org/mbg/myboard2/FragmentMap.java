package org.mbg.myboard2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class FragmentMap extends Fragment {
    ViewGroup viewGroup;
    //Fragment의 Context
    private Context mContext;
    //위치
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    private GpsTracker gpsTracker;
    double latitude=-1;
    double longitude=-1;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    //보드카페리스트
    ArrayList<BoardCafe> board_cafe_list=new ArrayList<BoardCafe>();


    @Override
    public void onAttach(Context context){
        //Fragment의 Context
        super.onAttach(context);
        mContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //권한 확인
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        //GpsTracker: 현재 위치의 위도 경도 얻기
        gpsTracker = new GpsTracker(mContext);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        if(latitude >0 && longitude >0){
            viewGroup = (ViewGroup) inflater.inflate(R.layout.fragmentmap,container,false);
            //보드카페데이터 받아오기
            getBoardCafeData(Double.toString(latitude),Double.toString(longitude),getActivity());

        }
        //install
        else{
            viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map_install,container,false);
            TextView user_location= (TextView)viewGroup.findViewById(R.id.user_location);
            Button location_install= (Button) viewGroup.findViewById(R.id.location_install);
            Button move_to_map=(Button)viewGroup.findViewById(R.id.move_to_map);
            //권한 확인
            if (!checkLocationServicesStatus()) {
                showDialogForLocationServiceSetting();
            }else {
                checkRunTimePermission();
            }

            //위치확인버튼
            location_install.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //GpsTracker: 현재 위치의 위도 경도 얻기
                    gpsTracker = new GpsTracker(mContext);
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                    user_location.setText("현재 위치: "+ getCurrentAddress(latitude,longitude));
                    move_to_map.setVisibility(View.VISIBLE);
                }
            });
            //지도이동버튼
            move_to_map.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //보드카페데이터 받아오기
                    getBoardCafeData(Double.toString(latitude),Double.toString(longitude),getActivity());
                }
            });

        }
        return viewGroup;
    }

    /*Kakao REST API로 카페 데이터 받아오기*/
    public void getBoardCafeData(String y, String x, Context mContext) {
        class getBoardCafe extends AsyncTask<Void, Void, Void> {
            //url, key
            String str_url="https://dapi.kakao.com/v2/local/search/keyword.json?" +
                    "page=1" +
                    "&size=15&sort=distance&query=%EB%B3%B4%EB%93%9C&category_group_code=CE7" +
                    "&y="+y + //latitude, 3@.
                    "&x="+x; //longitude, 12@.
            String API_key="KakaoAK 57903c4ee3dcf9a9cf4f1bf8bd6a4073";


            @Override
            protected Void doInBackground(Void... params) {
                //inputstream
                InputStream inputStream= null;

                try{
                    //url
                    URL url = new URL(str_url);
                    //con
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "KakaoAK " + "57903c4ee3dcf9a9cf4f1bf8bd6a4073");
                    conn.setRequestProperty("Consent-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    int response = conn.getResponseCode();
                    //inputstream
                    inputStream = conn.getInputStream();
                    InputStreamReader isr=new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader read= new BufferedReader(isr);
                    String line= read.readLine();
                    String temp="";
                    while(line!=null){
                        temp+=line;
                        line=read.readLine();
                    }
                    //json
                    JSONObject json=new JSONObject();
                    json= new JSONObject(temp);
                    JSONArray doc_Array=new JSONArray();
                    doc_Array=json.getJSONArray("documents");
                    JSONObject cafe_info=new JSONObject();
                    for(int i=0;i<doc_Array.length();i++){
                        cafe_info=doc_Array.getJSONObject(i);
                        board_cafe_list.add(new BoardCafe(
                                cafe_info.getString("id"),
                                cafe_info.getString("address_name"),
                                cafe_info.getString("phone"),
                                cafe_info.getString("place_name"),
                                cafe_info.getDouble("x"),
                                cafe_info.getDouble("y")
                        ));
                    }

                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //MapView의 인스턴스
                MapView mapView= new MapView();
                //Bundle- FragmentMap에서 MapView로 데이터 넘기기
                Bundle bundle=new Bundle(3);
                bundle.putParcelableArrayList("list", board_cafe_list);
                bundle.putDouble("latitude", Double.parseDouble(y));
                bundle.putDouble("longitude", Double.parseDouble(x));
                mapView.setArguments(bundle);

                //MapView로 전환
                ((MainActivity)mContext).replaceFragmentMapToMapView(mapView);

            }
        }
        new getBoardCafe().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                //오류날 경우 확인하기 this-> getActivity
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(mContext, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    //finish();


                }else {

                    Toast.makeText(mContext, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            //오류날 경우 확인하기 this-> getActivity
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(mContext, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(mContext , Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(mContext, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(mContext, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(mContext, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    //확인하기
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }




}