package ch.mobop.mse.vtrack.adapters.viewholders;

import android.view.View;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Item view holder for the recycler view of the "voucher for me" activity.
 * Created by n0daft on 12.03.2015.
 */
public class ForMeItemViewHolder extends VoucherItemViewHolder {

    private View itemView;

    public ForMeItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter){
        super(itemView, adapter);
        itemView.setOnClickListener(this);

        this.itemView = itemView;
    }

    public void setmTxtPerson(CharSequence person) {
        mTxtPerson.setText(itemView.getResources().getText(R.string.general_from) + " " + person);
    }
}
