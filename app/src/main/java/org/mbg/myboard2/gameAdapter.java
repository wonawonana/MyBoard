package org.mbg.myboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class gameAdapter extends RecyclerView.Adapter<gameAdapter.MyViewHolder> {

    //GenericTypeIndicator<ArrayList<GameData>> t = new GenericTypeIndicator<ArrayList<GameData>>() {};
    private ArrayList<GameData> mDataset;
    private Context context;
    //private int imgSet;
    FirebaseFirestore db;
    String UserEmail;


    // Provide a suitable constructor (depends on the kind of dataset)
    // 3-1 생성자에서 데이터 리스트 객체를 전달받음. 전체 데이터임
    public gameAdapter(Context context,ArrayList<GameData> myDataset) {
        this.context=context;
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();       //...?여기다 넣어도 되는건가?
        //this.imgSet=imgSet;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();
    }

    // 3-0 아이템 뷰를 저장하는 뷰홀더 클래스. (각 하나)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewGname; //게임 이름
        public TextView textViewTag1;
        public TextView textViewTag2;
        public TextView textViewTag3;
        public TextView textViewGtext;

        public Button addListButton;
        public ImageView image;


        public MyViewHolder(View v) {
            super(v);
            // 뷰 객체에 대한 참조. (hold strong reference)
            // 내 생각에 이거 각 아이템 뷰 내용을 참조하는듯
            textViewGname = v.findViewById(R.id.textView7);
            textViewTag1 = v.findViewById(R.id.textView8);
            textViewTag2 = v.findViewById(R.id.textView10);
            textViewTag3 = v.findViewById(R.id.textView11);
            textViewGtext= v.findViewById(R.id.textView9);
            addListButton=(Button)v.findViewById(R.id.addListButton);
            image=v.findViewById(R.id.imageView2);
        }
    }

    // Create new views (invoked by the layout manager)
    // 3-2 뷰홀더와 아이템뷰 xml 연결
    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    //뷰 홀더 생성
    @Override
    public gameAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        //어떤 뷰? 아이템 뷰 네모네모(만들어 놓은거)
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_game, parent, false);

        //뷰 홀더 객체 생성 <- 아이템 뷰 하나 생성자로 전달
        //드디어 뷰 홀더에 뷰를 전달합니다.
        MyViewHolder vh = new MyViewHolder(v);
        return vh;  //하나의 아이템 뷰 참조하는 뷰 홀더 하나
    }


    // Replace the contents of a view (invoked by the layout manager)
    // 3-3 position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    //연결된 뷰와 뷰 홀더 (xml 과 java)에 데이터 전달
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // 각 위치에 문자열 세팅
        //뭐여 포지션이 뭐여
        final String text1 = (String) mDataset.get(position).getGnameKOR();//position 번호의 데이터(객체)의 게임 이름
        final String text2 = (String) mDataset.get(position).getGenres();     //게임 장르
        final String text3 = (String) mDataset.get(position).getGnumbystring();   //게임 인원
        final String text4 = (String) mDataset.get(position).getGtimebystring();  //게임 시간
        String text5 = (String) mDataset.get(position).getSystem();     //시스템


        final String textGenre=(String) mDataset.get(position).getGenre();
        //final List listTime=new ArrayList<Map>();
        final Map <String, Boolean>timeMap=new HashMap<String,Boolean>();
        timeMap.put("gtimeless30",mDataset.get(position).getGtimeless30());
        timeMap.put("gtime30_60",mDataset.get(position).getGTime30_60());
        timeMap.put("gtime60_90",mDataset.get(position).getGTime60_90());
        timeMap.put("gtime90_120",mDataset.get(position).getGTime90_120());
        timeMap.put("gtimemore120",mDataset.get(position).getGtimemore120());
               // listTime.add(timeMap);
        final ArrayList<Integer> gnum=(ArrayList)mDataset.get(position).getGnum();

        holder.textViewGname.setText(text1) ;
        holder.textViewTag1.setText(text2) ;
        holder.textViewTag2.setText(text3) ;
        holder.textViewTag3.setText(text4) ;
        holder.textViewGtext.setText(text5) ;
        Glide.with(context).load(mDataset.get(position).getImgUrl()).override(150, 150).centerCrop().error(android.R.drawable.stat_notify_error)
                .placeholder(R.drawable.ic_launcher_background).into(holder.image);
        //holder.textView2.setText((String)mDataset.get(position));
        //Glide.with(context).load(img1).override(200, 200).centerCrop().error(android.R.drawable.stat_notify_error)
        //                .placeholder(R.drawable.ic_launcher_background).into(holder.image);

        holder.addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미 add 했다? -> toast 이미 추가한 게임입니다.
                //else -> toast list 에 추가했습니다.
                HashMap<String,String> memo = new HashMap<String,String>(){{//초기값 지정
                    put("memo","");
                }};
                //각 유저마다 메모 추가
                CollectionReference mPostReference =
                        (CollectionReference) db.collection("member").document(UserEmail)
                                .collection("LikeGame");
                mPostReference
                        .document(text1)
                        .set(memo);

                //각 유저마다 태그 수 추가
                //1. 장르 (단일 장르로 우선 한정)
                TagGenrePlus(UserEmail,textGenre);
                //2. 시간
                TagTimePlus(timeMap);
                //3. 인원
                TagNumPlus(gnum);

                Toast.makeText(context, "like list 에 추가했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void TagGenrePlus(String UserEmail, String textGenre){
        CollectionReference mPostTagGenre=(CollectionReference)db.collection("member").document(UserEmail)
                .collection("LikeGenre");
        mPostTagGenre
                .document(textGenre)
                .update("num", FieldValue.increment(1)); //++1
    }

    void TagTimePlus(Map <String, Boolean>timeSet){
        //value - true 인 value 만 document 로 ++1
        CollectionReference mPostTagTime=(CollectionReference)db.collection("member").document(UserEmail)
                .collection("LikeTime");
        Iterator<String> keys = timeSet.keySet().iterator();
        while (keys.hasNext()){
            String key = keys.next();
            Boolean value= timeSet.get(key);
            if(value){  //true 일때만 ++1
                mPostTagTime
                        .document(key)
                        .update("num",FieldValue.increment(1));
            }
        }
    }

    void TagNumPlus(ArrayList<Integer> gnum){
        CollectionReference mPostTagNum=(CollectionReference)db.collection("member").document(UserEmail)
                .collection("LikeNum");
        Iterator<Integer> it = gnum.iterator();
        while(it.hasNext()) {
            String number = it.next().toString();
            mPostTagNum
                    .document(number)
                    .update("num",FieldValue.increment(1));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    //전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
