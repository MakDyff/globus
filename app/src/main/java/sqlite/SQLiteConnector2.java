package sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by max on 07.05.17.
 */

public class SQLiteConnector2 {
    private final DateFormat writeFormat;
    private final SQLite _sqlLite;

    @SuppressLint("SimpleDateFormat")
    public SQLiteConnector2(Context context) {
        _sqlLite = SQLite.getInstance(context);
        writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public <T extends Serializable> T first(Class<T> classT) {
        List<T> list = getObjectList(classT,"","");
        if(list.size() != 0) return list.get(0);
        else return null;
    }

    public <T extends Serializable> List<T> getObjectList(Class<T> classT) {
        return getObjectList(classT,"","");
    }

    public <T extends Serializable> List<T> getObjectList(Class<T> classT, String idValue, String idName) {
        CL<T> cl = new CL<>(classT);

        Log.i("SQLiteConnector",  cl.getObjectClass().getSimpleName());

        if(idValue.length() != 0)
            _sqlLite.selectData(cl, String.format(" WHERE %s = '%s'", idName, idValue));
        else
            _sqlLite.selectData(cl);

        return cl.getList();
    }

    public <T extends Serializable> void updateObject(Class<T> classT, Object value, String idValue, String idName) {
        CL<T> cl = new CL<>(classT);
        ContentValues values = createContentValue(value);
        _sqlLite.update(cl, values, idValue, idName);
    }

    public <T extends Serializable> void setObjects(Class<T> classT, List<T> values) {
        List<ContentValues> list = new LinkedList<>();
        CL<T> cl = new CL<>(classT);

        for(Object value : values) {
            list.add(createContentValue(value));
        }
        _sqlLite.insert(cl, list);
    }

    public <T extends Serializable> void setObjects(Class<T> classT, Collection<T> values) {
        List<ContentValues> list = new LinkedList<>();
        CL<T> cl = new CL<>(classT);

        for(Object value : values) {
            list.add(createContentValue(value));
        }
        _sqlLite.insert(cl, list);
    }

    public <T extends Serializable> void setObjects(Class<T> classT, T[] values) {
        List<ContentValues> list = new LinkedList<>();
        CL<T> cl = new CL<>(classT);

        for(Object value : values) {
            list.add(createContentValue(value));
        }

        _sqlLite.insert(cl, list);
    }

    public <T extends Serializable> void addObject(Class<T> classT, T value) {
        CL<T> cl = new CL<>(classT);
        ContentValues cv = createContentValue(value);
        _sqlLite.insert(cl, cv);
    }

    public <T extends Serializable> void delete(Class<T> classT) {
        CL<T> cl = new CL<>(classT);
        _sqlLite.deleteTable(cl);
    }

    /**
     * Создается объект для добавления в таблицу
     */
    private ContentValues createContentValue(Object value) {
        ContentValues cv = new ContentValues();

        try {
            Field [] fields = value.getClass().getFields();
            for(Field field : fields) {
                Object obj = field.get(value);
                Class<?> cl = field.getType();
                if (obj != null) {
                    String nameField = String.format("[%s]", field.getName());

                    if (Integer.class.isAssignableFrom(cl) || int.class.isAssignableFrom(cl))
                        cv.put(nameField, Integer.valueOf(obj.toString()));
                    else if (BigDecimal.class.isAssignableFrom(cl))
                        cv.put(nameField, Double.valueOf(obj.toString()));
                    else if (java.util.Date.class.isAssignableFrom(cl))
                        cv.put(nameField, writeFormat.format(obj));
                    else if (double.class.isAssignableFrom(cl) || Double.class.isAssignableFrom(cl))
                        cv.put(nameField, Double.valueOf(obj.toString()));
                    else if (String.class.isAssignableFrom(cl))
                        cv.put(nameField, obj.toString());
                    else if (Boolean.class.isAssignableFrom(cl) || boolean.class.isAssignableFrom(cl))
                        cv.put(nameField, obj.toString());
                    else if (Bitmap.class.isAssignableFrom(cl)) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ((Bitmap)obj).compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitmapdata = stream.toByteArray();

                        String values = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                        cv.put(nameField, values);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return cv;
    }

    private class CL<T extends Serializable> implements ConnectorListen<T> {
        private final List<T> _listTemp;
        private final DateFormat writeFormat;
        private final Class<T> _classT;

        @SuppressLint("SimpleDateFormat")
        CL(Class<T> classT) {
            _classT = classT;
            _listTemp = new LinkedList<>();
            writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        public List<T> getList() {
            return _listTemp;
        }

        @Override
        public T getDataObject(Cursor cursor) {
            T objClass = null;

            try {
                objClass = getObjectClass().newInstance();


                for(Field field : objClass.getClass().getFields()) {
                    String value = cursor.getString(cursor.getColumnIndex(field.getName()));
                    Class<?> type = field.getType();
                    Object v = null;
                    if (value != null) {
                        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
                            v = Integer.valueOf(value);

                        else if (BigDecimal.class.isAssignableFrom(type))
                            v = BigDecimal.valueOf(Double.valueOf(value));

                        else if (java.util.Date.class.isAssignableFrom(type))
                            v = writeFormat.parse(value);

                        else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type))
                            v = Double.valueOf(value);

                        else if (String.class.isAssignableFrom(type))
                            v = value;

                        else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
                            v = Boolean.valueOf(value);

                        else if (Bitmap.class.isAssignableFrom(type)) {
                            if(value.length() != 0) {
                                byte[] response = Base64.decode(value, Base64.DEFAULT);
                                v = BitmapFactory.decodeByteArray(response, 0, response.length);
                            }
                        }

                        field.set(objClass, v);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return objClass;
        }

        @Override
        public Class<T> getObjectClass() {
            return _classT;
        }

        @Override
        public void addDataObject(Cursor cursor) {
            T data = getDataObject(cursor);
            _listTemp.add(data);
        }
    }
}


