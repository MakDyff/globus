package service;

import android.annotation.SuppressLint;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by max on 07.03.16.
 */
class ServiceResponses<T> extends DefaultHandler {
    private final List<T> _objectList;
    private final String _simpleName;
    private final SAXParserFactory _factory;
    private final Class<?> _class;
    private int current_field;
    private String current_value;
    private final Field[] fields;

    private final HashMap<String, Integer> _hashMap;

    private StringReader _stringReader;
    private final InputSource _inputSource;

    public List<T> getObjectList() {
        return _objectList;
    }

    public void parse(String response) throws Exception {
        _stringReader = new StringReader(response);
        _inputSource.setCharacterStream(_stringReader);

        _factory.newSAXParser().parse(_inputSource, this);
    }

    public ServiceResponses (Class<T> cl) {
        _factory = SAXParserFactory.newInstance();
        _class = cl;
        _simpleName = _class.getSimpleName();
        _objectList = new ArrayList<T>();
        _inputSource = new InputSource();
        _hashMap = new HashMap<>();

        fields = _class.getFields();
        current_value  = "";

        fillHashMap();
    }

    private void fillHashMap() {
        for(int i = 0; i < fields.length; i++)
            _hashMap.put(fields[i].getName(), i);
    }

    private T getObject() {
        try {
            return (T)_class.newInstance();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return (T)new Object();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        super.startElement(uri, localName, qName, attributes);

		if(_simpleName.equalsIgnoreCase(qName)) {
                _objectList.add(getObject());
        }

        current_field = _hashMap.containsKey(qName)? _hashMap.get(qName) : -1;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, qName);
        if(current_field != -1 && current_value != "") {

            try {
                Field field = fields[current_field];
                Class<?> type = field.getType();
                Object value = current_value;

                if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
                    value = Integer.valueOf(value.toString());
                } else if (BigDecimal.class.isAssignableFrom(type)) {
                    value = BigDecimal.valueOf(Double.valueOf(value.toString()));
                } else if (java.util.Date.class.isAssignableFrom(type)) {
                    value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(value.toString());
                } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
                    String boolStr = value.toString();
                    if(boolStr.contains("true") || boolStr.contains("false"))
                        value = Boolean.valueOf(boolStr);
                    else
                        value = "1".equalsIgnoreCase(boolStr);
                } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
                    value = Double.valueOf(value.toString());

                Object obj = _objectList.get(_objectList.size() - 1);
                _class.getField(field.getName()).set(obj, value);

                current_value = "";

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        super.characters(ch, start, length);

        if(current_field != -1 && length != 0)
            current_value += new String(ch, start, length);
    }
}
