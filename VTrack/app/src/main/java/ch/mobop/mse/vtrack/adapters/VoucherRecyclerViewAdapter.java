package ch.mobop.mse.vtrack.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.viewholders.VoucherItemViewHolder;
import ch.mobop.mse.vtrack.model.Voucher;

/**
 * Created by n0daft on 12.03.2015.
 */
public abstract class VoucherRecyclerViewAdapter extends RecyclerView.Adapter<VoucherItemViewHolder> {

    protected ArrayList<Voucher> mItems;
    protected AdapterView.OnItemClickListener mOnItemClickListener;

    public VoucherRecyclerViewAdapter() {
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
    public int getItemCount() {
        return mItems.size();
    }



}
