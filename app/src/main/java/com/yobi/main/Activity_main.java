package com.yobi.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yobi.R;
import com.yobi.community.Fragment_community;
import com.yobi.my.Fragment_my;
import com.yobi.recipe.Fragment_recipe;

public class Activity_main extends AppCompatActivity {

    // 컴포넌트
    BottomNavigationView bottomNavigationView;
    Fragment_main fragment_main;
    Fragment_recipe fragment_recipe;
    Fragment_community fragment_community;
    Fragment_my fragment_my;

    // 데이터 or Manager
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 초기화
        bottomNavigationView = findViewById(R.id.bottomNavView_main);
        fragment_main = new Fragment_main();
        fragment_recipe = new Fragment_recipe();
        fragment_community = new Fragment_community();
        fragment_my = new Fragment_my();

        // 이벤트 리스너
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.fragment_home)
                    setFrag(0);
                if (menuItem.getItemId() == R.id.fragment_recipe)
                    setFrag(1);
                if (menuItem.getItemId() == R.id.fragment_community)
                    setFrag(2);
                if (menuItem.getItemId() == R.id.fragment_my)
                    setFrag(3);

                return true;
            }
        });


        setFrag(0); // 메인 화면으로 설정
    }
    private void setFrag(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_main).commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_recipe).commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_community).commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_my).commit();
                break;
        }
    }
}