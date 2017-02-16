package com.android.jh.selfmemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

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
        setList();
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
        transaction.add(R.id.frame_layout,detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void goDetail(int position) {
        FragmentTransaction transaction = manager.beginTransaction();
        detail.updateToData(datas.get(position));
        transaction.add(R.id.frame_layout,detail);
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
