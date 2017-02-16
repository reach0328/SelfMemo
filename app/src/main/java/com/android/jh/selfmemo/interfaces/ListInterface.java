package com.android.jh.selfmemo.interfaces;

import com.android.jh.selfmemo.domain.Memo;

import java.sql.SQLException;

/**
 * Created by JH on 2017-02-14.
 */

public interface ListInterface {
    void goDetail();
    void goDetail(int position);
    void delete(int position) throws SQLException;
    void saveToQuick(Memo memo) throws SQLException;
}
