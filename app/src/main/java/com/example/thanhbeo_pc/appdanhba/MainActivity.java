package com.example.thanhbeo_pc.appdanhba;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.thanhbeo_pc.appdanhba.Adapter.PagerAdapter;
import com.example.thanhbeo_pc.appdanhba.Fragment.DanhbaFragment;
import com.example.thanhbeo_pc.appdanhba.Fragment.DienthoaiFragment;
import com.example.thanhbeo_pc.appdanhba.listener.FragmentListener;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private DienthoaiFragment dienthoaiFragment;
    private DanhbaFragment danhbaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(dienthoaiFragment = new DienthoaiFragment(), "Điện thoại");
        adapter.addFragment(danhbaFragment = new DanhbaFragment(), "Danh bạ");

        danhbaFragment.setListener(new FragmentListener() {
            @Override
            public void onFragmentAction(Bundle bundle) {
                if(dienthoaiFragment!=null) dienthoaiFragment.Loaddata();
            }
        });
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        initData();
    }
    private void initData() {
        db = openOrCreateDatabase("danhba.db", MODE_PRIVATE, null);
        String sqll = "CREATE TABLE IF NOT EXISTS danhba (id integer primary key autoincrement, ten text, sdt text, gt text)";
        String sql2 = "CREATE TABLE IF NOT EXISTS cuocgoiganday (id integer primary key autoincrement, ten text, sdt text, gt text,ngay text)";
        db.execSQL(sql2);
        db.execSQL(sqll);
    }

}