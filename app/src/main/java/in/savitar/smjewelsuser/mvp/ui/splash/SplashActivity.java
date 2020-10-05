package in.savitar.smjewelsuser.mvp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.ActivitySplashBinding;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    SplashContract.Presenter mPresenter;
    ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        mPresenter = new SplashPresenter(this);
        init();
        mBinding.getRoot();
    }
    private void init() {
        setupNavigator();
        NavigationUtil.INSTANCE.setSplash();
    }

    protected void setupNavigator()
    {
        NavigationUtil.INSTANCE.setupNavigator(this, getSupportActionBar(), getmBinding(), null);
    }

    public ActivitySplashBinding getmBinding(){
        return (ActivitySplashBinding) mBinding;
    }



    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}