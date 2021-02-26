package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import in.savitar.smjewelsuser.Adapters.TransactionsAdapter;
import in.savitar.smjewelsuser.DialogFragments.AmountPayableFragment;
import in.savitar.smjewelsuser.Modal.TransactionsModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentDashboardBinding;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;


public class DashboardFragment extends Fragment implements DashboardContract.View{


    DashboardPresenter mPresenter;
    FragmentDashboardBinding mBinding;
    String dueDate;
    long planAmount;
    String lpm="";
    public DashboardFragment() {
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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard,container,false);
        mPresenter = new DashboardPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {

        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.dashboardAdBanner.loadAd(adRequest);

        mBinding.userPhotoDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toUserProfile();
            }
        });

        fetchBasicData(); //Fetches username and profile photo in dashboard

        /* mBinding.payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentSummaryDialog((int)planAmount);
            }
        }); */

        fetchDrawWinner();

        mBinding.planWinnerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toPlanWinners();
            }
        });

        mBinding.planWinnerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toPlanWinners();
            }
        });

    }

    private void fetchDrawWinner() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("PlanA").child("DrawWinners");
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mBinding.planWinnerName.setText(snapshot.child("Winner1").getValue(String.class));
                Glide
                        .with(getContext())
                        .load(snapshot.child("Photo1").getValue(String.class))
                        .into(mBinding.planWinnerPhoto);

                //Winner 2
                mBinding.planWinnerNameTwo.setText(snapshot.child("Winner2").getValue(String.class));
                Glide
                        .with(getContext())
                        .load(snapshot.child("Photo2").getValue(String.class))
                        .into(mBinding.planWinnerPhotoTwo);
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

    private void showPaymentSummaryDialog(int i) {
        AmountPayableFragment dialogFragment = new AmountPayableFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putInt("Amount",i);
        dialogFragment.setArguments(args);
        Fragment prev =getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "dialog");
    }

    private void fetchBasicData() {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String planName = preferences.getString("Plan","");
        String userKey = preferences.getString("UserKey","");
        final String userID = preferences.getString("UserID","");

        if (planName.compareToIgnoreCase("PlanA") == 0) { //If Plan is A
            String setName = preferences.getString("SetName","");

            databaseReference.child(planName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dueDate = snapshot.child("DueDate").getValue(String.class);
                    planAmount = snapshot.child("PlanAmount").getValue(Long.class);
                    mBinding.upcomingAmount.setText("Rs." + snapshot.child("PlanAmount").getValue(Long.class) + "/-");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); //Getting Due Date and plan Amount


            databaseReference.child(planName).child("UsersList").child(setName).child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mBinding.userNameDashboard.setText("Hi," + snapshot.child("Name").getValue(String.class));

                    if (getActivity() == null) {
                        return;
                    }

                    if (snapshot.hasChild("ProfilePhoto")){
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


                    mBinding.planNameProgress.setText(planName);
                    mBinding.cardView.setVisibility(View.GONE);
                    mBinding.summaryText.setVisibility(View.GONE);
                    if (snapshot.hasChild("TotalMonths")){
                        long totalMonths = snapshot.child("TotalMonths").getValue(Long.class);
                        mBinding.cardView.setVisibility(View.VISIBLE);
                        mBinding.summaryText.setVisibility(View.VISIBLE);
                        long completedMonths = snapshot.child("CompletedMonths").getValue(Long.class);
                        long percentageCompleted = (completedMonths*100)/totalMonths;

                        int _completedPercentage = (int)percentageCompleted;
                        if(completedMonths ==  totalMonths)
                        {
                            mBinding.loadingLayout.setVisibility(View.GONE);
                            mBinding.completeLayout.setVisibility(View.VISIBLE);
                            mBinding.cardView.setVisibility(View.VISIBLE);
                            mBinding.summaryText.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run()
                                {
                                   mBinding.completeAnimation.setVisibility(View.GONE);
                                   mBinding.completeText.setVisibility(View.VISIBLE);
                                  // Toast.makeText(getContext(),"Completed",Toast.LENGTH_LONG).show();
                                }
                            }, 3000);
                        }
                        else
                        {
                            mBinding.loadingLayout.setVisibility(View.GONE);
                            mBinding.completeLayout.setVisibility(View.GONE);
                            mBinding.cardView.setVisibility(View.VISIBLE);
                            mBinding.summaryText.setVisibility(View.VISIBLE);
                        }

                        mBinding.progressPercentage.setText(String.valueOf(_completedPercentage) + "% Completed");
                        mBinding.planProgress.setProgress(_completedPercentage);

                        //mBinding.lastLoginDashboard.setText("Last login on "+snapshot.child("LastLogin").getValue(String.class));
                        mBinding.userIdDashboard.setText(userID);
                        mBinding.progressPercentageMonths.setText(String.valueOf(completedMonths) + "/" + String.valueOf(totalMonths) + " Months");
                        mBinding.totalTransactions.setText(String.valueOf(completedMonths));
                        mBinding.totalPaidAmount.setText("Rs." + String.valueOf(completedMonths * 500) + "/-");

                        if (snapshot.hasChild("LastPaidMonth")){

                            getNextPayingDate(snapshot.child("LastPaidMonth").getValue(String.class), snapshot.child("TotalTransactions").getValue(Long.class));
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            final List<TransactionsModal> list = new ArrayList<>();
            final TransactionsAdapter adapter = new TransactionsAdapter(getContext(),R.layout.single_transaction_layout,list);
            mBinding.transactionsList.setAdapter(adapter);

            databaseReference.child(planName).child("UsersList").child(setName).child(userKey).child("Transactions")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            TransactionsModal modal = snapshot.getValue(TransactionsModal.class);
                            Log.v("Modal",String.valueOf(modal));
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

        mBinding.upcomingPaymentDate.setText(nextDate);
        Log.v("Date", nextDate);

    }


    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}