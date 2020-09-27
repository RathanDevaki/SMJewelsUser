package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import in.savitar.smjewelsuser.mvp.ui.splash.SplashActivity;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashContract;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;

public class DashboardPresenter implements DashboardContract.Presenter {

    DashboardContract.View view;
    FirebaseAuth mAuth;

    public DashboardPresenter(DashboardContract.View view) {
        this.view = view;
    }

    @Override
    public void signOut(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        clearPreferences(activity);
        Intent i  = new Intent(activity, SplashActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    private void clearPreferences(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }
}
