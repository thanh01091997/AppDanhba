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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thanhbeo_pc.appdanhba.Data.Danhba;
import com.example.thanhbeo_pc.appdanhba.R;
import com.example.thanhbeo_pc.appdanhba.listener.RVClickItemListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DanhbaAdapter extends RecyclerView.Adapter <DanhbaAdapter.ViewHolder> {
    private Context context;
    private List <Danhba> danhbaList;
    SQLiteDatabase db;

    private RVClickItemListener listener;

    public void setListener(RVClickItemListener listener) {
        this.listener = listener;
    }

    public DanhbaAdapter(Context context, List <Danhba> danhbaList) {
        this.context = context;
        this.danhbaList = danhbaList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeview) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_danhba, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {

        Danhba danhba = danhbaList.get(pos);
        viewHolder.tvName.setText(danhba.getTen());
        viewHolder.tvPhone.setText(danhba.getSdt());
        if (danhba.getGt().equals("Nam")) {
            viewHolder.ivAvatar.setImageResource(R.drawable.ic_nam);
        } else {
            viewHolder.ivAvatar.setImageResource(R.drawable.ic_nu);
        }

    }


    public int getItemCount() {
        return danhbaList.size();
    }

    public void filterlist(ArrayList <Danhba> filteredList) {
        danhbaList = filteredList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        ImageView ivAvatar, imgcall, imgmess;
        EditText edttimkiem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            imgcall = itemView.findViewById(R.id.imgcall);
            imgmess = itemView.findViewById(R.id.imgmess);

            edttimkiem = itemView.findViewById(R.id.edttimkiem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hienThiSua();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    xoadanhba();
                    return false;
                }

            });
            imgcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calldanhba();
                    String gt = danhbaList.get(getPosition()).getGt();
                    String ten = danhbaList.get(getPosition()).getTen();
                    String so = danhbaList.get(getPosition()).getSdt();
                    Calendar cal = Calendar.getInstance();
                    String ngay = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/"+ cal.get(Calendar.YEAR) + "   " + cal.get(Calendar.HOUR_OF_DAY) +":"+ cal.get(Calendar.MINUTE);
                    db = context.openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
                    String sql1 = "INSERT INTO cuocgoiganday (ten, sdt, gt, ngay) VALUES ('" + ten + "','" + so + "','" + gt + "','" + ngay + "')";
                    db.execSQL(sql1);

                    if(listener!=null) listener.onItemClick(getAdapterPosition(), "call");
                }
            });

            imgmess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messdanhba();

                }
            });

        }


        public void Loaddata() {

            danhbaList.clear();
            String sql = "SELECT * FROM danhba";
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
                notifyDataSetChanged();
            }

        }

        public void messdanhba() {

            Intent mess = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + danhbaList.get(getPosition()).getSdt()));
            context.startActivity(mess);

        }

        private void calldanhba() {
            Intent callIntent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + danhbaList.get(getPosition()).getTen()));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(callIntent1);
        }

        private void xoadanhba() {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn muốn xóa người dùng?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            id = danhbaList.get(getPosition()).getId();
                            db = context.openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
                            String sql = "DELETE FROM danhba WHERE id = " + id;
                            db.execSQL(sql);
                            Loaddata();
                        }
                    }).setNegativeButton("Canncel", null)
                    .show();
        }

        private void hienThiSua() {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_dialog_edit);
            dialog.show();
            final EditText edten, edso;
            Button btnsua, btnhuy;
            final RadioButton rdnam, rdnu;

            edten =  dialog.findViewById(R.id.edtendialogsua);
            edso =  dialog.findViewById(R.id.edsodialogsua);
            btnhuy =  dialog.findViewById(R.id.btnhuydialogsua);
            btnsua =  dialog.findViewById(R.id.btneditdialog);
            rdnam =  dialog.findViewById(R.id.rdnamsua);
            rdnu =  dialog.findViewById(R.id.rdnusua);


            edten.setText(danhbaList.get(getPosition()).getTen().toString().trim());
            edso.setText(danhbaList.get(getPosition()).getSdt().toString().trim());
            if (danhbaList.get(getPosition()).getGt().toString().trim().equals(rdnam.getText().toString().trim())){
                rdnam.setChecked(true);
            }else {
                rdnu.setChecked(true);
            }
            btnhuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnsua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String gt;
                    String ten = edten.getText().toString().trim();
                    String so = edso.getText().toString().trim();

                    if (rdnam.isChecked()){
                        gt = rdnam.getText().toString().trim();
                    }else {
                        gt = rdnu.getText().toString().trim();
                    }
                    suaDanhBa(danhbaList.get(getPosition()).getId(),ten, so, gt);
                    dialog.dismiss();
                    Loaddata();

                }
            });

        }
        private void suaDanhBa(int id, String ten, String so, String gt) {
            if (ten.isEmpty() || so.isEmpty()) {
                Toast.makeText(context, "Nhập đầy đủ thông tin!!", Toast.LENGTH_SHORT).show();
            } else {
                db = context.openOrCreateDatabase("danhba.db", Context.MODE_PRIVATE, null);
                String sql = "UPDATE danhba SET ten = '" + ten + "', sdt = '" + so + "', gt = '" + gt + "' WHERE id = " + id;
                db.execSQL(sql);
                Toast.makeText(context, "Sửa thành công!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
