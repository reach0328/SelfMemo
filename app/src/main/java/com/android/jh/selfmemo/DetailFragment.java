package com.android.jh.selfmemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.jh.selfmemo.domain.Memo;
import com.android.jh.selfmemo.interfaces.DetailInterface;

import java.sql.SQLException;
import java.util.Date;

public class DetailFragment extends Fragment implements View.OnClickListener {

    int position = -1;
    boolean flag = false;
    Context context = null;
    View view = null;
    Button btn_save, btn_cancel;
    EditText et_Memo;
    DetailInterface detailInterface;
    Memo memo, before;
    public DetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view != null)
            return view;
        view =inflater.inflate(R.layout.fragment_detail, container, false);
        btn_save = (Button)view.findViewById(R.id.btn_save);
        btn_cancel = (Button)view.findViewById(R.id.btn_cancle);
        et_Memo = (EditText)view.findViewById(R.id.et_content);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        Log.i("create","==================================="+et_Memo.getText());
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.detailInterface = (DetailInterface) context;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("attach","==================================="+et_Memo.getText());
        et_Memo.setText("");
        if(flag){
            Log.i("attach","==================================="+memo.getMemo());
            et_Memo.setText(memo.getMemo());
            Log.i("attach","==================================="+et_Memo.getText());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save :
                if(flag) {
                    try {
                        memo.setMemo(et_Memo.getText().toString());
                        detailInterface.updateToLIst(memo);
                        flag = false;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Memo memo = makeMemo();
                        detailInterface.saveToList(memo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_cancle :
                detailInterface.backToList();
                et_Memo.setText(" ");
                Log.i("cancel","==================================="+et_Memo.getText());
                flag = false;
                break;
        }
    }

    private Memo makeMemo() {
        Memo memo = new Memo();
        memo.setMemo(et_Memo.getText().toString());
        memo.setDate(new Date(System.currentTimeMillis()));
        return memo;
    }

    public void updateToData(Memo memo) {
        this.memo = memo;
        Log.i("update","====================="+memo.getMemo());
        Log.i("MEMEMEMEMME","----------------------------"+memo.getId());
        flag = true;
    }
}
