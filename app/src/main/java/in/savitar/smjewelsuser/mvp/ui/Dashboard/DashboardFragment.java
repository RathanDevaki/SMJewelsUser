package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

        mBinding.userPhotoDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtilMain.INSTANCE.toUserProfile();
            }
        });

        fetchBasicData(); //Fetches username and profile photo in dashboard

        mBinding.payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentSummaryDialog((int)planAmount);
            }
        });

        fetchDrawWinner();

    }

    private void fetchDrawWinner() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("PlanA").child("DrawWinners");
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mBinding.planWinnerName.setText(snapshot.child("Name").getValue(String.class));
                Glide
                        .with(getContext())
                        .load(snapshot.child("Photo").getValue(String.class))
                        .into(mBinding.planWinnerPhoto);
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
//        HashMap<String,String> drawMap = new HashMap<>();
//        drawMap.put("Name","Shrivathsa J V");
//        drawMap.put("Month","August 2020");
//        drawMap.put("UserID","202008002");
//        drawMap.put("Photo","https://firebasestorage.googleapis.com/v0/b/sm-jewels.appspot.com/o/ProfilePictures%2FD8dDZukXUAAXLdY.jpg?alt=media&token=8e289afe-4fdf-4e04-b39d-b96b4ac0ad6a");
//        databaseReference.child("DrawWinners").push().setValue(drawMap);



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
        databaseReference.child(planName).child("UsersList").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mBinding.userNameDashboard.setText("Hi," + snapshot.child("Name").getValue(String.class));
                Glide
                        .with(getContext())
                        .load(snapshot.child("ProfilePhoto").getValue(String.class))
                        .into(mBinding.userPhotoDashboard);

                mBinding.planNameProgress.setText(planName);
                long totalMonths = snapshot.child("TotalMonths").getValue(Long.class);
                long completedMonths = snapshot.child("CompletedMonths").getValue(Long.class);
                long percentageCompleted = (completedMonths*100)/totalMonths;
                int _completedPercentage = (int)percentageCompleted;
                mBinding.progressPercentage.setText(String.valueOf(_completedPercentage) + "% Completed");
                mBinding.planProgress.setProgress(_completedPercentage);

                mBinding.lastLoginDashboard.setText("Last login on "+snapshot.child("LastLogin").getValue(String.class));
                mBinding.userIdDashboard.setText(userID);
                mBinding.progressPercentageMonths.setText(String.valueOf(completedMonths) + "/" + String.valueOf(totalMonths) + " Months");
                mBinding.totalTransactions.setText(String.valueOf(completedMonths));
                mBinding.totalPaidAmount.setText("Rs." + String.valueOf(completedMonths * 500) + "/-");

                getNextPayingDate(snapshot.child("LastPaidMonth").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        List<TransactionsModal> list = new ArrayList<>();
        final TransactionsAdapter adapter = new TransactionsAdapter(getContext(),R.layout.single_transaction_layout,list);
        mBinding.transactionsList.setAdapter(adapter);

        databaseReference.child(planName).child("UsersList").child(userKey).child("Transactions")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        TransactionsModal modal = snapshot.getValue(TransactionsModal.class);
                        adapter.add(modal);
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

    private void getNextPayingDate(String lastPaidMonth) {

        String[] _lastPaidMonth = lastPaidMonth.split("-");
        String _day = _lastPaidMonth[0];
        String _month = _lastPaidMonth[1];
        String _year = _lastPaidMonth[2];

        Calendar currentMonth = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        currentMonth.add(Calendar.MONTH, 1);
         String nextDate =  dateFormat.format(currentMonth.getTime()) + "-" +  _year;
         mBinding.upcomingPaymentDate.setText(nextDate);

    }


    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}