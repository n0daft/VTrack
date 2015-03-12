package ch.mobop.mse.vtrack;

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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.FromMeRecyclerviewAdapter;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Created by n0daft on 12.03.2015.
 */
public class FromMeRecyclerviewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private RecyclerView recyclerView;
    private FromMeRecyclerviewAdapter adapter;
    private ArrayList<Voucher> voucherFromMeList;

    private RequestToken mRefresh;

    public static FromMeRecyclerviewFragment newInstance() {
        FromMeRecyclerviewFragment fragment = new FromMeRecyclerviewFragment();
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

        voucherFromMeList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addItemDecoration(getItemDecoration());

        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);

        adapter = new FromMeRecyclerviewAdapter();
        //adapter.setItemCount(getDefaultItemCount());

        //Load all Items from Server
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
    }

    private void refreshDocuments(){
        mRefresh = BaasDocument.fetchAll("vtrack", onRefresh);
    }

    private final BaasHandler<List<BaasDocument>>
            onRefresh = new BaasHandler<List<BaasDocument>>() {
        @Override
        public void handle(BaasResult<List<BaasDocument>> result) {
            //mDialog.dismiss();
            mRefresh=null;
            try {
                Iterator it = result.get().iterator();
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");

                //Clear Lists
                voucherFromMeList.clear();

                while(it.hasNext()){
                    BaasDocument doc = (BaasDocument) it.next();
                    String name = doc.getString("name");
                    DateTime created_on = formatter.parseDateTime(doc.getString("created_on"));
                    DateTime  valid_till = formatter.parseDateTime(doc.getString("valid_till"));
                    DateTime  redeemedAt = null;
                    String person = doc.getString("person");
                    String redeemed = doc.getString("redeemed");
                    if(Boolean.valueOf(doc.getString("archive"))){
                        //Add to archive list
                    }else{
                        if("from_me".equals(doc.getString("type"))){
                            //Voucher for me
                            voucherFromMeList.add(new VoucherFromMe(name,person,null,valid_till,"","",redeemedAt));
                        }
                    }

                }

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

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        //We must draw dividers ourselves if we want them in a list
        return new DividerDecoration(getActivity());
    }
    protected int getDefaultItemCount() {
        return 100;
    }
}
