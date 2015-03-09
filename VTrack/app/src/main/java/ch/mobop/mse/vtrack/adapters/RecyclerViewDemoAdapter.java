package ch.mobop.mse.vtrack.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.mobop.mse.vtrack.R;
import ch.mobop.mse.vtrack.model.VoucherForMe;

/**
 * Created by n0daft on 05.03.2015.
 */
public class RecyclerViewDemoAdapter extends RecyclerView.Adapter<RecyclerViewDemoAdapter.VerticalItemHolder>{

    private ArrayList<VoucherForMe> mItems;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public RecyclerViewDemoAdapter() {
        mItems = new ArrayList<VoucherForMe>();
    }

    /*
     * A common adapter modification or reset mechanism. As with ListAdapter,
     * calling notifyDataSetChanged() will trigger the RecyclerView to update
     * the view. However, this method will not trigger any of the RecyclerView
     * animation features.
     */
    public void setItemCount(int count) {
        mItems.clear();
        mItems.addAll(generateDummyData(count));

        notifyDataSetChanged();
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemInserted(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void addItem(int position) {
        if (position >= mItems.size()) return;

        mItems.add(position, generateDummyItem());
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
    public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.view_match_item, container, false);

        return new VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
        VoucherForMe item = mItems.get(position);

        itemHolder.setVoucherName(item.getRedeemAt());
        itemHolder.setReceivedBy(item.getReceivedBy());
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yy").withLocale(Locale.GERMAN);
        itemHolder.setDateOfExpiration(format.print(item.getDateOfexpiration()));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(VerticalItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getPosition(), itemHolder.getItemId());
        }
    }

    public static class VerticalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtVoucherName;
        private TextView txtReceivedBy, txtDateOfExpiration;

        private RecyclerViewDemoAdapter mAdapter;

        public VerticalItemHolder(View itemView, RecyclerViewDemoAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);

            mAdapter = adapter;

            txtVoucherName = (TextView) itemView.findViewById(R.id.txtVoucherName);
            txtReceivedBy = (TextView) itemView.findViewById(R.id.txtReceivedFrom);
            txtDateOfExpiration = (TextView) itemView.findViewById(R.id.txtDateOfExpiration);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }

        public void setVoucherName(CharSequence voucherName) {
            txtVoucherName.setText(voucherName);
        }

        public void setReceivedBy(CharSequence receivedBy) {
            txtReceivedBy.setText(receivedBy);
        }

        public void setDateOfExpiration(CharSequence dateOfExpiration) {
            txtDateOfExpiration.setText(dateOfExpiration);
        }
    }

    public static VoucherForMe generateDummyItem() {
        VoucherForMe v1 = new VoucherForMe(new DateTime(), new DateTime(), "Marco", "Thermalbad");
        return v1;
    }

    public static List<VoucherForMe> generateDummyData(int count) {
        ArrayList<VoucherForMe> items = new ArrayList<>();

        for (int i=0; i < count; i++) {
            items.add(new VoucherForMe(new DateTime(), new DateTime(), "Marco", "Thermalbad"));
        }

        return items;
    }
}