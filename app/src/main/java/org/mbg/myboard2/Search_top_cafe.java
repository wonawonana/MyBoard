package org.mbg.myboard2;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

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

public class Search_top_cafe {
    cafeDB mCafe;
    String mQuery;
    String mAddress_name;
    String mPhone;
    Context mContext;
    private FragmentManager mFragmentManager;
    ArrayList<BoardCafe> search_result;
    static SearchDialog searchDialog;
    BoardCafe cafe_result=null;


    public Search_top_cafe(cafeDB cafe, FragmentManager fragmentManager)
    //Context context, FragmentManager fragmentManager)
    {
        mCafe=cafe;
        mFragmentManager=fragmentManager;
        mQuery=mCafe.getCafeName();
        //mQuery="";
       /* if(!mQuery.contains("보드")){
            mQuery= mQuery + " 보드";
        }*/
        //mContext=context;

        mAddress_name=mCafe.getAddress_name();
        mPhone=mCafe.getPhone();
        search_result= new ArrayList<BoardCafe>();
    }



    public BoardCafe searchBoardCafeData() throws UnsupportedEncodingException {

        class searchBoardCafe extends AsyncTask<Void, Void, Void>  {
            //검색어에 '보드' 추가하기


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
                boolean SEARCH=false;
                for(int i=0;i<search_result.size();i++){
                    if(mQuery.equals(search_result.get(i).place_name)){
                        if(mAddress_name.equals(search_result.get(i).address_name)
                                && mPhone.equals(search_result.get(i).phone)){
                            InfoWindowDialog_home infoWindowDialog_home= new InfoWindowDialog_home(search_result.get(i));
                            infoWindowDialog_home.show(mFragmentManager,InfoWindowDialog.TAG_EVENT_DIALOG);
                            SEARCH=true;
                            break;
                        }
                    }
                }
                if(SEARCH==false){
                    InfoWindowDialog_home_false infoWindowDialog_home_false= new InfoWindowDialog_home_false(mCafe);
                    infoWindowDialog_home_false.show(mFragmentManager, InfoWindowDialog.TAG_EVENT_DIALOG);
                }else{

                }
                //Toast.makeText(mContext, mQuery, Toast.LENGTH_SHORT).show();
                //searchDialog= new SearchDialog(search_result, mFragmentManager);
                //FragmentManager fragmentManager= mContext.getFrag
                //searchDialog.show(mFragmentManager, SearchDialog.TAG_EVENT_DIALOG);


            }


        }
        new searchBoardCafe().execute();

        return cafe_result;
    }
}