package ch.mobop.mse.vtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;

import ch.mobop.mse.vtrack.widgets.DatePickerFragment;

/**
 * Created by n0daft on 01.03.2015.
 */
public class NewVoucherActivity extends FragmentActivity  implements DatePickerFragment.EditDateDialogListener {

    private EditText txtVoucherName;
    private TextView lblReceivedAt;
    private TextView lblValidUntil;
    private EditText txtReceivedFrom;
    private EditText txtNotes;
    private Intent intent;
    private BaasDocument receivedDoc;

    private String type;
    private String name;
    private String dateOfReceipt;
    private String dateOfDelivery;
    private String dateOfexpiration;
    private String receivedBy;
    private String givenTo;
    private String archive;
    private String redeemedAt;
    private String redeemedWhere;
    private String notes;

    private static final String PENDING_SAVE = "PENDING_SAVE";
    public static final int RESULT_SESSION_EXPIRED = Activity.RESULT_FIRST_USER+1;
    public static final int RESULT_FAILED = RESULT_SESSION_EXPIRED+1;

    private RequestToken mAddToken;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voucher);

        intent = getIntent();

        // Get UI component references
        txtVoucherName = (EditText) findViewById(R.id.txtVoucherName);
        lblReceivedAt = (TextView) findViewById(R.id.lblReceivedAt);
        lblValidUntil = (TextView) findViewById(R.id.lblValidUntil);
        txtReceivedFrom = (EditText) findViewById(R.id.txtReceivedFrom);
        txtNotes = (EditText) findViewById(R.id.txtNotes);

        if("edit".equals(intent.getStringExtra("intentType"))){
            txtVoucherName.setText(intent.getStringExtra("name"));
        }

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading...");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_voucher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_new_voucher_cancel:
                setResult(RESULT_CANCELED);
                finish();

                break;

            case R.id.action_new_voucher_save:

                //Create new BassDocument
                receivedDoc = new BaasDocument("vtrack");

                if("edit".equals(intent.getStringExtra("intentType"))){
                    //Fetch the old voucher with its id and save it after changes
                    retrieveOnBaasBox();
                }else{
                    //Create new voucher
                    addContentToDocument();
                    saveOnBaasBox(receivedDoc);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addContentToDocument(){
        receivedDoc.put("name",txtVoucherName.getText().toString());
        receivedDoc.put("dateOfexpiration", lblValidUntil.getText().toString());
        receivedDoc.put("notes", txtNotes.getText().toString());
        receivedDoc.put("archive", "false");
        receivedDoc.put("redeemedAt", "");
        receivedDoc.put("redeemedWhere", "");

        if("for_me".equals(intent.getStringExtra("type"))){
            //Content for_me voucher
            receivedDoc.put("type", "for_me");
            receivedDoc.put("dateOfReceipt", "2014.01.01");
            receivedDoc.put("receivedBy", "Callback-Agent");
        }else{
            //Content from_me voucher
            receivedDoc.put("type", "from_me");
            receivedDoc.put("dateOfDelivery", "Callback-Agent");
            receivedDoc.put("givenTo", "Callback-Agent");
        }
    }





    private void editOnBaasBox(){
        //Set new content
        addContentToDocument();
        receivedDoc.save(SaveMode.IGNORE_VERSION,new BaasHandler<BaasDocument>(){
            @Override
            public void handle(BaasResult<BaasDocument> res) {
                mDialog.dismiss();

                if(res.isSuccess()){
                    Log.d("LOG","Document saved "+res.value().getId());
                    System.out.println("Save OK");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.e("LOG","Error",res.error());
                    System.out.println("Save Fail");
                    setResult(RESULT_FAILED);
                    finish();
                }
            }
        });
    }

    private void retrieveOnBaasBox(){
        mDialog.show();
        mAddToken=BaasDocument.fetch("vtrack",intent.getStringExtra("baasID"),receiveHandler);
    }


    private final BaasHandler<BaasDocument> receiveHandler= new BaasHandler<BaasDocument>() {
        @Override
        public void handle(BaasResult<BaasDocument> res) {
            mAddToken=null;
            if(res.isSuccess()) {
                receivedDoc = res.value();
                editOnBaasBox();
            } else {
                setResult(RESULT_FAILED);
                finish();
                //Log.d("ERROR", "Failed with error", doc.error());
            }
        }
    };


    private void saveOnBaasBox(BaasDocument document){
        mDialog.show();
        mAddToken=document.save(SaveMode.IGNORE_VERSION,uploadHandler);
    }

    private final BaasHandler<BaasDocument> uploadHandler = new BaasHandler<BaasDocument>() {
        @Override
        public void handle(BaasResult<BaasDocument> doc) {
            mDialog.dismiss();
            mAddToken=null;

            if(doc.isSuccess()){
                setResult(RESULT_OK);
                finish();
            } else {
                if (doc.error() instanceof BaasInvalidSessionException){
                    setResult(RESULT_SESSION_EXPIRED);
                    finish();
                }else{
                    setResult(RESULT_FAILED);
                    Log.d("ERROR", "Failed with error", doc.error());
                    finish();
                }
            }
        }
    };




    public void handleReceivedAt(View v){

        System.out.println("11");
        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("callerId", 1);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), "datePicker");
        System.out.println("22");
    }

    public void handleValidUntil(View v){

        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("callerId", 2);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onFinishEditDialog(int year, int month, int day, int callerId) {
        switch (callerId){
            case 1:
                lblReceivedAt.setText(String.valueOf(day) + "." + String.valueOf(month + 1) + "." + String.valueOf(year));
                break;
            case 2:
                lblValidUntil.setText(String.valueOf(day) + "." + String.valueOf(month + 1) + "." + String.valueOf(year));
                break;
            default:
                Toast.makeText(getBaseContext(),"Something went wrong with the DatePicker", Toast.LENGTH_SHORT).show();
        }
    }
}
