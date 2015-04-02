package ch.mobop.mse.vtrack.adapters.viewholders;

import android.view.View;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Created by n0daft on 12.03.2015.
 */
public class FromMeItemViewHolder extends VoucherItemViewHolder {

    private View itemView;

    public FromMeItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter){
        super(itemView, adapter);
        itemView.setOnClickListener(this);

        this.itemView = itemView;
    }

    public void setTxtPerson(CharSequence person) {
        txtPerson.setText(itemView.getResources().getText(R.string.general_for) + " " + person);
    }

}
