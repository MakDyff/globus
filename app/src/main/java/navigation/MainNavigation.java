package navigation;

import fragments.FirstFragment;
import fragments.FragmentBase;
import ru.makdyff.globus.MainActivity;
import ru.makdyff.globus.R;

public class MainNavigation extends NavigationBase<NavigationType> {

    public MainNavigation(MainActivity mainActivity){
        super(mainActivity, R.id.activity_main_container, NavigationType.First);
    }

    protected FragmentBase<NavigationType, NavigationBase<NavigationType>> getFragment(NavigationType type) {
        FragmentBase fragment = new FragmentBase<NavigationType, MainNavigation>(this);

        if(type == NavigationType.First) {
            fragment = new FirstFragment(this);
        }

        return fragment;
    }

    @Override
    protected void onLastFragment() {
        getActivity().finish();
    }
}
