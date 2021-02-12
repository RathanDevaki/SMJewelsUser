package in.savitar.smjewelsuser.mvp.ui.PlanC;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentPlanCBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;

public class PlanCFragment extends DialogFragment implements DashboardContract.View {

    FragmentPlanCBinding mBinding;
    DashboardPresenter mPresenter;

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
        AdRequest adRequest=new AdRequest.Builder().build();
        mBinding.planCAdBanner.loadAd(adRequest);


    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}