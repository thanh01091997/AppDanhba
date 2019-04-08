package com.example.thanhbeo_pc.appdanhba.Adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanhbeo_pc.appdanhba.Data.Cuocgoiganday;
import com.example.thanhbeo_pc.appdanhba.R;

import java.util.List;

public class CuocgoigandayAdapter extends RecyclerView.Adapter <CuocgoigandayAdapter.ViewHolder> {

    private Context context;
    private List<Cuocgoiganday> cuocgoigandayList;
    SQLiteDatabase db;

    public CuocgoigandayAdapter(Context context, List <Cuocgoiganday> cuocgoigandayList) {
        this.context = context;
        this.cuocgoigandayList = cuocgoigandayList;
        this.db = db;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_cuocgoiganday, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(CuocgoigandayAdapter.ViewHolder viewHolder, int pos) {

        Cuocgoiganday cuocgoiganday = cuocgoigandayList.get(pos);
        viewHolder.tvName.setText(cuocgoiganday.getTen());
        viewHolder.tvPhone.setText(cuocgoiganday.getSdt());
        viewHolder.tvNgay.setText(cuocgoiganday.getNgay());
        if (cuocgoiganday.getGt().equals("Nam")) {
            viewHolder.ivAvatar.setImageResource(R.drawable.ic_nam);
        } else {
            viewHolder.ivAvatar.setImageResource(R.drawable.ic_nu);
        }
    }

    @Override
    public int getItemCount() {
        return cuocgoigandayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone, tvNgay;
        ImageView ivAvatar;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNgay = itemView.findViewById(R.id.tvNgay);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.tuychon_call_mess);
                    dialog.show();

                    Button btngoi, btnnhantin, btnhuy;
                    btngoi = dialog.findViewById(R.id.btngoi);
                    btnnhantin = dialog.findViewById(R.id.btnnhantin);
                    btnhuy = dialog.findViewById(R.id.btnhuy);

                    btngoi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calldanhba();
                        }
                    });
                    btnnhantin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messdanhba();
                        }
                    });
                    btnhuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    xoacuocgoi();
                    return false;
                }
            });
        }
        private void xoacuocgoi() {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn muốn xóa người dùng?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            id = cuocgoigandayList.get(getPosition()).getId();
                            db = context.openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
                            String sql = "DELETE FROM cuocgoiganday WHERE id = " + id;
                            db.execSQL(sql);
                            Loaddata();
                        }
                    }).setNegativeButton("Canncel", null)
                    .show();
        }
        private void Loaddata() {

            cuocgoigandayList.clear();
            String sql = "SELECT * FROM cuocgoiganday";
            Cursor cursor = db.rawQuery(sql, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String ten = cursor.getString(1);
                String sdt = cursor.getString(2);
                String gt = cursor.getString(3);
                String ngay = cursor.getString(4);

                Cuocgoiganday d =new Cuocgoiganday();
                d.setId(id);
                d.setTen(ten);
                d.setSdt(sdt);
                d.setGt(gt);
                d.setNgay(ngay);
                cuocgoigandayList.add(d);
                cursor.moveToNext();
                notifyDataSetChanged();
            }
        }
        public void messdanhba() {

            Intent mess = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + cuocgoigandayList.get(getPosition()).getSdt()));
            context.startActivity(mess);

        }

        private void calldanhba() {

            Intent callIntent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cuocgoigandayList.get(getPosition()).getTen()));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(callIntent1);
        }

    }
}
