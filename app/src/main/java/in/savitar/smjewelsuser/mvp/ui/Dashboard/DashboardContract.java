package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.app.Activity;
import android.content.Context;

public interface DashboardContract {

    interface View{
        void onSuccess(String message);
        void onFailure(String message);
    }

    interface Presenter{
        void signOut(Activity activity);
    }



}
