package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Iterator;
import java.util.List;

/**
 * Created by max on 01.01.16.
 */
class SQLite extends SQLiteOpenHelper {
    private volatile static SQLite _sqLite;
    private static final int DB_VERSION = 1;

    private volatile Boolean _bool;

    public static synchronized SQLite getInstance(Context context) {
        return _sqLite == null ? _sqLite = new SQLite(context.getApplicationContext()) : _sqLite;
    }

    private SQLite(Context context) {
        super(context, "qwerty", null, DB_VERSION);
        _bool = false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SQLite", "Создание БД");
        for(String value : SQLTableOper.genSqlCreateTableList(null))
            db.execSQL(value);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("SQLite", "Обновление БД");

        for(String value : SQLTableOper.genSqlDropTableList(null))
            db.execSQL(value);

        for(String value : SQLTableOper.genSqlCreateTableList(null))
            db.execSQL(value);
    }

    public void selectData(ConnectorListen connectorListen) {
        synchronized(_bool) {
            String selectQuery = SQLTableOper.genSqlSelect(connectorListen.getObjectClass());

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (!cursor.isClosed())
                while (cursor.moveToNext())
                    connectorListen.addDataObject(cursor);

            db.close();
        }
    }

    public void selectData(ConnectorListen connectorListen, String query) {
        synchronized(_bool) {
            String selectQuery = SQLTableOper.genSqlSelect(connectorListen.getObjectClass()) + query;

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (!cursor.isClosed())
                while (cursor.moveToNext())
                    connectorListen.addDataObject(cursor);

            db.close();
        }
    }

    public void deleteTable(ConnectorListen connectorListen) {
        synchronized(_bool) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SQLTableOper.getTableName(connectorListen.getObjectClass()), null, null);
            db.close();
        }
    }

    public void insert(ConnectorListen connectorListen, ContentValues cv) {
        synchronized(_bool) {
            SQLiteDatabase db = getWritableDatabase();
            db.insert(SQLTableOper.getTableName(connectorListen.getObjectClass()), null, cv);
            db.close();
        }
    }

    public void insert(ConnectorListen connectorListen, List<ContentValues> cv) {
        synchronized(_bool) {
            SQLiteDatabase db = getWritableDatabase();

            Iterator<ContentValues> iterator = cv.iterator();
            while (iterator.hasNext()) {
                ContentValues c = iterator.next();
                db.insert(SQLTableOper.getTableName(connectorListen.getObjectClass()), null, c);
            }

            db.close();
        }
    }

    public void update(ConnectorListen connectorListen, ContentValues values, String idValue, String idName) {
        synchronized(_bool) {
            SQLiteDatabase db = getWritableDatabase();
            db.update(SQLTableOper.getTableName(connectorListen.getObjectClass()),
                    values,
                    idName + " = ?",
                    new String[]{idValue});
            db.close();
        }
    }
}
