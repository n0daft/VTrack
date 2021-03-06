package ch.mobop.mse.vtrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.viewholders.ArchiveItemViewHolder;
import ch.mobop.mse.vtrack.adapters.viewholders.VoucherItemViewHolder;
import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherForMe;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Adapter for the recycler view of the "archive" activity.
 * Created by Simon on 24.03.2015.
 */
public class ArchiveRecyclerViewAdapter extends VoucherRecyclerViewAdapter{

    private View root;

    @Override
    public ArchiveItemViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        root = inflater.inflate(R.layout.recyclerview_item, container, false);

        return new ArchiveItemViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(VoucherItemViewHolder itemHolder, int position) {
        //General Information
        Voucher item = mItems.get(position);
        itemHolder.setmTxtVoucherName(item.getName());
        itemHolder.setmTxtDate(Config.dateTimeFormatter.print(item.getRedeemedAt()));

        itemHolder.setmFlValidityStatus(View.GONE);

        if(item.isRedeemed()){
            itemHolder.setivArchiveStatusFalse(View.GONE);
            itemHolder.setivArchiveStatusTrue(View.VISIBLE);
        }else{
            itemHolder.setivArchiveStatusTrue(View.GONE);
            itemHolder.setivArchiveStatusFalse(View.VISIBLE);
        }

        if(mItems.get(position) instanceof VoucherForMe){
            VoucherForMe itemForMe = (VoucherForMe) mItems.get(position);
            String tmp = root.getResources().getString(R.string.general_from) + " " + itemForMe.getReceivedBy();
            itemHolder.setmTxtPerson(tmp);
        }else{
            VoucherFromMe itemFromMe = (VoucherFromMe) mItems.get(position);
            String tmp = root.getResources().getString(R.string.general_for) + " " + itemFromMe.getGivenTo();
            itemHolder.setmTxtPerson(tmp);
        }

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(VoucherItemViewHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getPosition(), itemHolder.getItemId());
        }
    }
}
