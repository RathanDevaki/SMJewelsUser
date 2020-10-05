package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import in.savitar.smjewelsuser.Adapters.PlanWinnerAdapter;
import in.savitar.smjewelsuser.Modal.DrawWinnerModal;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentPlanWinnersBinding;


public class PlanWinnersFragment extends Fragment implements DashboardContract.View {

    FragmentPlanWinnersBinding mBinding;
    DashboardPresenter mPresenter;



    public PlanWinnersFragment() {
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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_plan_winners,container,false);
        mPresenter = new DashboardPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {

        //Initialize Ad Data
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.planWinnersAdBanner.loadAd(adRequest);

        fetchPlanWinners();

    }

    private void fetchPlanWinners() {

        List<DrawWinnerModal> list  = new ArrayList<>();
        final PlanWinnerAdapter adapter = new PlanWinnerAdapter(getContext(),R.layout.single_plan_winners,list);
        mBinding.planWinnerScrollingList.setAdapter(adapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("PlanA");
        databaseReference.child("DrawWinners").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DrawWinnerModal modal = snapshot.getValue(DrawWinnerModal.class);
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