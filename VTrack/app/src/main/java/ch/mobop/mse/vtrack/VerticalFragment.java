package ch.mobop.mse.vtrack;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by n0daft on 05.03.2015.
 */
public class VerticalFragment extends AbstractRecyclerviewFragment {

    public static VerticalFragment newInstance() {
        VerticalFragment fragment = new VerticalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        //We must draw dividers ourselves if we want them in a list
        return new DividerDecoration(getActivity());
    }

    @Override
    protected int getDefaultItemCount() {
        return 100;
    }
}