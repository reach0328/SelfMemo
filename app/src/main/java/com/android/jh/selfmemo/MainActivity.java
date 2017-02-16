package com.android.jh.selfmemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.jh.selfmemo.data.DBHelper;
import com.android.jh.selfmemo.domain.Memo;
import com.android.jh.selfmemo.interfaces.DetailInterface;
import com.android.jh.selfmemo.interfaces.ListInterface;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DetailInterface,ListInterface{

    // 권한 요청 코드
    private final int REQ_PERMISSION = 100;
    ListFragment list;
    DetailFragment detail;
    FrameLayout main;
    FragmentManager manager;
    List<Memo> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = ListFragment.newInstance(1);
        detail = DetailFragment.newInstance();
        main = (FrameLayout)findViewById(R.id.frame_layout);
        manager = getSupportFragmentManager();
        try {
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.setDatas(datas);

        // 권한 처리
        checkPermission();
    }
    public void loadData() throws SQLException{
        DBHelper dbHelper = OpenHelperManager.getHelper(this,DBHelper.class);
        Dao<Memo,Integer> memoDao = dbHelper.getMemoDao();
        datas = memoDao.queryForAll();
    }

    private void setList() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_layout,list);
        transaction.commit();
    }

    @Override
    public void goDetail() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout,detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void goDetail(int position) {
        FragmentTransaction transaction = manager.beginTransaction();
        detail.updateToData(datas.get(position));
        transaction.replace(R.id.frame_layout,detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void delete(int position) throws SQLException{
        DBHelper dbHelper = OpenHelperManager.getHelper(this,DBHelper.class);
        Dao<Memo,Integer> memoDao = dbHelper.getMemoDao();
        Memo memo = datas.get(position);
        Log.i("MAIN", "0--------------------------------------" + memo.getMemo());
        memoDao.delete(memo);
        loadData();
        list.setDatas(datas);
        for(int i = 0; i< datas.size(); i++) {
            Log.i("MAIN", "0--------------------------------------" + datas.get(i).getMemo());
        }
        list.refreshAdapter();
    }

    private void checkPermission() {
        //버전 체크해서 마시멜로우(6.0)보다 낮으면 런타임 권한 체크를 하지않는다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermssion(this, REQ_PERMISSION)) {
                setList();
            }
        } else {
            setList();
        }
    }

    //권한체크 후 콜백< 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 됐으면
            if (PermissionControl.onCheckedResult(grantResults)) {
                setList();
            } else {
                Toast.makeText(this, "권한을 사용하지 않으시면 프로그램을 실행시킬수 없습니다", Toast.LENGTH_SHORT).show();
                finish();
                // 선택 1.종료, 2. 권한체크 다시물어보기
                //PermissionControl.checkPermssion(this,REQ_PERMISSION);
            }
        }
    }
    @Override
    public void saveToQuick(Memo memo) throws SQLException {
        DBHelper dbHelper = OpenHelperManager.getHelper(this,DBHelper.class);
        Dao<Memo,Integer> memoDao = dbHelper.getMemoDao();
        memoDao.create(memo);
        loadData();
        list.setDatas(datas);
        for(int i = 0; i< datas.size(); i++) {
            Log.i("MAIN", "0--------------------------------------" + datas.get(i).getMemo());
        }
        list.refreshAdapter();
    }

    @Override
    public void backToList() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        detail.resetText();
    }

    @Override
    public void saveToList(Memo memo) throws SQLException {
        DBHelper dbHelper = OpenHelperManager.getHelper(this,DBHelper.class);
        Dao<Memo,Integer> memoDao = dbHelper.getMemoDao();
        memoDao.create(memo);
        loadData();
        list.setDatas(datas);
        list.refreshAdapter();
        super.onBackPressed();
    }

    @Override
    public void updateToLIst(Memo memo) throws SQLException {
        DBHelper dbHelper = OpenHelperManager.getHelper(this,DBHelper.class);
        Dao<Memo,Integer> memoDao = dbHelper.getMemoDao();
        Memo temp =memoDao.queryForId(memo.getId());
        temp.setMemo(memo.getMemo());
        temp.setDate(new Date(System.currentTimeMillis())) ;
        temp.setImgUri(memo.getImgUri());
        memoDao.update(temp);
        loadData();
        Log.i("MEMEMEMEMME","----------------------------"+temp.getId()+"-----------"+temp.getMemo());
        Log.i("MEMEMEMEMME","----------------------------"+memo.getId()+"-----------"+temp.getMemo());
        // List<Memo> test = memoDao.queryForAll();
        for(int i = 0; i< datas.size(); i++) {
            Log.i("MAIN", "0--------------------------------------" + datas.get(i).getMemo());
        }
        list.refreshAdapter();
        super.onBackPressed();
    }

}
