package ch.mobop.mse.vtrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ch.mobop.mse.vtrack.adapters.RecyclerViewDemoAdapter;

/**
 * Created by n0daft on 01.03.2015.
 */
public abstract class ReceivedVouchersFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String ARG_POSITION = "position";

    private RecyclerView mList;
    private RecyclerViewDemoAdapter mAdapter;

    protected abstract RecyclerView.LayoutManager getLayoutManager();
    protected abstract RecyclerView.ItemDecoration getItemDecoration();
    protected abstract int getDefaultItemCount();

    private int position;

    /*
    public static ReceivedVouchersFragment newInstance(int position) {
        ReceivedVouchersFragment f = new ReceivedVouchersFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_received, container, false);

        mList = (RecyclerView) rootView.findViewById(R.id.section_list);
        mList.setLayoutManager(getLayoutManager());
        mList.addItemDecoration(getItemDecoration());

        mList.getItemAnimator().setAddDuration(1000);
        mList.getItemAnimator().setChangeDuration(1000);
        mList.getItemAnimator().setMoveDuration(1000);
        mList.getItemAnimator().setRemoveDuration(1000);

        mAdapter = new RecyclerViewDemoAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mAdapter.setOnItemClickListener(this);
        mList.setAdapter(mAdapter);

        return rootView;
       // return fl;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
