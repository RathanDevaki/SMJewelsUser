package in.savitar.smjewelsuser.mvp.ui.PlanB;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.Adapters.TransactionsAdapter;
import in.savitar.smjewelsuser.DialogFragments.PlanBTransactionFragment;
import in.savitar.smjewelsuser.Modal.TransactionsModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentPlanBBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;


public class PlanBFragment extends Fragment implements DashboardContract.View, AdapterView.OnItemSelectedListener {

    FragmentPlanBBinding mBinding;
    DashboardPresenter mPresenter;
    long totalAmount;
    Double goldRate;
    int flag=0;
    int dataflag=0;
    String planMonths;
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

        mBinding.loadingLayout.setVisibility(View.VISIBLE);
        mBinding.cardView.setVisibility(View.GONE);
        mBinding.planSpinner.setOnItemSelectedListener(PlanBFragment.this);
        //Creating the ArrayAdapter instance having the country list
        String[] plans = {"Please Select","12", "18", "24","36"};
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, plans);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        mBinding.planSpinner.setAdapter(aa);


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

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        final DatabaseReference databaseReferenceRoot = firebaseDatabase.getReference();
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String planName = preferences.getString("Plan","");
        final String userKey = preferences.getString("UserKey","");
        final String userID = preferences.getString("UserID","");

        Log.v("Plan name",planName);
        Log.v("userId",userID);
        Log.v("userkey",userKey);
       // Toasty.success(getContext(),planName).show();

        mBinding.userIdDashboard.setText(userID);
        Log.v("hasOut","has");
      /*  final DatabaseReference databaseReference3 = firebaseDatabase.getReference().child("PlanB").child("GoldRate");
        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.hasChild(planName)){
                   Log.v("has","has");
                   goldRate=snapshot.getValue(Double.class);
                   totalAmount= snapshot.child("UsersList").child(userKey).child("TotalAmount").getValue(Long.class);

               }
               else{
                   Log.v("hasNo","has");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       }); */


        databaseReference.child(planName).child("UsersList").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {

                if(snapshot.hasChild("InstallmentPeriod"))
                {
                    int instPeriod=snapshot.child("InstallmentPeriod").getValue(Integer.class);
                    if(instPeriod == 0)
                    {
                        mBinding.accountSummary.setVisibility(View.GONE);
                        mBinding.cardView.setVisibility(View.GONE);
                        mBinding.loadingLayout.setVisibility(View.GONE);
                        mBinding.yourTransaction.setVisibility(View.GONE);
                        mBinding.transactionsList.setVisibility(View.GONE);
                        mBinding.spinnerLayout.setVisibility(View.VISIBLE);
//spinner part
                       planMonths= (String) mBinding.planSpinner.getSelectedItem();

                        mBinding.btnAddInstallment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mBinding.progressBar.setVisibility(View.VISIBLE);
                                mBinding.btnAddInstallment.setVisibility(View.GONE);
                                if (mBinding.planSpinner.getSelectedItem().toString().compareToIgnoreCase("Please Select") == 0)
                                {
                                    Toasty.error(getContext(),"Please select any plan",Toasty.LENGTH_LONG).show();
                                    mBinding.progressBar.setVisibility(View.GONE);
                                    mBinding.btnAddInstallment.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    String pl= (String) mBinding.planSpinner.getSelectedItem();
                                    Log.v("Installment amnt",pl);
                                    int plInt=Integer.parseInt(pl);

                                    databaseReference.child(planName).child("UsersList").child(userKey).child("InstallmentPeriod").setValue(plInt);

                                    Toasty.success(getContext(), pl + " Month plan has selected", Toasty.LENGTH_LONG).show();
                                    mBinding.spinnerLayout.setVisibility(View.VISIBLE);
                                    dataflag=1;
                                    mBinding.accountSummary.setVisibility(View.VISIBLE);
                                    mBinding.cardView.setVisibility(View.VISIBLE);
                                    mBinding.transactionsList.setVisibility(View.VISIBLE);
                                    mBinding.yourTransaction.setVisibility(View.VISIBLE);
                                    mBinding.spinnerLayout.setVisibility(View.GONE);
                                    mBinding.progressBar.setVisibility(View.GONE);
                                    mBinding.btnAddInstallment.setVisibility(View.VISIBLE);

                                }

                            }

                        });

