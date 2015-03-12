package ch.mobop.mse.vtrack.adapters.viewholders;

import android.view.View;

import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Created by n0daft on 12.03.2015.
 */
public class ForMeItemViewHolder extends VoucherItemViewHolder {

    public ForMeItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter){
        super(itemView, adapter);
        itemView.setOnClickListener(this);
    }

    public void setTxtPerson(CharSequence person) {
        txtPerson.setText("from " + person);
    }
}
