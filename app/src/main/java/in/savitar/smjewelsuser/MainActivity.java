package in.savitar.smjewelsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.PaymentResultListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.databinding.ActivityMainBinding;
import in.savitar.smjewelsuser.databinding.ActivitySplashBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;

public class MainActivity extends AppCompatActivity implements DashboardContract.View, PaymentResultListener {

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

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            storeLastVisitTime();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void storeLastVisitTime() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(sharedPreferences.getString("Plan",""));
        databaseReference.child("UsersList").child("Set1").child(sharedPreferences.getString("UserKey","")).child("LastLogin")
                .setValue(getCurrentTimeAndDate());

    }

    private String  getCurrentTimeAndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy, HH:mm aa", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toasty.success(this,"Payment Successfull").show();
        updateTransactionInfo();

    }

    private void updateTransactionInfo() {

        final SharedPreferences preferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child(preferences.getString("Plan",""));

        HashMap<String,String> transactionMap = new HashMap<>();
        transactionMap.put("Amount","500");
        transactionMap.put("Comments","Paid");
        transactionMap.put("Date",getCurrentTimeAndDate());

        databaseReference.child("UsersList").child("Set1").child(preferences.getString("UserKey",""))
                .child("Transactions").push().setValue(transactionMap);

        databaseReference.child("UsersList").child("Set1").child(preferences.getString("UserKey",""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long _completedMonths = snapshot.child("CompletedMonths").getValue(Long.class);
                        _completedMonths = _completedMonths+1;
                        databaseReference.child("UsersList").child("Set1").child(preferences.getString("UserKey",""))
                                .child("CompletedMonths").setValue(_completedMonths);
                        NavigationUtilMain.INSTANCE.setUpDashboard();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toasty.success(this,"Payment Successfull").show();
    }
}