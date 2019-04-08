package com.example.thanhbeo_pc.appdanhba.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.thanhbeo_pc.appdanhba.Adapter.DanhbaAdapter;
import com.example.thanhbeo_pc.appdanhba.Data.Danhba;
import com.example.thanhbeo_pc.appdanhba.R;
import com.example.thanhbeo_pc.appdanhba.listener.FragmentListener;
import com.example.thanhbeo_pc.appdanhba.listener.RVClickItemListener;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DanhbaFragment extends Fragment {

    private SQLiteDatabase db;
    private List <Danhba> danhbaList = new ArrayList <>();
    private DanhbaAdapter adapter;
    EditText edttimkiem;

    ImageView themnguoidung;

    private FragmentListener listener;

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.danhba_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themnguoidung = view.findViewById(R.id.themnguoidung);

        db = getActivity().openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
        Loaddata();

        RecyclerView rvDanhba = view.findViewById(R.id.rvDanhba);
        rvDanhba.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new DanhbaAdapter(getActivity(), danhbaList);
        adapter.setListener(new RVClickItemListener() {
            @Override
            public void onItemClick(int pos, String action) {
                if (action.equals("call")) {
                    reloadHistory();
                }
            }
        });
        rvDanhba.setAdapter(adapter);

        themnguoidung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }
        });

        edttimkiem = view.findViewById(R.id.edttimkiem);
        edttimkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void reloadHistory() {
        if(listener!=null) listener.onFragmentAction(null);
    }

    private void filter(String text) {

        ArrayList <Danhba> filterlist = new ArrayList <>();
        for (Danhba item : danhbaList) {

            if (item.getTen().toLowerCase().contains(text.toLowerCase())) {
                filterlist.add(item);
            }

        }
        adapter.filterlist(filterlist);
        adapter.notifyDataSetChanged();
    }

    private void showDialogAdd() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_add);

        final EditText edten, edso;
        Button btnthem, btnhuy;
        final RadioButton rdnam, rdnu;

        edten = dialog.findViewById(R.id.edtendialog);
        edso = dialog.findViewById(R.id.edsodialog);
        btnhuy = dialog.findViewById(R.id.btnhuydialog);
        btnthem = dialog.findViewById(R.id.btnadđialog);
        rdnam = dialog.findViewById(R.id.rdnam);
        rdnu = dialog.findViewById(R.id.rdnu);

        btnhuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gt;
                String ten = edten.getText().toString().trim();
                String so = edso.getText().toString().trim();

                if (rdnam.isChecked()) {
                    gt = rdnam.getText().toString().trim();
                } else {
                    gt = rdnu.getText().toString().trim();
                }
                themDanhBa(ten, so, gt);
                Loaddata();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void themDanhBa(String ten, String so, String gt) {

        if (ten.isEmpty() || so.isEmpty()) {
            Toast.makeText(getActivity(), "Nhập đầy đủ thông tin!!", Toast.LENGTH_SHORT).show();
        } else {
            db = getActivity().openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);

            String sql = "INSERT INTO danhba (ten, sdt, gt) VALUES ('" + ten + "','" + so + "','" + gt + "')";
            db.execSQL(sql);
            Toast.makeText(getActivity(), "Thêm thành công!!", Toast.LENGTH_SHORT).show();

        }
    }

    public void Loaddata() {
        danhbaList.clear();
        String sql = "SELECT * FROM danhba order by id desc";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String sdt = cursor.getString(2);
            String gt = cursor.getString(3);

            Danhba d = new Danhba();
            d.setId(id);
            d.setTen(ten);
            d.setSdt(sdt);
            d.setGt(gt);
            danhbaList.add(d);
            cursor.moveToNext();
        }
    }

}


