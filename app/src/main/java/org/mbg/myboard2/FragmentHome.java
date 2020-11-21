package org.mbg.myboard2;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FragmentHome extends Fragment {
    ViewGroup viewGroup;

    //**
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //**

    private ImageView TodayGameImg;

    private ImageButton[] imgGButton;
    private TextView[] gTextName;
    private String[] gameNameM;
    private String UserEmail;
    private String todayGameName;

    private ArrayList<GameData> gameDataList;
    private GameData todayGameData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home,container,false);
        db = FirebaseFirestore.getInstance();
        TodayGameImg=viewGroup.findViewById(R.id.imageView3);
        gameDataList=new ArrayList<GameData>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserEmail=user.getEmail();

        todayGameName="모두의 마블 베이직";

        imgGButton=new ImageButton[5];
        imgGButton[0]=viewGroup.findViewById(R.id.GameButton1);
        imgGButton[1]=viewGroup.findViewById(R.id.GameButton2);
        imgGButton[2]=viewGroup.findViewById(R.id.GameButton3);
        imgGButton[3]=viewGroup.findViewById(R.id.GameButton4);
        imgGButton[4]=viewGroup.findViewById(R.id.GameButton5);
        gTextName=new TextView[5];
        gTextName[0]=viewGroup.findViewById(R.id.GameName1);
        gTextName[1]=viewGroup.findViewById(R.id.GameName2);
        gTextName[2]=viewGroup.findViewById(R.id.GameName3);
        gTextName[3]=viewGroup.findViewById(R.id.GameName4);
        gTextName[4]=viewGroup.findViewById(R.id.GameName5);

        ConstraintLayout layout1=viewGroup.findViewById(R.id.infoLayout);
        layout1.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               clickTodayGame();
           }
       });

        ConstraintLayout layout2=viewGroup.findViewById(R.id.likeLayout);
        layout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickTodayGameLike();
            }
        });

        gameNameM=new String[5];
        //1. 오늘의 게임
        showTodayGame();
        //2. top 5 게임
        showTopGame();
        clickTop5Game();

        //3. 인기 보드게임 카페

        //4. 최근 업데이트 된 보드게임 카페?




        return viewGroup;
    }

    void showTodayGame(){

        db.collection("BoardGame").document(todayGameName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        todayGameData=(document.toObject(GameData.class));
                        String imgUrl=document.getString("imgUrl");
                        Glide.with(getActivity()).load(imgUrl).override(200, 200).centerCrop().error(android.R.drawable.stat_notify_error)
                                .placeholder(R.drawable.ic_launcher_background).into(TodayGameImg);

                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void showTopGame(){
        CollectionReference BoardRef = db.collection("BoardGame");
        BoardRef.orderBy("likeNum", Query.Direction.DESCENDING).limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            gameDataList.clear();
                           int num=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                GameData gameData=(document.toObject(GameData.class));
                                gameDataList.add(gameData);
                                gTextName[num].setText(gameData.getGnameKOR());
                                Glide.with(getActivity()).load(gameData.getImgUrl()).override(200, 200).centerCrop().error(android.R.drawable.stat_notify_error)
                                        .placeholder(R.drawable.ic_launcher_background).into(imgGButton[num++]);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


       // Glide.with(getActivity()).load(gameDataTop.).override(200, 200).centerCrop().error(android.R.drawable.stat_notify_error)
              //  .placeholder(R.drawable.ic_launcher_background).into(TodayGameImg);
        /*
        int num=0;
        for(GameData i : gameDataList){
            Glide.with(getActivity()).load(i.getImgUrl()).override(100, 100).centerCrop().error(android.R.drawable.stat_notify_error)
              .placeholder(R.drawable.ic_launcher_background).into(imgGButton[num]);
            gTextName[num++].setText(i.getGnameKOR());
            Toast.makeText(getContext(),""+num,Toast.LENGTH_LONG).show();
        }*/

        /*
        for(int i = 0 ; i < 5 ; i++)
        {

            /*
            Glide.with(getActivity()).load(gameDataList.get(i).getImgUrl()).override(100, 100).centerCrop().error(android.R.drawable.stat_notify_error)
                    .placeholder(R.drawable.ic_launcher_background).into(imgGButton[i]);
            gTextName[i].setText(gameDataList.get(i).getGnameKOR());

            // 클릭 리스너 등록
            imgGButton[i].setOnClickListener((View.OnClickListener) this);

        }**/
        /*
        Glide.with(getActivity()).load(gameDataList.get(0).getImgUrl()).override(100, 100).centerCrop().error(android.R.drawable.stat_notify_error)
                .placeholder(R.drawable.ic_launcher_background).into(imgGButton[0]);*/
        /*
        Iterator<GameData> it = gameDataList.iterator();

        int i=0;
        while (it.hasNext()){
            gTextName[i++].setText(it.next().getGnameKOR());
            Toast.makeText(getContext(),it.next().getGnameKOR(),Toast.LENGTH_LONG).show();
        }*/


    }

    void clickTop5Game() {

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            imgGButton[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.view_home_gameinfo);
                    TextView gname = (TextView) dialog.findViewById(R.id.textView7);
                    gname.setText(gameDataList.get(finalI).getGnameKOR());

                    TextView gGenre = (TextView) dialog.findViewById(R.id.textView8);
                    gGenre.setText(gameDataList.get(finalI).getGenres());
                    TextView gNum = (TextView) dialog.findViewById(R.id.textView10);
                    gNum.setText(gameDataList.get(finalI).getGnumbystring());
                    TextView gTime = (TextView) dialog.findViewById(R.id.textView11);
                    gTime.setText(gameDataList.get(finalI).getGtimebystring());
                    TextView gSys = (TextView) dialog.findViewById(R.id.textView9);
                    gSys.setText(gameDataList.get(finalI).getSystem());
                    ImageButton gYou = (ImageButton) dialog.findViewById(R.id.youtubeButton);
                    TextView YoutubeText = (TextView) dialog.findViewById(R.id.textView15);
                    String youtubeUrllink = gameDataList.get(finalI).getYoutubeUrl();
                    ImageView iv = (ImageView) dialog.findViewById(R.id.imageView2);
                    Glide.with(getContext()).load(gameDataList.get(finalI).getImgUrl()).override(150, 150).centerCrop().error(android.R.drawable.stat_notify_error)
                            .placeholder(R.drawable.ic_launcher_background).into(iv);
                    String webUrllink=gameDataList.get(finalI).getGameUrl();
                    ImageButton weblink=(ImageButton)dialog.findViewById(R.id.imageButton2);
                    weblink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(webUrllink));
                            v.getContext().startActivity(intent);
                        }
                    });
                    if (youtubeUrllink != "") {
                        YoutubeText.setVisibility(View.VISIBLE);
                        gYou.setVisibility(View.VISIBLE);
                        gYou.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //2. 존재하므로 클릭시 url 연결
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(youtubeUrllink));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);

                            }
                        });
                    } else {
                        YoutubeText.setVisibility(View.GONE);
                        gYou.setVisibility(View.GONE);
                        gYou.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }
                    Button addListButton=(Button)dialog.findViewById(R.id.addListButton);
                    addListButton.setOnClickListener(new View.OnClickListener() {
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
                                    .document(gameDataList.get(finalI).getGnameKOR())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Toast.makeText(getContext(), "이미 추가된 게임입니다.", Toast.LENGTH_SHORT).show();
                                                    //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                } else {
                                                    //보드게임 likeNum ++1
                                                    LikeNumPlus(gameDataList.get(finalI).getGnameKOR());
                                                    //1. 메모 추가
                                                    mPostReference.document(gameDataList.get(finalI).getGnameKOR()).set(memo);
                                                    Toast.makeText(getContext(), "like list 에 추가했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                //Log.d(TAG, "get failed with ", task.getException());

                                            }
                                        }
                                    });

                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    void clickTodayGame(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.view_game_likelist);
        TextView gname = (TextView) dialog.findViewById(R.id.textView7);
        gname.setText(todayGameData.getGnameKOR());

        TextView gGenre = (TextView) dialog.findViewById(R.id.textView8);
        gGenre.setText(todayGameData.getGenres());
        TextView gNum = (TextView) dialog.findViewById(R.id.textView10);
        gNum.setText(todayGameData.getGnumbystring());
        TextView gTime = (TextView) dialog.findViewById(R.id.textView11);
        gTime.setText(todayGameData.getGtimebystring());
        TextView gSys = (TextView) dialog.findViewById(R.id.textView9);
        gSys.setText(todayGameData.getSystem());
        ImageButton gYou = (ImageButton) dialog.findViewById(R.id.youtubeButton);
        TextView YoutubeText = (TextView) dialog.findViewById(R.id.textView15);
        String youtubeUrllink = todayGameData.getYoutubeUrl();
        ImageView iv = (ImageView) dialog.findViewById(R.id.imageView2);
        Glide.with(getContext()).load(todayGameData.getImgUrl()).override(150, 150).centerCrop().error(android.R.drawable.stat_notify_error)
                .placeholder(R.drawable.ic_launcher_background).into(iv);
        String webUrllink=todayGameData.getGameUrl();
        ImageButton weblink=(ImageButton)dialog.findViewById(R.id.imageButton2);
        weblink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(webUrllink));
                v.getContext().startActivity(intent);
            }
        });
        if (youtubeUrllink != "") {
            YoutubeText.setVisibility(View.VISIBLE);
            gYou.setVisibility(View.VISIBLE);
            gYou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //2. 존재하므로 클릭시 url 연결
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(youtubeUrllink));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);

                }
            });
        } else {
            YoutubeText.setVisibility(View.GONE);
            gYou.setVisibility(View.GONE);
            gYou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        dialog.show();
    }

    void clickTodayGameLike(){
        HashMap<String,String> memo = new HashMap<String,String>(){{//초기값 지정
            put("memo","");
        }};
        //각 유저마다 메모 추가
        CollectionReference mPostReference =
                (CollectionReference) db.collection("member").document(UserEmail)
                        .collection("LikeGame");
                /*
                mPostReference
                        .document(text1)
                        .set(memo);*/
        mPostReference
                .document(todayGameName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(getContext(), "이미 추가된 게임입니다.", Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                //내 컬랙션에 추가하지 않은 게임일때
                                //보드게임 likeNum ++1
                                LikeNumPlus(todayGameName);
                                //1. 메모 추가
                                mPostReference.document(todayGameName).set(memo);
                                Toast.makeText(getContext(), "like list 에 추가했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());

                        }
                    }
                });
    }

    void LikeNumPlus(String gameName){
        db.collection("BoardGame").document(gameName).update("likeNum", FieldValue.increment(1));
    }


}
