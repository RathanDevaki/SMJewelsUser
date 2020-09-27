package in.savitar.smjewelsuser.mvp.ui.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import es.dmoral.toasty.Toasty;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentCreateAccountBinding;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashContract;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;

import static android.app.Activity.RESULT_OK;

public class CreateAccountFragment extends Fragment implements SplashContract.View, AdapterView.OnItemSelectedListener {

    SplashContract.Presenter mPresenter;
    FragmentCreateAccountBinding mBinding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String imageUrl;
    Uri imageUri;
    public static final int IMAGE_CODE=1;
    StorageReference storageReference;
    private BottomSheetBehavior sheetBehavior;


    public CreateAccountFragment() {
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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_account,container,false);
        mPresenter = new SplashPresenter(this);
        init();
        return mBinding.getRoot();

    }

    private void init() {

        //Setting up of plan Spinner
        mBinding.planSpinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        String[] plans = {"Plan A", "Plan B","Plan C"};
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,plans);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        mBinding.planSpinner.setAdapter(aa);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UsersList");

        //OnClick listener for Plan Info Button
        sheetBehavior = BottomSheetBehavior.from(mBinding.planInfoSheet.planInfoBottomSheet);
        mBinding.planInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mBinding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });

        mBinding.userPhotoCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null && data.getData() !=null) {
            imageUri = data.getData();
            mBinding.userPhotoCreateAccount.setImageURI(imageUri);
        }

    }

    private void signUpUser() {

        if (TextUtils.isEmpty(mBinding.userNameSignUp.getText())){
            mBinding.userNameSignUp.setError("Enter Name");
            Toasty.success(getContext(),"Enter Name").show();
        } else if (TextUtils.isEmpty(mBinding.userPhoneSignUp.getText()) || mBinding.userPhoneSignUp.getText().length() < 10){
            mBinding.userPhoneSignUp.setError("Invalid Number");
            Toasty.success(getContext(),"Invalid Number").show();
        }else if (TextUtils.isEmpty(mBinding.userPANSignUp.getText()) || mBinding.userPANSignUp.getText().length() < 10){
            mBinding.userPANSignUp.setError("Invalid Pan Number");
            Toasty.success(getContext(),"Invalid Pan Number").show();
        }else if (TextUtils.isEmpty(mBinding.userAadharSignUp.getText()) || mBinding.userAadharSignUp.getText().length() < 12){
            mBinding.userAadharSignUp.setError("Invalid Aadhar Number");
            Toasty.success(getContext(),"Invalid Aadhar Number").show();
        } else if (TextUtils.isEmpty(mBinding.userEmailSignUp.getText())){
            mBinding.userEmailSignUp.setError("Enter Email");
            Toasty.success(getContext(),"Enter Email").show();
        }else if (TextUtils.isEmpty(mBinding.userAddressSignUp.getText())){
            mBinding.userAddressSignUp.setError("Enter Address");
            Toasty.success(getContext(),"Enter Address").show();
        } else if (TextUtils.isEmpty(mBinding.userPincodeSignUp.getText()) || mBinding.userPincodeSignUp.getText().length() < 6){
            mBinding.userPincodeSignUp.setError("Invalid Pincode");
            Toasty.success(getContext(),"Invalid Pincode").show();
        }else if (TextUtils.isEmpty(mBinding.userDOBSignUp.getText())){
            mBinding.userDOBSignUp.setError("Enter DOB");
            Toasty.success(getContext(),"Enter DOB").show();
        } else {
            uploadData();
            Toasty.success(getContext(),"Created Successfully").show();
            NavigationUtil.INSTANCE.toMainActivity();
        }

    }

    private void uploadData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

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