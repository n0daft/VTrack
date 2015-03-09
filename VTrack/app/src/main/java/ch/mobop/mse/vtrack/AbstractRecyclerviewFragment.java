package ch.mobop.mse.vtrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Iterator;
import java.util.List;

import ch.mobop.mse.vtrack.adapters.RecyclerViewDemoAdapter;

/**
 * Abstract implementation of a recyclerview fragment. This class can be extended
 * in order to create a custom recylcerview.
 * Created by n0daft on 01.03.2015.
 */
public abstract class AbstractRecyclerviewFragment extends Fragment implements AdapterView.OnItemClickListener{

    private RecyclerView recyclerView;
    private RecyclerViewDemoAdapter adapter;

    protected abstract RecyclerView.LayoutManager getLayoutManager();
    protected abstract RecyclerView.ItemDecoration getItemDecoration();
    protected abstract int getDefaultItemCount();

    private RequestToken mRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_received, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.section_list);
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addItemDecoration(getItemDecoration());

        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);

        adapter = new RecyclerViewDemoAdapter();
        adapter.setItemCount(getDefaultItemCount());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        refreshDocuments();
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
                while(it.hasNext()){
                    BaasDocument doc = (BaasDocument) it.next();
                    String name = doc.getString("name");
                    System.out.println(name);
                }

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
