package in.savitar.smjewelsuser.mvp.ui.splash;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;

import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentSplashBinding;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;

public class SplashFragment extends Fragment implements SplashContract.View {

    FragmentSplashBinding mBinding;
    SplashPresenter mPresenter;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_splash,container,false);
        mPresenter = new SplashPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter.checkInternet(getContext())){// Checking of internet connection

                    if (mPresenter.checkLogin()) { //Already user is logged in
                        NavigationUtil.INSTANCE.toMainActivity();
                    } else { //No User is logged In
                        NavigationUtil.INSTANCE.toLogin();
                    }

                } else{ //No Internet connection
                    NavigationUtil.INSTANCE.toNoInternet();
                }
            }
        },3000);

    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}