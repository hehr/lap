package com.hehr.lap.utils.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class DBManager {

    private static DBHelper dbHelper;

    private SQLiteDatabase sqLiteDatabase;

    public DBManager(Context context , String dbName) {

        dbHelper = new DBHelper( context , dbName);

        sqLiteDatabase = dbHelper.getWritableDatabase();

    }

    /**
     * sql执行查询操作的sql语句
     * selectionargs查询条件
     * 返回查询的游标，可对数据进行操作，但是需要自己关闭游标
     */
    public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception {
        Cursor cursor = null;
        if (sqLiteDatabase.isOpen()) {
            cursor = sqLiteDatabase.rawQuery(sql, selectionArgs);
        } else {
            throw new RuntimeException("The DataBase has already closed");
        }
        return cursor;
    }


    /**
     * 在一个事务中批量更新数据
     * @param tableName
     * @param values
     * @return
     */
    public long  updateDateInTransaction(String  tableName , List<ContentValues> values ){

        long count = 0;

        try {

            sqLiteDatabase.beginTransaction();

            for (ContentValues value:values) {
                count += sqLiteDatabase.replace( tableName ,null,value);
            }

            sqLiteDatabase.setTransactionSuccessful();

        }finally {

            sqLiteDatabase.endTransaction();

        }

        return count;


    }


    /**
     * @param table         表名
     * @param columns       查询需要返回的列的字段
     * @param selection     SQL语句中的条件语句
     * @param selectionArgs 占位符的值
     * @param groupBy       表示分组，可以为NULL
     * @param having        SQL语句中的having，可以为null
     * @param orderBy       表示结果排序，可以为null
     * @param limit         表示分页
     * @return
     * @throws Exception
     */
    public Cursor queryData(String table, String[] columns, String selection, String[] selectionArgs,
                            String groupBy, String having, String orderBy, String limit) throws Exception {
        return queryData(false, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * @param distinct      true if you want each row to be unique,false otherwise
     * @param table         表名
     * @param columns       查询需要返回的列的字段
     * @param selection     SQL语句中的条件语句
     * @param selectionArgs 占位符的值
     * @param groupBy       表示分组，可以为NULL
     * @param having        SQL语句中的having，可以为null
     * @param orderBy       表示结果排序，可以为null
     * @param limit         表示分页
     * @return
     * @throws Exception
     */
    public Cursor queryData(boolean distinct, String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy,
                            String having, String orderBy, String limit) throws Exception {
        return queryData(null, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * @param cursorFactory 游标工厂
     * @param distinct      true if you want each row to be unique,false otherwise
     * @param table         表名
     * @param columns       查询需要返回的列的字段
     * @param selection     SQL语句中的条件语句
     * @param selectionArgs 占位符的值
     * @param groupBy       表示分组，可以为NULL
     * @param having        SQL语句中的having，可以为null
     * @param orderBy       表示结果排序，可以为null
     * @param limit         表示分页
     * @return
     * @throws Exception
     */
    public Cursor queryData(SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy,
                            String having, String orderBy, String limit) throws Exception {
        Cursor cursor = null;
        if (sqLiteDatabase.isOpen()) {
            cursor = sqLiteDatabase.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } else {
            throw new RuntimeException("The database has already closed!");
        }
        return cursor;
    }

    /**
     * 销毁数据库资源
     */
    public void destroy(){

        if(sqLiteDatabase !=null && sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }

        sqLiteDatabase = null;

        if (dbHelper != null) {
            dbHelper.close();
        }

        dbHelper = null;

    }

}
