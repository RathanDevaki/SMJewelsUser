package in.savitar.smjewelsuser.mvp.utils;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;

import in.savitar.smjewelsuser.databinding.ActivityMainBinding;
import in.savitar.smjewelsuser.databinding.ActivitySplashBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardFragment;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.PlanWinnersFragment;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.UserProfileFragment;
import in.savitar.smjewelsuser.mvp.ui.PlanB.PlanBFragment;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashFragment;

public enum NavigationUtilMain {

    @SuppressLint("StaticFieldLeak")
    INSTANCE;

    private FragmentManager mFragMngr;
    private ViewDataBinding mBinding;
    private AppCompatActivity mActivity;
    private static WeakReference<AppCompatActivity> actRef = new WeakReference<>(null);

    public AppCompatActivity getActivity() {
        final AppCompatActivity activity = actRef.get();
        return activity;
    }

    public void setupNavigator(AppCompatActivity activity,
                               ActionBar actionBar,
                               ViewDataBinding binding,
                               ActionBarDrawerToggle actionBarDrawerToggle) {
        actRef = new WeakReference<>(activity);
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity fragmentActivity = activity;
            mFragMngr = fragmentActivity.getSupportFragmentManager();
        }
        mActivity = activity;
        this.mBinding = binding;
    }

    private void addFragment(Fragment fragment,
                             String tag,
                             boolean addToBackStack) {
        if (mFragMngr == null) {
            return;
        }
        try {
            FragmentTransaction transaction = mFragMngr.beginTransaction();
            //transaction.setCustomAnimations(R.anim.animation_right_in, R.anim.animation_left_out, R.anim.animation_right_in, R.anim.animation_left_out);

            if (mBinding instanceof ActivityMainBinding)
                transaction.replace(((ActivityMainBinding) mBinding).mainContainer.getId(), fragment, tag);
            if (addToBackStack)
                transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            // Logger.e(Logger.TAG, e.getMessage());
        }
    }

    public void setUpDashboard() {
        addFragment(new DashboardFragment(), DashboardFragment.class.getSimpleName(),false);
    }

    public void toPlanB() {
        addFragment(new PlanBFragment(), PlanBFragment.class.getSimpleName(),false);
    }


    public void toUserProfile() {
        addFragment(new UserProfileFragment(), UserProfileFragment.class.getSimpleName(),true);
    }

    public void toPlanWinners() {
        addFragment(new PlanWinnersFragment(), PlanWinnersFragment.class.getSimpleName(),true);
    }

}
