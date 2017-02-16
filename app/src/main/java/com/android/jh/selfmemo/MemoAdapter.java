package com.android.jh.selfmemo;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jh.selfmemo.domain.Memo;
import com.android.jh.selfmemo.interfaces.ListInterface;
import com.bumptech.glide.Glide;

import java.sql.SQLException;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    Context context;
    List<Memo> datas;
    ListInterface listInterface;
    View view;
    AlertDialog dialog;

    public MemoAdapter(Context context,List<Memo> datas) {
        this.context = context;
        this.datas = datas;
        listInterface = (ListInterface) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_memo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tv_content.setText(datas.get(position).getMemo());
        holder.position = position;
        holder.tv_listdate.setText(datas.get(position).getDate().toString());
        if(datas.get(position).getImgUri()!=null) {
            Log.i("listlist","============================"+datas.get(position).getImgUri());
            holder.list_img.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.parse(datas.get(position).getImgUri()))
                    .into(holder.list_img);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_content;
        public TextView tv_listdate;
        public CardView cardView;
        public ImageView list_img;
        public int position;
        public ViewHolder(View view) {
            super(view);
            tv_content = (TextView) view.findViewById(R.id.textView);
            tv_listdate = (TextView) view.findViewById(R.id.tv_listdate);
            list_img = (ImageView) view.findViewById(R.id.list_img);
            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listAlert(position);
                }
            });
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    makeAlert(position);
                    return false;
                }
            });
        }
    }

    public void listAlert(int position) {
        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view3 = inflater.inflate(R.layout.detailalert,null);

        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        final AlertDialog.Builder buider = new AlertDialog.Builder(context); //AlertDialog.Builder 객체 생성
        TextView tv_content = (TextView)view3.findViewById(R.id.tv_content);
        TextView tv_date = (TextView)view3.findViewById(R.id.date);
        final ImageView show_img = (ImageView) view3.findViewById(R.id.show_img);

        if(datas.get(position).getImgUri()!= null) {
            show_img.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse(datas.get(position).getImgUri());
            Glide.with(context)
                    .load(uri)
                    .into(show_img);
        }

        Button btn_ok = (Button)view3.findViewById(R.id.btn_ok);
        tv_content.setText(datas.get(position).getMemo());
        Log.i("tvvvvvvvvvvv","++++++++++++++++++++++++++++++++++"+tv_content.getText());
        tv_date.setText(datas.get(position).getDate().toString());
        buider.setView(view3);
        dialog = buider.create();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                show_img.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    public void makeAlert(final int position) {
        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view2 = inflater.inflate(R.layout.listalert,null);

        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        final AlertDialog.Builder buider = new AlertDialog.Builder(context); //AlertDialog.Builder 객체 생성
        TextView tv_update = (TextView)view2.findViewById(R.id.tv_update);
        TextView tv_delete = (TextView)view2.findViewById(R.id.tv_delete);
        Log.i("loggoogog","============================"+position);
        buider.setView(view2);
        dialog = buider.create();
        dialog.show();
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("loggoogog","============================");
                listInterface.goDetail(position);
                dialog.dismiss();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ldelete","============================");
                try {
                    listInterface.delete(position);
                    dialog.dismiss();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
