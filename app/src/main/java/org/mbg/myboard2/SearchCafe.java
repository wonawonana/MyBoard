package org.mbg.myboard2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SearchCafe extends FragmentMap {
    String query="query";
    String str_toast="toast";
    private Context sContext;
    //recycler
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //private ArrayList<cafe_searched> mDataset;

    @Override
    public void onAttach(Context context){
        //Fragment의 Context
        super.onAttach(context);
        sContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //viewGroup = (ViewGroup) inflater.inflate(R.layout.search_cafe,container,false);
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_cafe_recyclerview,container,false);
        /*검색_api*/
        query=getArguments().getString("search_query","");
        try {
            searchBoardCafeData(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*recyclerview*/
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.search_cafe_recyclerview);
        layoutManager = new LinearLayoutManager(getActivity()); //원래 인자 this 임
        recyclerView.setLayoutManager(layoutManager);
        //mDataset=new ArrayList<cafe_searched>();
        //mAdapter = new gameAdapter(getActivity(),mDataset);
        //mAdapter= new search_cafe_Adapter(getActivity(), mDataset);
        //recyclerView.setAdapter(mAdapter);









        return viewGroup;
    }



    public void searchBoardCafeData(String query) throws UnsupportedEncodingException {
        class searchBoardCafe extends AsyncTask<Void, Void, Void>  {

            String query_API=URLEncoder.encode(query, "UTF-8");
            searchBoardCafe() throws UnsupportedEncodingException {
            }
            //url, key
            String str_url="https://dapi.kakao.com/v2/local/search/keyword.json?" +
                    "page=1&size=15&sort=accuracy" +
                    "&query=" +
                    query_API+
                    //"%EC%98%A4%EB%A0%8C%EC%A7%80%EB%B3%B4%EB%93%9C%EC%B9%B4%ED%8E%98" +
                    "&category_group_code=CE7";
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
                    str_toast=temp;
                    //json
                    JSONObject json=new JSONObject();
                    json= new JSONObject(temp);
                    JSONArray doc_Array=new JSONArray();
                    doc_Array=json.getJSONArray("documents");
                    JSONObject cafe_info=new JSONObject();
                    for(int i=0;i<doc_Array.length();i++){
                        cafe_info=doc_Array.getJSONObject(0);
                        MapView.cafe_map.add(new BoardCafe(
                                cafe_info.getString("id"),
                                cafe_info.getString("address_name"),
                                cafe_info.getString("phone"),
                                cafe_info.getString("place_name"),
                                cafe_info.getString("place_url"),
                                cafe_info.getString("road_address_name"),
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
                /*
                super.onPostExecute(result);
                TextView txtPlaceName_search = (TextView) viewGroup.findViewById(R.id.txtTitle_search);
                TextView txtAddr_search = (TextView) viewGroup.findViewById(R.id.txtAddress_search);
                TextView txtTel_search = (TextView) viewGroup.findViewById(R.id.txtTel_search);
                TextView txtLink_search = (TextView) viewGroup.findViewById(R.id.txtLink_search);
                //db
                //db = FirebaseFirestore.getInstance();

                //주소, 번호, 링크
                if(MapView.cafe_map.get(MapView.cafe_map.size()-1).place_name.length()!=0){
                    txtPlaceName_search.setText(""+MapView.cafe_map.get(MapView.cafe_map.size()-1).place_name+"");
                }
                if(MapView.cafe_map.get(MapView.cafe_map.size()-1).address_name.length()!=0) {
                    txtAddr_search.setText(" "+MapView.cafe_map.get(MapView.cafe_map.size()-1).address_name);
                }
                if(MapView.cafe_map.get(MapView.cafe_map.size()-1).phone.length()!=0) {
                    txtTel_search.setText(" "+MapView.cafe_map.get(MapView.cafe_map.size()-1).phone);
                }

                if(MapView.cafe_map.get(MapView.cafe_map.size()-1).place_url.length()!=0) {
                    txtLink_search.setText(" "+MapView.cafe_map.get(MapView.cafe_map.size()-1).place_url);
                }

                //MapView의 인스턴스
                //MapView tf= new MapView();
                //Bundle- FragmentMap에서 MapView로 데이터 넘기기
                /*Bundle bundle=new Bundle(3);
                bundle.putParcelableArrayList("list", board_cafe_list);
                bundle.putDouble("latitude", Double.parseDouble(y));
                bundle.putDouble("longitude", Double.parseDouble(x));
                tf.setArguments(bundle);*/
                //MapView로 전환
                //((MainActivity)getActivity()).replaceFragment(tf);

            }


        }
        new searchBoardCafe().execute();
    }
}