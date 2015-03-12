package ch.mobop.mse.vtrack.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Created by n0daft on 12.03.2015.
 */
public class FromMeRecyclerviewAdapter extends RecyclerView.Adapter<FromMeRecyclerviewAdapter.FromMeItemHolder>{


    private ArrayList<Voucher> mItems;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public FromMeRecyclerviewAdapter() {
        mItems = new ArrayList<>();
    }

    public void setItemList(List<Voucher> items) {
        mItems.clear();
        mItems.addAll(items);
        System.out.println("setItemList aufgerufen");

        notifyDataSetChanged();
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemInserted(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void addItem(int position, Voucher item) {
        if (position >= mItems.size()) return;

        mItems.add(position, item);
        notifyItemInserted(position);
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemRemoved(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void removeItem(int position) {
        if (position >= mItems.size()) return;

        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public FromMeItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.recyclerview_item, container, false);

        return new FromMeItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(FromMeItemHolder itemHolder, int position) {
        VoucherFromMe item = (VoucherFromMe) mItems.get(position);

        itemHolder.setVoucherName(item.getName());
        itemHolder.setGivenTo(item.getGivenTo());
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yy").withLocale(Locale.GERMAN);
        itemHolder.setDateOfExpiration(format.print(item.getDateOfexpiration()));

        int color;
        switch (item.getValidityStatus()){
            case valid:
                color = Color.GREEN;
                break;
            case soonToExpire:
                color = Color.YELLOW;
                break;
            case expired:
                color = Color.RED;
                break;
            default:
                color = Color.GRAY;
                break;
        }

        itemHolder.setFlValidityStatusColor(color);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(FromMeItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getPosition(), itemHolder.getItemId());
        }
    }

    public static class FromMeItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtVoucherName, txtReceivedBy, txtDateOfExpiration;
        private FrameLayout flValidityStatus;

        private FromMeRecyclerviewAdapter mAdapter;

        public FromMeItemHolder(View itemView, FromMeRecyclerviewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);

            mAdapter = adapter;

            txtVoucherName = (TextView) itemView.findViewById(R.id.txtVoucherName);
            txtReceivedBy = (TextView) itemView.findViewById(R.id.txtReceivedFrom);
            txtDateOfExpiration = (TextView) itemView.findViewById(R.id.txtDateOfExpiration);
            flValidityStatus = (FrameLayout) itemView.findViewById(R.id.flValidityStatus);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }

        public void setVoucherName(CharSequence voucherName) {
            txtVoucherName.setText(voucherName);
        }

        public void setGivenTo(CharSequence givenTo) {
            txtReceivedBy.setText(givenTo);
        }

        public void setDateOfExpiration(CharSequence dateOfExpiration) {
            txtDateOfExpiration.setText(dateOfExpiration);
        }

        public void setFlValidityStatusColor(int color){

            GradientDrawable d = (GradientDrawable) flValidityStatus.getBackground().mutate();
            d.setColor(color);
            d.invalidateSelf();
            //Drawable mDrawable = Drawable(R.drawable.circle);
            //mDrawable.setColorFilter(new PorterDuffColorFilter(0xffff00, PorterDuff.Mode.MULTIPLY));
        }
    }
}
