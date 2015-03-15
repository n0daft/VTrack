package ch.mobop.mse.vtrack.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.adapters.viewholders.ForMeItemViewHolder;
import ch.mobop.mse.vtrack.adapters.viewholders.VoucherItemViewHolder;
import ch.mobop.mse.vtrack.model.VoucherForMe;

/**
 * Created by n0daft on 05.03.2015.
 */
public class ForMeRecyclerViewAdapter extends VoucherRecyclerViewAdapter{

    @Override
    public ForMeItemViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.recyclerview_item, container, false);

        return new ForMeItemViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(VoucherItemViewHolder itemHolder, int position) {
        VoucherForMe item = (VoucherForMe) mItems.get(position);

        itemHolder.setTxtVoucherName(item.getName());
        itemHolder.setTxtPerson(item.getReceivedBy());
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yy").withLocale(Locale.GERMAN);
        itemHolder.setTxtDate(format.print(item.getDateOfexpiration()));

        int color;
        switch (item.getValidityStatus()){
            case valid:
                color = Color.parseColor("#FF96AA39"); // pastel green
                break;
            case soonToExpire:
                color = Color.parseColor("#FFF4842D"); // pastel orange
                break;
            case expired:
                color = Color.parseColor("#FFC74B46"); // pastel red
                break;
            default:
                color = Color.GRAY;
                break;
        }

        itemHolder.setFlValidityStatusColor(color);
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