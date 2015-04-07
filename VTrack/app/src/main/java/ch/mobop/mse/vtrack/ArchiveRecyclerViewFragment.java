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
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.ArchiveRecyclerViewAdapter;
import ch.mobop.mse.vtrack.decorators.DividerDecoration;
import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherForMe;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Methods for the Archive Fragment.
 */
public class ArchiveRecyclerViewFragment extends Fragment implements AdapterView.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private ArchiveRecyclerViewAdapter mAdapter;
    private ArrayList<Voucher> mVoucherList;
    private BaasQuery.Criteria mFilter;
    private ProgressDialog mDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestToken mRefresh;

    /**
     * Initiates a new ArchiveRecyclerViewFragment object.
     * @return The created ArchiveRecyclerViewFragment object.
     */
    public static ArchiveRecyclerViewFragment newInstance() {
        ArchiveRecyclerViewFragment fragment = new ArchiveRecyclerViewFragment();
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.section_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        mRecyclerView.getItemAnimator().setAddDuration(1000);
        mRecyclerView.getItemAnimator().setChangeDuration(1000);
        mRecyclerView.getItemAnimator().setMoveDuration(1000);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);

        mAdapter = new ArchiveRecyclerViewAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mDialog = new ProgressDialog(this.getActivity());
        mDialog.setMessage(getString(R.string.dialog_refreshing));

        // Load all Items from Server only if there are no Objects saved.
        mFilter = BaasQuery.builder().orderBy("redeemedAt desc").where("archive='true'").criteria();
        if(mVoucherList == null) {
            mVoucherList = new ArrayList<>();
            refreshDocuments(true);
        }else{
            mAdapter.setItemList(mVoucherList);
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
        // Due to anonymous tab fragments we reload the data not here.
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
            mRefresh.suspendAndSave(outState, Constants.REFRESH_TOKEN_KEY);
        }
    }


    /**
     *  Create an intent to start the detail view of a voucher.
     *  Passing data via parcable voucher object.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(),DetailVoucherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("voucherParcelable", mVoucherList.get(position));
        intent.putExtras(bundle);

        if (mVoucherList.get(position) instanceof VoucherForMe){
            intent.putExtra("type","for_me");
        }else{
            intent.putExtra("type","from_me");
        }
        intent.putExtra("archive","true");
        startActivityForResult(intent, Constants.DETAIL_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== Constants.DETAIL_CODE){
            if (resultCode==DetailVoucherActivity.RESULT_OK){
                // If voucher gets deleted reload the data.
                if(!mDialog.isShowing())mDialog.show();
                refreshDocuments(true);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     *  Download all vouchers with archive filter and refresh the archive list.    
     *  Use onRefresh Handler as Callback method.
     */                                                           
    public void refreshDocuments(boolean setSpinner){
        if (getUserVisibleHint() && setSpinner){
            if(!mDialog.isShowing())mDialog.show();
        }
        mRefresh = BaasDocument.fetchAll(Constants.COLLECTION_NAME, mFilter, onRefresh);
    }

    private final BaasHandler<List<BaasDocument>>
            onRefresh = new BaasHandler<List<BaasDocument>>() {
        @Override
        public void handle(BaasResult<List<BaasDocument>> result) {

            if(mDialog.isShowing())mDialog.dismiss();
            mRefresh=null;

            try {
                Iterator it = result.get().iterator();

                mVoucherList.clear();

                while(it.hasNext()){
                    BaasDocument doc = (BaasDocument) it.next();
                    DateTime redeemedAt = null;
                    if(!doc.getString("redeemedAt").equals("")){
                        redeemedAt = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("redeemedAt"));
                    }

                    DateTime  dateOfexpiration = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("dateOfexpiration"));
                    String name = doc.getString("name");
                    String notes = doc.getString("notes");
                    String redeemedWhere = doc.getString("redeemedWhere");
                    String id = doc.getId();

                    if(doc.getString("type").equals("for_me")){
                        DateTime dateOfReceipt = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("dateOfReceipt"));
                        String receivedBy = doc.getString("receivedBy");
                        VoucherForMe voucher = new VoucherForMe(name,receivedBy,dateOfReceipt,dateOfexpiration,redeemedWhere,notes,redeemedAt,id);
                        voucher.setRedeemed(Boolean.valueOf(doc.getString("redeemed")));
                        mVoucherList.add(voucher);
                    }else{
                        DateTime dateOfDelivery = Config.dateTimeFormatterBaas.parseDateTime(doc.getString("dateOfDelivery"));
                        String givenTo = doc.getString("givenTo");
                        VoucherFromMe voucher = new VoucherFromMe(name,givenTo,dateOfDelivery,dateOfexpiration,redeemedWhere,notes,redeemedAt,id);
                        voucher.setRedeemed(Boolean.valueOf(doc.getString("redeemed")));
                        mVoucherList.add(voucher);
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setItemList(mVoucherList);

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
