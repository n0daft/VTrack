package ch.mobop.mse.vtrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasQuery.Criteria;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.FromMeRecyclerViewAdapter;
import ch.mobop.mse.vtrack.decorators.DividerDecoration;
import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Created by n0daft on 12.03.2015.
 */
public class FromMeRecyclerViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private RecyclerView recyclerView;
    private FromMeRecyclerViewAdapter adapter;
    private ArrayList<Voucher> voucherFromMeList;
    private Criteria filter;
    private ProgressDialog mDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestToken mRefresh;
    private ArchiveListenerFromMe listener;

    // Listener to communicate with Archive Fragment.
    public interface ArchiveListenerFromMe {
        public void voucherArchived();
    }


    public void setArchiveListener(ArchiveListenerFromMe listener) {
        this.listener = listener;
    }


    public static FromMeRecyclerViewFragment newInstance() {
        FromMeRecyclerViewFragment fragment = new FromMeRecyclerViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);

        adapter = new FromMeRecyclerViewAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        mDialog = new ProgressDialog(this.getActivity());
        mDialog.setMessage(getString(R.string.dialog_refreshing));

        // Load all Items from Server with a filter and set list.
        filter = BaasQuery.builder().orderBy("dateOfexpiration").where("type='from_me' and archive='false'").criteria();
        if(voucherFromMeList == null) {
            voucherFromMeList = new ArrayList<>();
            refreshDocuments(true);
        }else{
            adapter.setItemList(voucherFromMeList);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDocuments(false);
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println("FromMe Resume");
        //Due to anonymous tab fragments we reload the data not here
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mDialog.isShowing()){
            mDialog.dismiss();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRefresh!=null){
            mRefresh.suspendAndSave(outState,Constants.REFRESH_TOKEN_KEY);
        }
    }


    /*
     *  Create an intent to start the detail view of a voucher.
     *  Passing data via parcable voucher object.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(),DetailVoucherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("voucherParcelable", voucherFromMeList.get(position));
        intent.putExtras(bundle);
        intent.putExtra("type","from_me");
        startActivityForResult(intent, Constants.DETAIL_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== Constants.DETAIL_CODE){
            if (resultCode==DetailVoucherActivity.RESULT_OK){
                if(!mDialog.isShowing())mDialog.show();
                refreshDocuments(true);
            }
            if (resultCode==Constants.RESULT_ARCHIVED){
                // Refresh Archive Fragment and this.
                refreshDocuments(true);
                if (null != listener) {listener.voucherArchived();}
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /*
     *  Download all vouchers with FromMe filter and refresh the archive list.
     *  Use onRefresh Handler as Callback method.
     */
    public void refreshDocuments(boolean setSpinner){
        if (getUserVisibleHint() && setSpinner){
            if(!mDialog.isShowing())mDialog.show();
        }
        mRefresh = BaasDocument.fetchAll(Constants.COLLECTION_NAME, filter, onRefresh);
    }

    private final BaasHandler<List<BaasDocument>>
            onRefresh = new BaasHandler<List<BaasDocument>>() {
        @Override
        public void handle(BaasResult<List<BaasDocument>> result) {

            mRefresh=null;
            if(mDialog.isShowing())mDialog.dismiss();

            try {

                Iterator it = result.get().iterator();
                voucherFromMeList.clear();

                while(it.hasNext()){
                    BaasDocument doc = (BaasDocument) it.next();
                    DateTime redeemedAt = null;
                    DateTime dateOfDelivery = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("dateOfDelivery"));
                    DateTime  dateOfExpiration = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("dateOfexpiration"));
                    String name = doc.getString("name");
                    String notes = doc.getString("notes");
                    String givenTo = doc.getString("givenTo");
                    String redeemedWhere = doc.getString("redeemedWhere");
                    String id = doc.getId();

                    voucherFromMeList.add(new VoucherFromMe(name,givenTo,dateOfDelivery,dateOfExpiration,redeemedWhere,notes,redeemedAt,id));
                }

                mSwipeRefreshLayout.setRefreshing(false);
                adapter.setItemList(voucherFromMeList);

            }catch (BaasInvalidSessionException e){
                startLoginScreen();
            }catch (BaasException e){
                Log.e("LOGERR", "Error " + e.getMessage(), e);
                Toast.makeText(getActivity(),getString(R.string.toast_no_connection),Toast.LENGTH_LONG).show();
            }
        }
    };

    private void startLoginScreen(){
        Intent intent = new Intent(this.getActivity(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
