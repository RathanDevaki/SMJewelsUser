package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.savitar.smjewelsuser.Adapters.TransactionsAdapter;
import in.savitar.smjewelsuser.Modal.TransactionsModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentDashboardBinding;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtilMain;


public class DashboardFragment extends Fragment implements DashboardContract.View{


    DashboardPresenter mPresenter;
    FragmentDashboardBinding mBinding;

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

    }

    private void fetchBasicData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String planName = preferences.getString("Plan","");
        String userKey = preferences.getString("UserKey","");
        String userID = preferences.getString("UserID","");
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

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}