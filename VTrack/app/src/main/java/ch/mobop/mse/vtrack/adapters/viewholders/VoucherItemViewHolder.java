package ch.mobop.mse.vtrack.adapters.viewholders;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Custom item view holder which servers as a super class for
 * the specific recycler view view holders.
 * Created by n0daft on 12.03.2015.
 */
public class VoucherItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /** Placeholder for voucher name */
    private TextView mTxtVoucherName;

    /** Placeholder for person info */
    protected TextView mTxtPerson;

    /** Placeholder for date */
    private TextView mTxtDate;

    /** Placeholder for validity status icon */
    private FrameLayout mFlValidityStatus;

    /** Placeholder for archive status icon */
    private ImageView mIvArchiveStatusTrue;
    private ImageView mIvArchiveStatusFalse;

    private VoucherRecyclerViewAdapter mAdapter;

    public VoucherItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter) {
        super(itemView);
        itemView.setOnClickListener(this);

        mAdapter = adapter;

        mTxtVoucherName = (TextView) itemView.findViewById(R.id.txtVoucherName);
        mTxtPerson = (TextView) itemView.findViewById(R.id.txtPerson);
        mTxtDate = (TextView) itemView.findViewById(R.id.txtDate);
        mFlValidityStatus = (FrameLayout) itemView.findViewById(R.id.flValidityStatus);
        mIvArchiveStatusTrue = (ImageView) itemView.findViewById(R.id.ivValidityArchiveStatusTrue);
        mIvArchiveStatusFalse = (ImageView) itemView.findViewById(R.id.ivValidityArchiveStatusFalse);
    }

    @Override
    public void onClick(View v) {
        mAdapter.onItemHolderClick(this);
    }


    public void setmTxtDate(CharSequence date) {
        mTxtDate.setText(date);
    }

    public void setmTxtPerson(CharSequence person) {
        mTxtPerson.setText(person);
    }

    public void setmTxtVoucherName(CharSequence name) {
        mTxtVoucherName.setText(name);
    }

    public void setFlValidityStatusColor(int color){
        GradientDrawable d = (GradientDrawable) mFlValidityStatus.getBackground().mutate();
        d.setColor(color);
        d.invalidateSelf();
    }

    public void setmFlValidityStatus(int status){
        mFlValidityStatus.setVisibility(status);
    }

    public void setivArchiveStatusTrue(int status){
        mIvArchiveStatusTrue.setVisibility(status);
    }

    public void setivArchiveStatusFalse(int status){
        mIvArchiveStatusFalse.setVisibility(status);
    }
}
