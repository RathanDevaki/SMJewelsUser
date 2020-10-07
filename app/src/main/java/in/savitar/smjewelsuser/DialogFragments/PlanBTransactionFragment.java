package in.savitar.smjewelsuser.DialogFragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.razorpay.Checkout;

import org.json.JSONObject;

import in.savitar.smjewelsuser.MainActivity;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentAmountPayableBinding;
import in.savitar.smjewelsuser.databinding.FragmentPlanBTransactionBinding;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardContract;
import in.savitar.smjewelsuser.mvp.ui.Dashboard.DashboardPresenter;


public class PlanBTransactionFragment extends DialogFragment implements DashboardContract.View {

    FragmentPlanBTransactionBinding mBinding;
    DashboardPresenter mPresenter;

    int amount = 0;



    public PlanBTransactionFragment() {
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
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_plan_b_transaction,container,false);
        mPresenter = new DashboardPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {



        mBinding.amountPayableEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {

                    amount = Integer.parseInt(charSequence.toString());

                    mBinding.planAmountSummary.setText("Rs." + charSequence.toString() + "/-");
                    mBinding.internetChargesSummary.setText("Rs."+getInternetChargesAmount(Integer.parseInt(charSequence.toString()))+ "/-");
                    mBinding.totalAmountSummary.setText("Rs."+getTotalAmount(Integer.parseInt(charSequence.toString()))+ "/-");

                    ( (MainActivity)getActivity()).amount = amount;

                } catch (Exception e) {
                    dismiss();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.continuePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentGateway(Integer.parseInt(getTotalAmount(amount)));
            }
        });



    }




    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }

    private void openPaymentGateway(int amount) {
        dismiss();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final Activity activity = getActivity();
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "SM Jewels");
            options.put("description", "Grateful to have you as our customer");
            //You can omit the image option to fetch the image from dashboard
            options.put("currency", "INR");
            options.put("amount", amount * 100);
            JSONObject preFill = new JSONObject();
            preFill.put("contact", mAuth.getCurrentUser().getPhoneNumber());
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getTotalAmount(int amount) {
        int amountPerc = (amount*2) / 100;
        return String.valueOf(amountPerc + amount);
    }

    private String getInternetChargesAmount(int amount) {
        int amountPerc = (amount*2) / 100;
        return String.valueOf(amountPerc);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}