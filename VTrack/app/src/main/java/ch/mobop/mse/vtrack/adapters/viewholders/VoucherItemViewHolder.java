package ch.mobop.mse.vtrack.adapters.viewholders;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Created by n0daft on 12.03.2015.
 */
public class VoucherItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /** Placeholder for voucher name */
    private TextView txtVoucherName;

    /** Placeholder for person info */
    protected TextView txtPerson;

    /** Placeholder for date */
    private TextView txtDate;

    /** Placeholder for validity status icon */
    private FrameLayout flValidityStatus;

    private VoucherRecyclerViewAdapter mAdapter;

    public VoucherItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter) {
        super(itemView);
        itemView.setOnClickListener(this);

        mAdapter = adapter;

        txtVoucherName = (TextView) itemView.findViewById(R.id.txtVoucherName);
        txtPerson = (TextView) itemView.findViewById(R.id.txtPerson);
        txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        flValidityStatus = (FrameLayout) itemView.findViewById(R.id.flValidityStatus);
    }

    @Override
    public void onClick(View v) {
        //mAdapter.onItemHolderClick(this);
    }


    public void setTxtDate(CharSequence date) {
        txtDate.setText(date);
    }

    public void setTxtPerson(CharSequence person) {
        txtPerson.setText(person);
    }

    public void setTxtVoucherName(CharSequence name) {
        txtVoucherName.setText(name);
    }

    public void setFlValidityStatusColor(int color){
        GradientDrawable d = (GradientDrawable) flValidityStatus.getBackground().mutate();
        d.setColor(color);
        d.invalidateSelf();
    }
}
