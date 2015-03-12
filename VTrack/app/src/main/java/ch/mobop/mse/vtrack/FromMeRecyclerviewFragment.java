package ch.mobop.mse.vtrack;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasQuery.Criteria;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.FromMeRecyclerViewAdapter;
import ch.mobop.mse.vtrack.decorators.DividerDecoration;
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

    private RequestToken mRefresh;

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
    public void onResume() {
        super.onResume();
        //Reload Data after a Voucher was added or deleted
        System.out.println("onResume()");
        refreshDocuments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_received, container, false);

        voucherFromMeList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);

        adapter = new FromMeRecyclerViewAdapter();

        //adapter.setItemCount(getDefaultItemCount());

        //Load all Items from Server
        filter = BaasQuery.builder().orderBy("dateOfexpiration").where("type='from_me' and archive='false'").criteria();

        refreshDocuments();
        //Doesn't really do something as refresh is not done yet....
        adapter.setItemList(voucherFromMeList);


        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),
                "Clicked: " + position + ", index " + recyclerView.indexOfChild(view),
                Toast.LENGTH_SHORT).show();
        System.out.println("onClick!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    public void refreshDocuments(){
        mRefresh = BaasDocument.fetchAll("vtrack", filter, onRefresh);
    }

    private final BaasHandler<List<BaasDocument>>
            onRefresh = new BaasHandler<List<BaasDocument>>() {
        @Override
        public void handle(BaasResult<List<BaasDocument>> result) {
            //mDialog.dismiss();
            mRefresh=null;
            try {
                Iterator it = result.get().iterator();
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy.MM.dd");

                //Clear Lists
                voucherFromMeList.clear();

                while(it.hasNext()){
                    BaasDocument doc = (BaasDocument) it.next();
                    DateTime redeemedAt = null;
                    if(!doc.getString("redeemedAt").equals("")){
                        redeemedAt = formatter.parseDateTime(doc.getString("redeemedAt"));
                    }
                    DateTime dateOfDelivery = formatter.parseDateTime(doc.getString("dateOfDelivery"));
                    DateTime  dateOfexpiration = formatter.parseDateTime(doc.getString("dateOfexpiration"));
                    String name = doc.getString("name");
                    String notes = doc.getString("notes");
                    String givenTo = doc.getString("givenTo");
                    String redeemedWhere = doc.getString("redeemedWhere");

                    voucherFromMeList.add(new VoucherFromMe(name,givenTo,dateOfDelivery,dateOfexpiration,redeemedWhere,notes,redeemedAt));

                }

                 System.out.println("FromMe Data loaded");
                //onRefresh is asynchron and has to activate the display change somehow. like this?
                adapter.setItemList(voucherFromMeList);


                //mListFragment.refresh(result.get());
            }catch (BaasInvalidSessionException e){
                //startLoginScreen();
            }catch (BaasException e){
                Log.e("LOGERR", "Error " + e.getMessage(), e);
                //Toast.makeText(NoteListActivity.this,"Error while talking with the box",Toast.LENGTH_LONG).show();
            }
        }
    };
}
