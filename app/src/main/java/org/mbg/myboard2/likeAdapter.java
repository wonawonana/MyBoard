package org.mbg.myboard2;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class likeAdapter extends RecyclerView.Adapter<likeAdapter.MyViewHolder> {


    private ArrayList<LikeData> mDataset;
    private Context context;

    FirebaseFirestore db;
    String UserEmail;
    TextView gGenre;
    TextView gNum;
    TextView gTime;
    TextView gSys;

    ImageView iv;

    public likeAdapter(Context context,ArrayList<LikeData> myDataset) {
        this.context=context;
        mDataset = myDataset;
        db = FirebaseFirestore.getInstance();       //...?여기다 넣어도 되는건가?
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();
    }


    // 3-0 아이템 뷰를 저장하는 뷰홀더 클래스. (각 하나)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewGname; //게임 이름
        public TextView textViewMemo;   //게임 메모
        public Button saveButton;
        public Button deleteButton;
        public Button moreButton;


        public MyViewHolder(View v) {
            super(v);
            // 뷰 객체에 대한 참조. (hold strong reference)
            // 내 생각에 이거 각 아이템 뷰 내용을 참조하는듯
            textViewGname = v.findViewById(R.id.gameName);
            textViewMemo = v.findViewById(R.id.memoEditText);
            saveButton = v.findViewById(R.id.saveButton);
            deleteButton = v.findViewById(R.id.deleteButton);
            moreButton= v.findViewById(R.id.MoreButton);

        }
    }

    // Create new views (invoked by the layout manager)
    // 3-2 뷰홀더와 아이템뷰 xml 연결
    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    //뷰 홀더 생성
    @Override
    public likeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        //어떤 뷰? 아이템 뷰 네모네모(만들어 놓은거)
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_likelist, parent, false);

        //뷰 홀더 객체 생성 <- 아이템 뷰 하나 생성자로 전달
        //드디어 뷰 홀더에 뷰를 전달합니다.
        likeAdapter.MyViewHolder vh = new likeAdapter.MyViewHolder(v);
        return vh;  //하나의 아이템 뷰 참조하는 뷰 홀더 하나
    }


    @Override
    public void onBindViewHolder(final likeAdapter.MyViewHolder holder, int position){
        final String text1 = (String) mDataset.get(position).getGnameKOR();
        String text2 = (String) mDataset.get(position).getMemo();

        holder.textViewGname.setText(text1);
        holder.textViewMemo.setText(text2);

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.view_game_likelist);
                TextView gname =(TextView)dialog.findViewById(R.id.textView7);
                gname.setText(text1);

                gGenre =(TextView)dialog.findViewById(R.id.textView8);
                gNum =(TextView)dialog.findViewById(R.id.textView10);
                gTime =(TextView)dialog.findViewById(R.id.textView11);
                gSys=(TextView)dialog.findViewById(R.id.textView9);

                iv=(ImageView)dialog.findViewById(R.id.imageView2);

                DocumentReference docRef = db.collection("BoardGame").document(text1);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                gGenre.setText(document.get("genres").toString());
                                gNum.setText(document.get("gnumbystring").toString());
                                gTime.setText(document.get("gtimebystring").toString());
                                gSys.setText(document.get("system").toString());
                                Glide.with(context).load(document.get("imgUrl").toString()).override(150, 150).centerCrop().error(android.R.drawable.stat_notify_error)
                                        .placeholder(R.drawable.ic_launcher_background).into(iv);
                            } else {
                               // Log.d(TAG, "No such document");
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                memoUpdate(holder.textViewMemo.getText().toString(),holder.textViewGname.getText().toString());
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                memoDelete(holder.textViewGname.getText().toString());
            }
        });

    }

    public void memoUpdate(String memo,String gameName){
        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeGame");

        mPostReference
                .document(gameName)
                .update("memo", memo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(context, "memo가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                        Toast.makeText(context, "memo 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void memoDelete(String gameName){
        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeGame");
        mPostReference
                .document(gameName)
                .delete();
    }





        // Return the size of your dataset (invoked by the layout manager)
        //전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
