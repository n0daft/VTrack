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
        System.out.println("setItemCOUNT aufgerufen");

        notifyDataSetChanged();
    }

    public void setItemList(List<VoucherForMe> items) {
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
        View root = inflater.inflate(R.layout.recyclerview_item, container, false);

        return new VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
        VoucherForMe item = mItems.get(position);

        itemHolder.setVoucherName(item.getName());
        itemHolder.setReceivedBy(item.getReceivedBy());
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yy").withLocale(Locale.GERMAN);
        itemHolder.setDateOfExpiration(format.print(item.getDateOfexpiration()));

        int color = Color.GRAY;
        switch (item.getValidityStatus()){
            case valid:
                color = Color.GREEN;
            case soonToExpire:
                color = Color.YELLOW;
            case expired:
                color = Color.RED;
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

    private void onItemHolderClick(VerticalItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getPosition(), itemHolder.getItemId());
        }
    }

    public static class VerticalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtVoucherName, txtReceivedBy, txtDateOfExpiration;
        private FrameLayout flValidityStatus;

        private RecyclerViewDemoAdapter mAdapter;

        public VerticalItemHolder(View itemView, RecyclerViewDemoAdapter adapter) {
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

        public void setReceivedBy(CharSequence receivedBy) {
            txtReceivedBy.setText(receivedBy);
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

    public static VoucherForMe generateDummyItem() {
        VoucherForMe v1 = new VoucherForMe(new DateTime(), new DateTime(), "Marco", null, "Bad Zurzach", "Thermalbab");
        return v1;
    }

    public static List<VoucherForMe> generateDummyData(int count) {
        ArrayList<VoucherForMe> items = new ArrayList<>();

        /*
        for (int i=0; i < count; i++) {
            items.add(new VoucherForMe(new DateTime(), new DateTime(), "Marco", "Test", "Thermalbad"));
        }
        */

        items.add(new VoucherForMe(new DateTime().minusMonths(8), new DateTime().plusDays(12), "Marco", null, "Bad Zurzach", "Thermalbab"));
        items.add(new VoucherForMe(new DateTime().minusMonths(10).minusDays(20), new DateTime().plusYears(1), "Marco", null, "Bad Zurzach", "Thermalbab"));
        items.add(new VoucherForMe(new DateTime().minusMonths(14), new DateTime().minusDays(23), "Marco", null, "Bad Zurzach", "Thermalbab"));

        return items;
    }
}