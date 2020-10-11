package org.mbg.myboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class gameAdapter extends RecyclerView.Adapter<gameAdapter.MyViewHolder> {

    //GenericTypeIndicator<ArrayList<GameData>> t = new GenericTypeIndicator<ArrayList<GameData>>() {};
    private ArrayList<GameData> mDataset;
    private Context context;
    private int imgSet;


    // Provide a suitable constructor (depends on the kind of dataset)
    // 3-1 생성자에서 데이터 리스트 객체를 전달받음. 전체 데이터임
    public gameAdapter(Context context,ArrayList<GameData> myDataset,int imgSet) {
        this.context=context;
        mDataset = myDataset;
        this.imgSet=imgSet;
    }

    // 3-0 아이템 뷰를 저장하는 뷰홀더 클래스. (각 하나)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewGname; //게임 이름
        public TextView textViewTag1;
        public TextView textViewTag2;
        public TextView textViewTag3;
        public TextView textViewGtext;

        //public Button likeButton;
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
            //likeButton=v.findViewById(R.id.imageButton);
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
        String text1 = (String) mDataset.get(position).getGnameKOR();//position 번호의 데이터(객체)의 게임 이름
        String text2 = (String) mDataset.get(position).getGenres();     //게임 장르
        String text3 = (String) mDataset.get(position).getGnumbystring();   //게임 인원
        String text4 = (String) mDataset.get(position).getGtimebystring();  //게임 시간
        String text5 = (String) mDataset.get(position).getSystem();     //시스템

        holder.textViewGname.setText(text1) ;
        holder.textViewTag1.setText(text2) ;
        holder.textViewTag2.setText(text3) ;
        holder.textViewTag3.setText(text4) ;
        holder.textViewGtext.setText(text5) ;
        holder.image.setBackgroundResource(this.imgSet);
        //holder.textView2.setText((String)mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    //전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
