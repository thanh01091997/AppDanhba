package com.example.thanhbeo_pc.appdanhba.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thanhbeo_pc.appdanhba.Adapter.CuocgoigandayAdapter;
import com.example.thanhbeo_pc.appdanhba.Data.Cuocgoiganday;
import com.example.thanhbeo_pc.appdanhba.R;

import java.util.ArrayList;
import java.util.List;

public class DienthoaiFragment extends Fragment {

    int idButtons[] = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
            R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnStar,
            R.id.btnTag
    };
    private TextView tvPhone;
    private SQLiteDatabase db;
    private List <Cuocgoiganday> cuocgoigandayList = new ArrayList <>();
    private CuocgoigandayAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dienthoai_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvCuocgoiganday = view.findViewById(R.id.rvcuocgoiganday);
        rvCuocgoiganday.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new CuocgoigandayAdapter(getActivity(), cuocgoigandayList);
        rvCuocgoiganday.setAdapter(adapter);

        Loaddata();


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        tvPhone = view.findViewById(R.id.tvPhone);
        for (int i = 0; i < idButtons.length; i++) {
            view.findViewById(idButtons[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button btn = (Button) v;
                    tvPhone.append(btn.getText().toString());
                }
            });
        }

        view.findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvPhone.getText().toString();
                if (text.length() > 0) {
                    text = text.substring(0, text.length() - 1);
                    tvPhone.setText(text);
                }
            }
        });

        view.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                startActivity(callIntent);
            }
        });
    }

    public void Loaddata() {
        cuocgoigandayList.clear();
        db = getActivity().openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
        cuocgoigandayList.clear();
        String sql = "SELECT * FROM cuocgoiganday order by id desc";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String sdt = cursor.getString(2);
            String gt = cursor.getString(3);
            String ngay = cursor.getString(4);

            Cuocgoiganday d = new Cuocgoiganday();
            d.setId(id);
            d.setTen(ten);
            d.setSdt(sdt);
            d.setGt(gt);
            d.setNgay(ngay);
            cuocgoigandayList.add(d);
            cursor.moveToNext();
        }
        adapter.notifyDataSetChanged();
    }
}
