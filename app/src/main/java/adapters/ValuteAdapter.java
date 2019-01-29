package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.List;

import cache.Valute;

public class ValuteAdapter extends ArrayAdapter<Valute> {

    private final Context context;
    private final List<Valute> values;

    public ValuteAdapter(Context context, List<Valute> values) {
        super(context, android.R.layout.simple_spinner_dropdown_item, values);
        this.context = context;
        this.values = values;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        CheckedTextView textV = view.findViewById(android.R.id.text1);
        Valute value = values.get(position);

        textV.setText(value.Name + " (" + value.CharCode + ") - " + value.Value);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        CheckedTextView textV = view.findViewById(android.R.id.text1);
        Valute value = values.get(position);

        textV.setText(value.Name + " (" + value.CharCode + ") - " + value.Value);

        return view;
    }
}
