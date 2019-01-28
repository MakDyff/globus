package sqlite;

import android.database.Cursor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by max on 13.03.16.
 */
public interface ConnectorListen<T extends Serializable> {
    T getDataObject(Cursor cursor);
    Class<T> getObjectClass();
    void addDataObject(Cursor cursor);
}
