package fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cache.Valute;
import navigation.MainNavigation;
import navigation.NavigationType;
import ru.makdyff.globus.App;
import ru.makdyff.globus.R;

@SuppressLint("ValidFragment")
public class FirstFragment extends FragmentBase<NavigationType, MainNavigation> {
    public FirstFragment(MainNavigation mainNavigation) {
        super(mainNavigation);
    }

    public FirstFragment() {
        super(new MainNavigation(null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

//        App.SqlCon.delete(Valute.class);
//
//        Valute v = new Valute();
//        v.CharCode = "123";
//        v.Name = "Name";
//        v.Value = 1.333f;
//
//        App.SqlCon.addObject(Valute.class, v);
//
//        pool.submit(new Runnable() {
//            @Override
//            public void run() {
//                String vvv = "";
//
//                try {
//                    vvv = App.Disp.request();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
