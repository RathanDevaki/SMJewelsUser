package in.savitar.smjewelsuser.mvp.ui.splash;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentNoInternetBinding;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;


public class NoInternetFragment extends Fragment implements SplashContract.View {

    FragmentNoInternetBinding mBinding;
    SplashPresenter mPresenter;




    public NoInternetFragment() {
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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_no_internet,container,false);
        mPresenter = new SplashPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {

        mBinding.tryAgainInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter.checkInternet(getContext())){
                    NavigationUtil.INSTANCE.setSplash();
                }else {
                    onFailure("Try Again");
                }
            }
        });

    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {
        Toasty.error(getContext(),message).show();
    }
}