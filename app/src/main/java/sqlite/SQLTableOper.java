package sqlite;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cache.Valute;

public class SQLTableOper {
    private static StringBuilder _table = new StringBuilder();
    private static StringBuilder _value = new StringBuilder();

    private static List<Class<Valute>> _tableList = Collections.singletonList(Valute.class);

    public static String getTableName(Class<?> cl) {
        if (_tableList.contains(cl)) return cl.getSimpleName();
        else return "";
    }

    public static String genSqlSelect(Class<?> cl) {
        return "SELECT * FROM " + getTableName(cl);
    }

    /**
     * Создаем запросы для создания таблиц
     * можно добавить исключающий класс
     */
    public static List<String> genSqlCreateTableList(Class<?> except) {
        List<String> list = new ArrayList<>();

        try {
            for(Class<?> t : _tableList) {
                if (except != null && except.equals(t)) break;

                String str = createTable(t);
                list.add(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    /**
     * Создает запрос на удаление таблиц
     * можно добавить исключающий класс
     */
    public static List<String> genSqlDropTableList(Class<?> except) {
        List<String> list = new ArrayList<>();

        for(Class<?> t : _tableList) {
            if (except != null && except.equals(t)) break;

            String str = "DROP TABLE IF EXISTS " + t.getSimpleName();
            list.add(str);
        }

        return list;
    }

    /**
     * Генерация запроса для создания таблицы по классу
     * kotlin не всегда может отличить java типы от своих
     */
    private static String createTable(Class<?> cl) throws InstantiationException, IllegalAccessException {
        _table.setLength(0);

        Object obj = cl.newInstance();
        for(Field field : obj.getClass().getFields())
        {
            _value.append(String.format("[%s]", field.getName()));
            Class<?> type = field.getType();

            if (int.class.isAssignableFrom(type)
                    || Integer.class.isAssignableFrom(type))
            _value.append(" INTEGER, ");
            else if (BigDecimal.class.isAssignableFrom(type)
                    || java.lang.Double.TYPE.isAssignableFrom(type)
                    || Double.class.isAssignableFrom(type))
            _value.append(" NUMERIC, ");
            else /*if (java.util.Date::class.java.isAssignableFrom(type)
                    || String::class.java.isAssignableFrom(type)
                    || java.lang.String::class.java.isAssignableFrom(type)
                    || Boolean::class.java.isAssignableFrom(type)
                    || java.lang.Boolean::class.java.isAssignableFrom(type)
                    || Bitmap::class.java.isAssignableFrom(type))*/
            _value.append(" text, ");

            _table.append(_value);
            _value.setLength(0);
        }

        _table.delete(_table.length() - 2, _table.length());
        return String.format("create table %s ( _id integer primary key autoincrement, %s)", cl.getSimpleName(), _table);
    }
}
