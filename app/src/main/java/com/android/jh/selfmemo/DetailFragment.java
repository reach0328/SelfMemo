package com.android.jh.selfmemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.jh.selfmemo.domain.Memo;
import com.android.jh.selfmemo.interfaces.DetailInterface;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class DetailFragment extends Fragment implements View.OnClickListener {

    // 카메라 요청 코드
    private final int REQ_CAMERA = 101;
    // 겔러리 요청 코드
    private final int REQ_GALLERY = 102;

    boolean flag = false;
    Context context = null;
    View view = null;
    ImageView imgView;
    EditText et_Memo;
    DetailInterface detailInterface;
    Memo memo;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;
    Uri fileUri = null;

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
        materialDesignFAM = (FloatingActionMenu) view.findViewById(R.id.detail_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.detail_floating_action_menu_picture);
        floatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.detail_floating_action_menu_add);
        floatingActionButton3 = (FloatingActionButton) view.findViewById(R.id.detail_floating_action_menu_cancel);
        et_Memo = (EditText)view.findViewById(R.id.et_content);
        imgView = (ImageView) view.findViewById(R.id.detail_img);
        Log.i("create","==================================="+et_Memo.getText());
        floatingActionButton1.setOnClickListener(this);
        floatingActionButton2.setOnClickListener(this);
        floatingActionButton3.setOnClickListener(this);
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
        if(flag){
            Log.i("attach","==================================="+memo.getMemo());
            et_Memo.setText(memo.getMemo());
            Log.i("attach","==================================="+et_Memo.getText());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.detail_floating_action_menu_picture :
                getPicture();
                break;
            case R.id.detail_floating_action_menu_add :
                if(flag) {
                    try {
                        memo.setMemo(et_Memo.getText().toString());
                        memo.setImgUri(fileUri.toString());
                        imgView.setVisibility(View.GONE);
                        detailInterface.updateToLIst(memo);
                        flag = false;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Memo memo = makeMemo();
                        imgView.setVisibility(View.GONE);
                        detailInterface.saveToList(memo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.detail_floating_action_menu_cancel :
                detailInterface.backToList();
                resetText();
                Log.i("cancel","==================================="+et_Memo.getText());
                flag = false;
                imgView.setVisibility(View.GONE);
                break;
        }
    }

    private Memo makeMemo() {
        Memo memo = new Memo();
        memo.setMemo(et_Memo.getText().toString());
        memo.setImgUri(fileUri.toString());
        memo.setDate(new Date(System.currentTimeMillis()));
        return memo;
    }

    public void updateToData(Memo memo) {
        this.memo = memo;
        Log.i("update","====================="+memo.getMemo());
        Log.i("MEMEMEMEMME","----------------------------"+memo.getId());
        flag = true;
    }

    public void resetText() {
        if(et_Memo.getText().toString() != null)
            et_Memo.setText("");
    }

    public void getPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");//외부 저장소에 있는 이미지만 가져오기 위한 필터리
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_GALLERY:
                if (resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    Log.i("file","============================================"+fileUri.toString());
                    Glide.with(this)
                            .load(fileUri)
                            .into(imgView);
                    Log.i("file","============================================"+imgView.getId());
                    imgView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}