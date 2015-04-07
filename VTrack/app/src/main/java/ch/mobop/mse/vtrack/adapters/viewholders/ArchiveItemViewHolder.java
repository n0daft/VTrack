package ch.mobop.mse.vtrack.adapters.viewholders;

import android.view.View;

import ch.mobop.mse.vtrack.adapters.VoucherRecyclerViewAdapter;

/**
 * Item view holder for the recycler view of the "archive" activity.
 * Created by Simon on 24.03.2015.
 */
public class ArchiveItemViewHolder extends VoucherItemViewHolder {

    public ArchiveItemViewHolder(View itemView, VoucherRecyclerViewAdapter adapter){
        super(itemView, adapter);
        itemView.setOnClickListener(this);
    }

}
