package in.savitar.smjewelsuser.mvp.utils;

import android.annotation.SuppressLint;
import android.content.Intent;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import in.savitar.smjewelsuser.MainActivity;
import in.savitar.smjewelsuser.databinding.ActivitySplashBinding;
import in.savitar.smjewelsuser.mvp.ui.Login.CreateAccountFragment;
import in.savitar.smjewelsuser.mvp.ui.Login.LoginFragment;
import in.savitar.smjewelsuser.mvp.ui.Login.StatusPendingFragment;
import in.savitar.smjewelsuser.mvp.ui.splash.NoInternetFragment;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashFragment;

public enum NavigationUtil {

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

            if (mBinding instanceof ActivitySplashBinding)
                transaction.replace(((ActivitySplashBinding) mBinding).splashContainer.getId(), fragment, tag);
            if (addToBackStack)
                transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            // Logger.e(Logger.TAG, e.getMessage());
        }
    }


    public void setSplash(){
        addFragment(new SplashFragment(), SplashFragment.class.getSimpleName(),false);
    }

    public void toLogin(){
        addFragment(new LoginFragment(),LoginFragment.class.getSimpleName(),true);
    }

    public void toNoInternet() {
        addFragment(new NoInternetFragment(),NoInternetFragment.class.getSimpleName(),false);
    }

    public void toCreateAccount() {
        addFragment(new CreateAccountFragment(),CreateAccountFragment.class.getSimpleName(),true);
    }

    public void toMainActivity(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    public void toPendingStatus(){
        addFragment(new StatusPendingFragment(),StatusPendingFragment.class.getSimpleName(),false);
    }

    public void toSplash(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(i);
        getActivity().finish();
        setSplash();
    }






}
