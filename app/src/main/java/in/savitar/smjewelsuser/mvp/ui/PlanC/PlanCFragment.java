package in.savitar.smjewelsuser.mvp.ui.PlanC;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.Adapters.TransactionsAdapter;
import in.savitar.smjewelsuser.Modal.TransactionsModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentPlanCBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;

public class PlanCFragment extends DialogFragment implements DashboardContract.View, AdapterView.OnItemSelectedListener {

    FragmentPlanCBinding mBinding;
    DashboardPresenter mPresenter;
    long dueDate;
    long planAmount = 0;
    String planMonths;
    int dataflag = 0;
    int flag = 0;
    int instPeriod = 0;

    public PlanCFragment() {
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
        //return inflater.inflate(R.layout.fragment_plan_c, container, false);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_c, container, false);
        mPresenter = new DashboardPresenter(this);
        init();
        return mBinding.getRoot();
    }

    public void init() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.planCAdBanner.loadAd(adRequest);
        mBinding.loadingLayout.setVisibility(View.VISIBLE);
        mBinding.cardView.setVisibility(View.GONE);
        mBinding.planSpinner.setOnItemSelectedListener(PlanCFragment.this);
        //Creating the ArrayAdapter instance having the country list
        String[] plans = {"Please Select", "1000", "2000", "5000"};
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, plans);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        mBinding.planSpinner.setAdapter(aa);

        fetchBasicData();
        mBinding.userPhotoDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toUserProfile();
            }
        });

    }

    private void fetchBasicData() {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String planName = preferences.getString("Plan", "");
        final String userKey = preferences.getString("UserKey", "");
        final String userID = preferences.getString("UserID", "");


        if (planName.compareToIgnoreCase("PlanC") == 0) { //If Plan is A
            String setName = preferences.getString("SetName", "");

            databaseReference.child(planName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dueDate = snapshot.child("DueDate").getValue(Long.class);
                    planAmount = snapshot.child("UsersList").child(userKey).child("InstallmentAmount").getValue(Long.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); //Getting Due Date and plan Amount


            databaseReference.child(planName).child("UsersList").child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mBinding.userNameDashboard.setText("Hi," + snapshot.child("Name").getValue(String.class));
                    mBinding.userIdDashboard.setText(userID);
                    if (getActivity() == null) {
                        return;
                    }

                    if (snapshot.hasChild("ProfilePhoto")) {
                        Glide
                                .with(getActivity())
                                .load(snapshot.child("ProfilePhoto").getValue(String.class))
                                .into(mBinding.userPhotoDashboard);
                    } else {
                        Glide
                                .with(getActivity())
                                .load("https://firebasestorage.googleapis.com/v0/b/sm-jewels.appspot.com/o/img_162044.png?alt=media&token=c5445416-61d0-4ea7-90e7-a77a5e65cd09")
                                .into(mBinding.userPhotoDashboard);
                    }
                    if (snapshot.hasChild("InstallmentAmount")) {
                        instPeriod = snapshot.child("InstallmentAmount").getValue(Integer.class);
                        if (instPeriod == 0) {
                           mBinding.summaryText.setVisibility(View.GONE);
                            mBinding.cardView.setVisibility(View.GONE);
                            mBinding.loadingLayout.setVisibility(View.GONE);
                            mBinding.transactionsList.setVisibility(View.GONE);
                            mBinding.transactionsList.setVisibility(View.GONE);
                            mBinding.spinnerLayout.setVisibility(View.VISIBLE);
//spinner part
                            planMonths = (String) mBinding.planSpinner.getSelectedItem();

                            mBinding.btnAddInstallment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mBinding.progressBar.setVisibility(View.VISIBLE);
                                    mBinding.btnAddInstallment.setVisibility(View.GONE);
                                    if (mBinding.planSpinner.getSelectedItem().toString().compareToIgnoreCase("Please Select") == 0) {
                                        Toasty.error(getContext(), "Please select any Amount", Toasty.LENGTH_LONG).show();
                                        mBinding.progressBar.setVisibility(View.GONE);
                                        mBinding.btnAddInstallment.setVisibility(View.VISIBLE);
                                    } else {
                                        String pl = (String) mBinding.planSpinner.getSelectedItem();
                                        Log.v("Installment amnt", pl);
                                        int plInt = Integer.parseInt(pl);

                                        databaseReference.child(planName).child("UsersList").child(userKey).child("InstallmentAmount").setValue(plInt);

                                        Toasty.success(getContext(), pl + " Plan has selected", Toasty.LENGTH_LONG).show();
                                        dataflag = 1;
                                        mBinding.spinnerLayout.setVisibility(View.GONE);
                                        mBinding.cardView.setVisibility(View.VISIBLE);
                                        mBinding.yourPlan.setVisibility(View.VISIBLE);
                                        mBinding.progressLayout.setVisibility(View.VISIBLE);
                                        mBinding.transactionsList.setVisibility(View.VISIBLE);
                                        mBinding.progressBar.setVisibility(View.GONE);
                                        mBinding.btnAddInstallment.setVisibility(View.VISIBLE);

                                    }

                                }

                            });

//end spinner
                        } else {
                            flag = 1;
                        }
                    }


                    if (flag == 1) {

                       mBinding.yourPlan.setVisibility(View.VISIBLE);
                        mBinding.yourTransaction.setVisibility(View.VISIBLE);
                        mBinding.progressLayout.setVisibility(View.VISIBLE);
                        mBinding.summaryText.setVisibility(View.VISIBLE);

                        mBinding.planNameProgress.setText(planName);
                        mBinding.cardView.setVisibility(View.GONE);
                          mBinding.summaryText.setVisibility(View.GONE);
                        if (snapshot.hasChild("InstallmentPeriod")) {
                            long totalMonths = snapshot.child("InstallmentPeriod").getValue(Long.class);
                            mBinding.cardView.setVisibility(View.VISIBLE);
                             mBinding.summaryText.setVisibility(View.VISIBLE);
                            long completedMonths = snapshot.child("CompletedMonths").getValue(Long.class);
                            long percentageCompleted = (completedMonths * 100) / totalMonths;
                            long totalAmount = snapshot.child("TotalAmount").getValue(Long.class);
                            int _completedPercentage = (int) percentageCompleted;
                            long netAmount = instPeriod + totalAmount;
                            if (completedMonths == totalMonths) {
                                mBinding.loadingLayout.setVisibility(View.GONE);
                                mBinding.completeLayout.setVisibility(View.VISIBLE);
                                mBinding.completeText.setText("Your plan has been completed.\n \n Your total savings is Rs." + netAmount + "/-");
                                mBinding.cardView.setVisibility(View.VISIBLE);
                                mBinding.summaryText.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.completeAnimation.setVisibility(View.GONE);
                                        mBinding.completeText.setVisibility(View.VISIBLE);
                                        // Toast.makeText(getContext(),"Completed",Toast.LENGTH_LONG).show();
                                    }
                                }, 3000);
                            } else {
                                mBinding.loadingLayout.setVisibility(View.GONE);
                                mBinding.completeLayout.setVisibility(View.GONE);
                                mBinding.cardView.setVisibility(View.VISIBLE);
                                  mBinding.summaryText.setVisibility(View.VISIBLE);
                            }

                            mBinding.progressPercentage.setText(String.valueOf(_completedPercentage) + "% Completed");
                            mBinding.planProgress.setProgress(_completedPercentage);

                            //mBinding.lastLoginDashboard.setText("Last login on "+snapshot.child("LastLogin").getValue(String.class));
                            long TotalAmount = snapshot.child("TotalAmount").getValue(Long.class);
                            mBinding.progressPercentageMonths.setText(String.valueOf(completedMonths) + "/" + String.valueOf(totalMonths) + " Months");
                            mBinding.totalTransactions.setText(String.valueOf(completedMonths));
                            mBinding.totalPaidAmount.setText("Rs." + String.valueOf(TotalAmount) + "/-");
                            mBinding.upcomingAmount.setText("Rs." + snapshot.child("InstallmentAmount").getValue(Long.class) + "/-");

                            if (snapshot.hasChild("LastPaidMonth")) {

                                // lpm=snapshot.child("LastPaidMonth").getValue(String.class);

                                getNextPayingDate(snapshot.child("LastPaidMonth").getValue(String.class), snapshot.child("TotalTransactions").getValue(Long.class));
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            final List<TransactionsModal> list = new ArrayList<>();
            final TransactionsAdapter adapter = new TransactionsAdapter(getContext(), R.layout.single_transaction_layout, list);
            mBinding.transactionsList.setAdapter(adapter);

            databaseReference.child(planName).child("UsersList").child(setName).child(userKey).child("Transactions")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            TransactionsModal modal = snapshot.getValue(TransactionsModal.class);
                            Log.v("Modal", String.valueOf(modal));
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
        }


    }

    private void getNextPayingDate(String lastPaidMonth, Long totTrans) {
        //int _totTrans=Integer.parseInt(totTrans);

        Log.v("DateFire", lastPaidMonth);
        String[] _lastPaidMonth = lastPaidMonth.split("/");
        String _day = _lastPaidMonth[0];
        String _month = _lastPaidMonth[1];
        String _year = _lastPaidMonth[2];
        Log.v("Seperated date", _day + "" + _month + "" + _year);

        int intdate = 12;
        int intmonth = Integer.parseInt(_month);
        int intyear = Integer.parseInt(_year);
        if (intmonth == 12) {
            intyear++;
            intmonth = 1;
        } else {
            intmonth++;
        }
        String nextDate = intdate + "/" + intmonth + "/" + intyear;

       /*String[] _lpm=lpm.split("/");
       if(_lpm[1] == _month)
       {
           mBinding.upcomingPaymentDate.setText("-");
       }*/
        if (totTrans == 0) {
            mBinding.upcomingPaymentDate.setText("Pay Now");
        } else {
            mBinding.upcomingPaymentDate.setText(nextDate);
            Log.v("Date", nextDate);
        }

    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}