package in.savitar.smjewelsuser.mvp.ui.splash;

import android.content.Context;

public interface SplashContract {

    interface View {
        void onSuccess(String message);
        void onFailure(String message);
    }

    interface Presenter{
        boolean checkInternet(Context context);
        boolean checkLogin();
        void showDialog(Context context);
        void hideDialog();

    }



}
