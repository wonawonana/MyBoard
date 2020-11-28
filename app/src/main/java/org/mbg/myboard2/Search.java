package org.mbg.myboard2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
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

public class Search {
    String mQuery;
    Context mContext;
    FragmentManager mFragmentManager;
    ArrayList<BoardCafe> search_result;
    static SearchDialog searchDialog;


    public Search(String query, Context context, FragmentManager fragmentManager){
        mQuery=query;
        mContext=context;
        mFragmentManager=fragmentManager;
        search_result= new ArrayList<BoardCafe>();
    }



    public void searchBoardCafeData() throws UnsupportedEncodingException {
        class searchBoardCafe extends AsyncTask<Void, Void, Void>  {
            String query_API=URLEncoder.encode(mQuery, "UTF-8");
            searchBoardCafe() throws UnsupportedEncodingException {
            }
            //url, key
            String str_url="https://dapi.kakao.com/v2/local/search/keyword.json?" +
                    "page=1&size=15&" +
                    "sort=accuracy" +
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
                    //json
                    JSONObject json=new JSONObject();
                    json= new JSONObject(temp);
                    JSONArray doc_Array=new JSONArray();
                    doc_Array=json.getJSONArray("documents");
                    JSONObject cafe_info=new JSONObject();
                    for(int i=0;i<doc_Array.length();i++){
                        cafe_info=doc_Array.getJSONObject(i);
                        search_result.add(new BoardCafe(
                                cafe_info.getString("id"),
                                cafe_info.getString("address_name"),
                                cafe_info.getString("phone"),
                                cafe_info.getString("place_name"),
                                cafe_info.getDouble("x"),
                                cafe_info.getDouble("y"))
                        );
                    }

                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }


                return null;
            }


            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                /*SearchDialog searchDialog= new SearchDialog(
                        search_result.get(0).place_name, search_result.get(0).address_name,search_result.get(0).y, search_result.get(0).x);
               */
                searchDialog= new SearchDialog(search_result);
                //FragmentManager fragmentManager= mContext.getFrag
                searchDialog.show(mFragmentManager, SearchDialog.TAG_EVENT_DIALOG);


            }


        }
        new searchBoardCafe().execute();
    }
}