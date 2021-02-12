package in.savitar.smjewelsuser;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;

public class FcmRegistrationManager {

    private static final String TAG = "FcmRegistrationManager";

    /*This is async call*/
    public void registerWithFcm(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                       // Log.d(TAG, msg);
                        sendRegistrationToServer(token);

                    }
                });

    }

    private void sendRegistrationToServer(String token) {
        // send token to web service ??
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/IDs");
        // then store your token ID
        ref.push().setValue(token);
    }

}