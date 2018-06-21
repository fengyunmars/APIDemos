package com.example.android.apis.db;

import android.app.Application;


/**
 * Created by Moon on 2017/10/19.
 */

public class DBManager {
    private static final DBManager ourInstance = new DBManager();
    static final String DB_NAME = "person_face.db";


    public static DBManager getInstance() {
        return ourInstance;
    }

//    DaoMaster.DevOpenHelper mDevOpenHelper;
//    DaoMaster daoMaster;
//    DaoSession daoSession;
//
//    private DBManager() {
//
//    }
//
//    public void init(Application application) {
//        mDevOpenHelper = new DaoMaster.DevOpenHelper(application, DB_NAME);
//        daoMaster = new DaoMaster(mDevOpenHelper.getWritableDatabase());
//        daoSession = daoMaster.newSession();
//    }
//
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }
}
