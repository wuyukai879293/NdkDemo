package com.example.wuyukai.ndkdemo;

/**
 * Created by wuyukai on 16/12/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteTool extends SQLiteOpenHelper{

    public SQLiteTool(Context context, String name) {
        /**
         * 构造方法根据传递过来的数据创建数据库，这个方法在属性上面有说过
         * 参数1：上下文
         * 参数2：数据库的名称
         * 参数3：工厂游标（可为null）
         * 参数4：数据库版本，如果这个版本号有变则会调用onUpgrade方法而不是调用onCreate
         */
        super(context, name, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个数据库
        db.execSQL("create table user2(_id integer primary key autoincrement,user_id int(10),user_name varchar(20),ges_id int(10),ges_name varchar(20),ges_arrayX varchar(100),ges_arrayZ varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这个方法是用来更新数据库版本的时候用的，例如我们在app版本从1升级到2之后，数据库的表结构有改变的话可以通过这个方法来进行更新数据库结构
    }
}
