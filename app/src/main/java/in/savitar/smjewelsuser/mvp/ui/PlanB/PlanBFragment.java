package in.savitar.smjewelsuser.mvp.ui.PlanB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.Adapters.TransactionsAdapter;
import in.savitar.smjewelsuser.DialogFragments.AmountPayableFragment;
import in.savitar.smjewelsuser.DialogFragments.PlanBTransactionFragment;
import in.savitar.smjewelsuser.Modal.TransactionsModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentPlanBBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;


public class PlanBFragment extends Fragment implements DashboardContract.View {

    FragmentPlanBBinding mBinding;
    DashboardPresenter mPresenter;


    public PlanBFragment() {
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

        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_plan_b,container,false);
        mPresenter = new DashboardPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {

        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.planBAdBanner.loadAd(adRequest);


        getBasicData();

        mBinding.userPhotoDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toUserProfile();
            }
        });

        mBinding.payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentSummaryDialog();
            }
        });


    }

    private void showPaymentSummaryDialog() {
        PlanBTransactionFragment dialogFragment = new PlanBTransactionFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev =getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "dialog");
    }

    private void getBasicData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String planName = preferences.getString("Plan","");
        String userKey = preferences.getString("UserKey","");
        final String userID = preferences.getString("UserID","");

        Toasty.success(getContext(),planName).show();

        mBinding.userIdDashboard.setText(userID);

        databaseReference.child(planName).child("UsersList").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mBinding.userNameDashboard.setText("Hi," + snapshot.child("Name").getValue(String.class));

                if (snapshot.hasChild("ProfilePhoto")){
                    Glide
                            .with(getContext())
                            .load(snapshot.child("ProfilePhoto").getValue(String.class))
                            .into(mBinding.userPhotoDashboard);
                } else {
                    Glide
                            .with(getContext())
                            .load("https://firebasestorage.googleapis.com/v0/b/sm-jewels.appspot.com/o/img_162044.png?alt=media&token=c5445416-61d0-4ea7-90e7-a77a5e65cd09")
                            .into(mBinding.userPhotoDashboard);
                }

                if (snapshot.hasChild("Transactions")){
                    mBinding.totalTransactions.setText(String.valueOf(snapshot.child("Transactions").getChildrenCount()));

                    int totalAmount = 0;

                    for (DataSnapshot ds:snapshot.child("Transactions").getChildren()){

                        String amount = ds.child("Amount").getValue(String.class);
                        int _amount = Integer.parseInt(amount);
                        totalAmount = totalAmount+_amount;

                    }

                    mBinding.totalPaidAmount.setText("Rs." + totalAmount + "/-");

                }

                if (snapshot.hasChild("GoldSaved")){
                    mBinding.goldSaved.setText(String.format("%.2f",snapshot.child("GoldSaved").getValue(Double.class)) + "g");
                }else {
                    mBinding.goldSaved.setText("0g");
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final List<TransactionsModal> list = new ArrayList<>();
        final TransactionsAdapter adapter = new TransactionsAdapter(getContext(),R.layout.single_transaction_layout,list);
        mBinding.transactionsList.setAdapter(adapter);

        databaseReference.child(planName).child("UsersList").child(userKey).child("Transactions")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        TransactionsModal modal = snapshot.getValue(TransactionsModal.class);
                        list.add(modal);
                        Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        databaseReference.child(planName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mBinding.goldRate.setText("Rs." + String.valueOf(snapshot.child("GoldRate").getValue(Long.class)) + "/g");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}