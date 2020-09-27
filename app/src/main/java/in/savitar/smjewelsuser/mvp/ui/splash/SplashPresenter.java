package in.savitar.smjewelsuser.mvp.ui.splash;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class SplashPresenter implements SplashContract.Presenter {

    SplashContract.View view;
    FirebaseAuth mAuth;
    private String mVerificationId;
    Context _context;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    @Override
    public boolean checkInternet(Context context) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public boolean checkLogin() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //User Logged in Already
            return true;
        } else{
            //No User is logged in
            return false;
        }
    }

    @Override
    public void showDialog(Context context) {

    }

    @Override
    public void hideDialog() {

    }



}
