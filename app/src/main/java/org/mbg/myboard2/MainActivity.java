package org.mbg.myboard2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //**
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //**
    BottomNavigationView bottomNavigationView;
    FragmentHome fragmentHome;
    FragmentLike fragmentLike;
    FragmentMap fragmentMap;
    FragmentProfile fragmentProfile;
    FragmentSearch fragmentSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Button logoutButton=findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signOut();
            }});*/


         bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //프래그먼트 생성
        fragmentHome = new FragmentHome();
        fragmentLike = new FragmentLike();
        fragmentMap = new FragmentMap();
        fragmentProfile = new FragmentProfile();
        fragmentSearch = new FragmentSearch();

        //제일 처음 띄울 뷰
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,fragmentHome).commitAllowingStateLoss();

        //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.

                    case R.id.action_map: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout, fragmentMap).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.action_search: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,fragmentSearch).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.action_home: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout, fragmentHome).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.action_like: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout, fragmentLike).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.action_profile: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout, fragmentProfile).commitAllowingStateLoss();
                        return true;
                    }
                    default:
                        return false;
                }

            }
        });

    }


    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]

        //로그아웃 하면 로그인 화면으로 돌아감
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}