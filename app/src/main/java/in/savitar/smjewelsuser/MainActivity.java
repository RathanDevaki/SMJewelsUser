package in.savitar.smjewelsuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import in.savitar.smjewelsuser.databinding.ActivityMainBinding;
import in.savitar.smjewelsuser.databinding.ActivitySplashBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;

public class MainActivity extends AppCompatActivity implements DashboardContract.View {

    DashboardContract.Presenter mPresenter;
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mPresenter = new DashboardPresenter( this);
        init();
        mBinding.getRoot();
    }

    private void init() {
        setupNavigator();
        NavigationUtilMain.INSTANCE.setUpDashboard();
    }

    protected void setupNavigator() {
        NavigationUtilMain.INSTANCE.setupNavigator(this, getSupportActionBar(), getmBinding(), null);
    }

    public ActivityMainBinding getmBinding(){
        return (ActivityMainBinding) mBinding;
    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}