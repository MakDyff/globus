package navigation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import fragments.FragmentBase;
import ru.makdyff.globus.FragmentActivityBase;

public class NavigationBase<T extends Enum<T>> {
    private T _currentType;
    private RelativeLayout _fragments;
    private FragmentBase _fragment;
    private FragmentActivityBase _fragmentActivityBase;
    private int _viewContainerId;
    private ArrayList<T> _stackList;
    protected ArrayList<T> _baseTypes;
    protected ArrayList<T> _penultTypes;

    protected FragmentActivityBase getActivity() {
        return _fragmentActivityBase;
    }

    public NavigationBase(FragmentActivityBase actBase, int container, T baseType) {
        _fragmentActivityBase = actBase;
        _viewContainerId = container;
        _currentType = baseType;
        _stackList = new ArrayList<>();
        _baseTypes = new ArrayList<>();
        _penultTypes = new ArrayList<>();
        _baseTypes.add(baseType);
    }

    private void addFragment(FragmentBase<T, NavigationBase<T>> fragment, String name) {
        FragmentTransaction tran = _fragmentActivityBase.getFragmentManager().beginTransaction();

        int count = _fragmentActivityBase.getFragmentManager().getBackStackEntryCount();
        for(int i = 0; i < count; i++) {
            FragmentManager.BackStackEntry mmm = _fragmentActivityBase.getFragmentManager().getBackStackEntryAt(i);
            Fragment fr = _fragmentActivityBase.getFragmentManager().findFragmentById(mmm.getId());
        }
        // изменил на замену(вроде как не нужен теперь popBack)
        tran.replace(_viewContainerId, fragment, name);
        tran.addToBackStack(null).commitAllowingStateLoss();
    }

    private void backFragment(String name) {
        if (_fragments == null)
            _fragments = (RelativeLayout)_fragmentActivityBase.findViewById(_viewContainerId);

        _fragmentActivityBase.getFragmentManager().popBackStack();
    }

    public void start() {
        if(!_stackList.contains(_currentType)) {
            _stackList.add(_currentType);
            fragmentChange(_currentType);
        }
    }

    public synchronized void nextClick(T type) {
            if (_currentType == type)
                return;

            _currentType = type;
            if (_baseTypes.contains(_currentType))
                _stackList.clear();
            else if(_penultTypes.contains(_currentType)) {
                T first = _stackList.get(0);
                _stackList.clear();
                _stackList.add(first);
            }

            _stackList.add(type);
            fragmentChange(type);
    }

    public synchronized void backClick() {
            if (_stackList.size() > 1) {
                int index = _stackList.size() - 1;
                _stackList.remove(index);
                index = index -1;
                _currentType = _stackList.get(index);
                fragmentChange(_currentType);
            } else
                onLastFragment();
    }

    /**
     * Событие "последний элемент"
     */
    protected void onLastFragment() {
    }

    private void fragmentChange(T type) {
        backFragment("");
        _fragment = getFragment(type);
        addFragment(_fragment, type.name());
    }

    protected FragmentBase<T, NavigationBase<T>> getFragment(T type) {
        return new FragmentBase(this);
    }
}
