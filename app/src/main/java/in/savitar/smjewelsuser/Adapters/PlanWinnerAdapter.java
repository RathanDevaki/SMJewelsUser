package in.savitar.smjewelsuser.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.savitar.smjewelsuser.Modal.DrawWinnerModal;
import in.savitar.smjewelsuser.R;

public class PlanWinnerAdapter extends ArrayAdapter<DrawWinnerModal> {
    public PlanWinnerAdapter(@NonNull Context context, int resource, @NonNull List<DrawWinnerModal> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.single_plan_winners, parent, false);
        }

        DrawWinnerModal modal = getItem(position);

        TextView winner1 = convertView.findViewById(R.id.single_winner_one_name);
        TextView winner2 = convertView.findViewById(R.id.single_winner_two_name);
        TextView userID1 = convertView.findViewById(R.id.single_winner_one_id);
        TextView userID2 = convertView.findViewById(R.id.single_winner_two_id);
        TextView month = convertView.findViewById(R.id.single_winner_month);
        CircleImageView photo1 = convertView.findViewById(R.id.single_winner_one_photo);
        CircleImageView photo2 = convertView.findViewById(R.id.single_winner_two_photo);

        winner1.setText(modal.getWinner1());
        winner2.setText(modal.getWinner2());
        userID1.setText(modal.getUserID1());
        userID2.setText(modal.getUserID2());
        month.setText(modal.getMonth());


        Glide
                .with(getContext())
                .load(modal.getPhoto1())
                .into(photo1);

        Glide
                .with(getContext())
                .load(modal.getPhoto1())
                .into(photo2);


        return convertView;
    }
}
