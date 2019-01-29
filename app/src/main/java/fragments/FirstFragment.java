package fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.ValuteAdapter;
import cache.Valute;
import navigation.MainNavigation;
import navigation.NavigationType;
import ru.makdyff.globus.App;
import ru.makdyff.globus.R;

@SuppressLint("ValidFragment")
public class FirstFragment extends FragmentBase<NavigationType, MainNavigation> {
    private Spinner _valute1;
    private Spinner _valute2;
    private Button _update;
    private Button _convert;
    private EditText _edit;
    private TextView _value;
    private List<Valute> _valutes;
    private ValuteAdapter _ad;

    public FirstFragment(MainNavigation mainNavigation) {
        super(mainNavigation);
    }

    public FirstFragment() {
        super(new MainNavigation(null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        _valutes = new ArrayList<>();

        _valute1 = view.findViewById(R.id.fragment_first_valute1);
        _valute2 = view.findViewById(R.id.fragment_first_valute2);
        _update = view.findViewById(R.id.fragment_first_update);
        _value = view.findViewById(R.id.fragment_first_value);
        _convert = view.findViewById(R.id.fragment_first_convert);
        _edit = view.findViewById(R.id.fragment_first_edit);

        _ad = new ValuteAdapter(getActivity(), _valutes);
        _valute1.setAdapter(_ad);
        _valute2.setAdapter(_ad);

        _update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCache();
            }
        });

        _convert.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(_edit.getText().length() != 0) {
                    int value = Integer.parseInt(_edit.getText().toString());
                    Valute v1 = (Valute)_valute1.getSelectedItem();
                    Valute v2 = (Valute)_valute2.getSelectedItem();

                    double vv = value * v1.Value / v2.Value;
                    _value.setText(Double.toString(vv));

                } else {
                    Toast.makeText(getActivity(), "Введите значение", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        List<Valute> v = App.SqlCon.getObjectList(Valute.class);
        if (v == null || v.size() != 0)
            updateCache();
        else {
            _valutes.addAll(v);
            _ad.notifyDataSetChanged();
        }
    }

    private void updateCache() {
        progressShow(null);

        pool.submit(new Runnable() {
            @Override
            public void run() {
                _valutes.clear();

                try {
                    List tmp = App.Disp.getValutes();
                    _valutes.addAll(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                App.SqlCon.delete(Valute.class);
                App.SqlCon.setObjects(Valute.class, _valutes);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _ad.notifyDataSetChanged();
                    }
                });

                progressDismiss();
            }
        });
    }
}
