package fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import navigation.NavigationBase;

@SuppressLint("ValidFragment")
public class FragmentBase<TEnum extends Enum<TEnum>, TNavigation extends NavigationBase<TEnum>> extends Fragment {
    private ProgressDialog _dialog;
    public TNavigation navigation;
    protected ExecutorService pool = Executors.newCachedThreadPool();

    public FragmentBase(TNavigation navigation) {
        this.navigation = navigation;
    }

    @Override
    public void onStart() {
        super.onStart();

        _dialog = new ProgressDialog(getActivity());
        _dialog.setCanceledOnTouchOutside(false);
    }

    protected void progressShow(String message) {
        if(message == null || message.length() == 0)
             message = "Ожидайте...";

        _dialog.setMessage(message);
        _dialog.show();
    }

    protected void progressDismiss() {
        _dialog.dismiss();
    }
}
