package com.android.jh.selfmemo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.jh.selfmemo.domain.Memo;
import com.android.jh.selfmemo.interfaces.ListInterface;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ListFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<Memo> datas = new ArrayList<>();
    RecyclerView recyclerView;
    MemoAdapter adapter;
    Context context = null;
    View view = null;
    ListInterface listInterface;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    AlertDialog dialog;

    public ListFragment() {

    }
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        materialDesignFAM = (FloatingActionMenu) view.findViewById(R.id.list_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.list_floating_action_menu_quick);
        floatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.list_floating_action_menu_detail);

        // Set the adapter
        Context context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MemoAdapter(context,datas);
        recyclerView.setAdapter(adapter);
        floatingActionButton1.setOnClickListener(this);
        floatingActionButton2.setOnClickListener(this);
        return view;
    }

    private void refreshList() {
        adapter = new MemoAdapter(context,datas);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listInterface = (ListInterface) context;
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setDatas(List<Memo> datas) {
        this.datas = datas;
    }

    public void refreshAdapter() {
        adapter = new MemoAdapter(context, datas);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_floating_action_menu_quick :
                quickAddAlert();
                materialDesignFAM.close(true);
                break;
            case R.id.list_floating_action_menu_detail :
                listInterface.goDetail();
                materialDesignFAM.close(true);
                break;
        }
    }

    public void quickAddAlert() {
        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view4 = inflater.inflate(R.layout.quick_add_alert, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        final AlertDialog.Builder buider = new AlertDialog.Builder(context); //AlertDialog.Builder 객체 생성
        TextView tv_quick = (TextView) view4.findViewById(R.id.tv_quick);
        final EditText et_quick_content = (EditText) view4.findViewById(R.id.et_quick_content);

        et_quick_content.setText("");
        Button btn_ok = (Button) view4.findViewById(R.id.btn_add_ok);
        Button btn_cancle = (Button)view4.findViewById(R.id.btn_quick_cancel);

        Log.i("tvvvvvvvvvvv", "++++++++++++++++++++++++++++++++++" + tv_quick.getText());
        buider.setView(view4);
        dialog = buider.create();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listInterface.saveToQuick(new Memo(et_quick_content.getText().toString(),new Date(System.currentTimeMillis())));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