//end spinner
                    }
                    else{
                        flag=1;
                    }
                }
                //from here if
                //if(flag==1) {
                    mBinding.loadingLayout.setVisibility(View.GONE);
                    mBinding.cardView.setVisibility(View.VISIBLE);
                    mBinding.userNameDashboard.setText("Hi," + snapshot.child("Name").getValue(String.class));

                    if (snapshot.hasChild("ProfilePhoto")) {
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
                    //Checking for completion
                    if (snapshot.hasChild("InstallmentPeriod")) {
                        long completedMonths = snapshot.child("CompletedMonths").getValue(Long.class);
                        long instPeriod = snapshot.child("InstallmentPeriod").getValue(Long.class);
                        if (instPeriod == 0) {
                            databaseReference.child(planName).child("UsersList").child(userKey).child("PlanCompletionStatus").setValue("NA");
                            databaseReferenceRoot.child("PlanCompletionStatus").child(userID).setValue("NA");
                            mBinding.cardView.setVisibility(View.GONE);
                            // mBinding.userWarning.setVisibility(View.VISIBLE);
                        }
                        else if (instPeriod == completedMonths && instPeriod != 0) {
                            databaseReference.child(planName).child("UsersList").child(userKey).child("PlanCompletionStatus").setValue("Completed");
                            databaseReferenceRoot.child("PlanCompletionStatus").child(userID).setValue("Completed");
                            mBinding.loadingLayout.setVisibility(View.GONE);
                            mBinding.completeLayout.setVisibility(View.VISIBLE);
                            mBinding.cardView.setVisibility(View.GONE);
                            // mBinding.summaryText.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.completeAnimation.setVisibility(View.GONE);
                                    mBinding.completeText.setVisibility(View.VISIBLE);
                                    // Toast.makeText(getContext(),"Completed",Toast.LENGTH_LONG).show();
                                }
                            }, 3000);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.cardView.setVisibility(View.VISIBLE);
                                    mBinding.completeText.setVisibility(View.VISIBLE);
                                    // Toast.makeText(getContext(),"Completed",Toast.LENGTH_LONG).show();
                                }
                            }, 3000);

                        }
                        else{
                            Log.v("flag=0","flg0");
                        }

                    }

                    if (snapshot.hasChild("Transactions")) {
                        mBinding.totalTransactions.setText(String.valueOf(snapshot.child("Transactions").getChildrenCount()));
                    }
                    // int totalAmount = 0;
                      /*for (DataSnapshot ds:snapshot.child("Transactions").getChildren()){

                        String amount = ds.child("Amount").getValue(String.class);

                        int _amount = Integer.parseInt(amount);
                        totalAmount = totalAmount+_amount;*/

                    if (snapshot.hasChild("TotalAmount")) {
                        totalAmount = snapshot.child("TotalAmount").getValue(Long.class);

                        mBinding.totalPaidAmount.setText("Rs." + totalAmount + "/-");
                    }

                    if (snapshot.hasChild("GoldSaved")) {
                        Log.v("goldrate", String.valueOf(goldRate));
                        Log.v("tot amt", String.valueOf(totalAmount));
                        // Double grams = Double.parseDouble(String.valueOf(totalAmount / goldRate));
                        mBinding.goldSaved.setText(String.format("%.2f", snapshot.child("GoldSaved").getValue(Double.class)) + "g");
                    } else {
                        mBinding.goldSaved.setText("0g");
                    }
                    //to here
              //  }
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
        Log.v("planName",planName);
        databaseReference.child(planName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double goldrate1=(snapshot.child("GoldRate").getValue(Double.class));
                Log.v("Todaygoldrate", String.valueOf(goldrate1));
                mBinding.goldRate.setText("Rs." + goldrate1+ "/g");
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}