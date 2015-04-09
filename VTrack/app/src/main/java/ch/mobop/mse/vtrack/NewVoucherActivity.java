package ch.mobop.mse.vtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.GregorianCalendar;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherForMe;
import ch.mobop.mse.vtrack.model.VoucherFromMe;
import ch.mobop.mse.vtrack.widgets.DatePickerFragment;

/**
 * Provides the logic for the new voucher view.
 * Created by n0daft on 01.03.2015.
 */
public class NewVoucherActivity extends FragmentActivity  implements DatePickerFragment.EditDateDialogListener {

    private EditText mTxtVoucherName;
    private TextView mLblReceivedAt;
    private TextView mLblValidUntil;
    private EditText mTxtReceivedFrom;
    private EditText mTxtNotes;
    private EditText mTxtLocation;
    private Intent mIntent;
    private BaasDocument mReceivedDoc;
    private Voucher mVoucher;
    private VoucherForMe mVoucherForMe;
    private VoucherFromMe mVoucherFromMe;

    public static final int RESULT_SESSION_EXPIRED = Activity.RESULT_FIRST_USER+1;
    public static final int RESULT_FAILED = RESULT_SESSION_EXPIRED+1;

    private RequestToken mAddToken;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voucher);

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        ColorDrawable color = new ColorDrawable(sharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        getActionBar().setBackgroundDrawable(color);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mIntent = getIntent();
        mVoucher = getIntent().getParcelableExtra("voucherParcelable");

        // Get UI component references.
        mTxtVoucherName = (EditText) findViewById(R.id.txtVoucherName);
        mLblReceivedAt = (TextView) findViewById(R.id.lblReceivedAt);
        mLblValidUntil = (TextView) findViewById(R.id.lblValidUntil);
        mTxtReceivedFrom = (EditText) findViewById(R.id.txtReceivedFrom);
        mTxtNotes = (EditText) findViewById(R.id.txtNotes);
        mTxtLocation = (EditText) findViewById(R.id.txtLocation);

        mLblValidUntil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handleValidUntil(v);
                }
            }
        });

        mLblReceivedAt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handleReceivedAt(v);
                }

            }
        });

        // Add content if it's an edit request.
        if("edit".equals(mIntent.getStringExtra("intentType"))){
            mVoucher = getIntent().getParcelableExtra("voucherParcelable");
            mTxtVoucherName.setText(mVoucher.getName());
            mTxtNotes.setText(mVoucher.getNotes());
            mTxtLocation.setText(mVoucher.getRedeemWhere());
            mLblValidUntil.setText(Config.dateTimeFormatter.print(mVoucher.getDateOfexpiration()));
            if("for_me".equals(mIntent.getStringExtra("type"))){
                VoucherForMe voucherForMe = (VoucherForMe) mVoucher;
                mTxtReceivedFrom.setText(voucherForMe.getReceivedBy());
                mLblReceivedAt.setText(Config.dateTimeFormatter.print(voucherForMe.getDateOfReceipt()));
            }else if("from_me".equals(mIntent.getStringExtra("type"))){
                VoucherFromMe voucherFromMe = (VoucherFromMe) mVoucher;
                mTxtReceivedFrom.setText(voucherFromMe.getGivenTo());
                mLblReceivedAt.setText(Config.dateTimeFormatter.print(voucherFromMe.getDateOfDelivery()));
            }
        }else{
            mVoucher = new Voucher("",null,"","",null,"");
        }

        if("from_me".equals(mIntent.getStringExtra("type"))){
            mTxtReceivedFrom.setHint(getString(R.string.activity_new_voucher_edittext_givento_hint));
            mLblReceivedAt.setHint(getString(R.string.activity_new_voucher_edittext_dataOfDelivery_hint));
        }


        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.dialog_uploading));

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

                // Create new BassDocument
                mReceivedDoc = new BaasDocument("vtrack");

                if("edit".equals(mIntent.getStringExtra("intentType"))){
                    // Fetch the old mVoucher with its id and save it after changes
                    retrieveOnBaasBox();
                }else{
                    // Only create new mVoucher id form is valid.
                    if(validateForm()){
                        // Create new mVoucher.
                        addContentToDocument();
                        saveOnBaasBox(mReceivedDoc);
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementation of DatePickerFragment onFinishEditDialog event.
     * Gets fired when DatePickerFragment calls onDateSet method.
     * @param year
     * @param month
     * @param day
     * @param callerId
     */
    @Override
    public void onFinishEditDialog(int year, int month, int day, int callerId) {
        mLblValidUntil.setError(null);
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(year,month,day);

        switch (callerId){
            case 1:
                mLblReceivedAt.setText(Config.dateTimeFormatter.print(cal.getTimeInMillis()));
                break;
            case 2:
                mLblValidUntil.setText(Config.dateTimeFormatter.print(cal.getTimeInMillis()));
                break;
            default:
                Toast.makeText(getBaseContext(),"Something went wrong with the DatePicker", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle click on the received at edit text, by initiating
     * the DatePickerFragment.
     * @param v
     */
    public void handleReceivedAt(View v){

        System.out.println("11");
        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("callerId", 1);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), "datePicker");
        System.out.println("22");
    }

    /**
     * Handle click on the valid until edit text, by initiating
     * the DatePickerFragment.
     * @param v
     */
    public void handleValidUntil(View v){

        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("callerId", 2);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private boolean validateForm() {

        // Reset errors.
        mTxtVoucherName.setError(null);
        mLblValidUntil.setError(null);
        mTxtReceivedFrom.setError(null);

        // Store values at the time of the login attempt.
        String voucherName = mTxtVoucherName.getText().toString();
        String dateOfReceipt = mLblReceivedAt.getText().toString();
        String validUntil = mLblValidUntil.getText().toString();
        String receivedFrom = mTxtReceivedFrom.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid person entry.
        if (TextUtils.isEmpty(receivedFrom)) {
            mTxtReceivedFrom.setError(getString(R.string.error_field_required));
            focusView = mTxtReceivedFrom;
            cancel = true;
        }

        // Check for a valid validity date.
        if (TextUtils.isEmpty(validUntil)) {
            mLblValidUntil.setError(getString(R.string.error_field_required));
            focusView = mLblValidUntil;
            cancel = true;
        }

        // Check if valid until lies before date of receipt.
        if(!TextUtils.isEmpty(dateOfReceipt) && !TextUtils.isEmpty(validUntil)){
            if (Config.dateTimeFormatter.parseDateTime(validUntil).isBefore(Config.dateTimeFormatter.parseDateTime(dateOfReceipt))) {
                mLblValidUntil.setError(getString(R.string.error_date_lies_before));
                focusView = mLblValidUntil;
                cancel = true;
            }
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(voucherName)) {
            mTxtVoucherName.setError(getString(R.string.error_field_required));
            focusView = mTxtVoucherName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void addContentToDocument(){

        DateTime date_validUntil = new DateTime(new Date());
        if(!mLblValidUntil.getText().toString().isEmpty()) {
            date_validUntil = Config.dateTimeFormatter.parseDateTime(mLblValidUntil.getText().toString());
        }

        DateTime date_receivedAt = new DateTime(new Date());
        if(!mLblReceivedAt.getText().toString().isEmpty()) {
             date_receivedAt = Config.dateTimeFormatter.parseDateTime(mLblReceivedAt.getText().toString());
        }

        //Update BaasDoc
        mReceivedDoc.put("name", mTxtVoucherName.getText().toString());
        mReceivedDoc.put("dateOfexpiration", Config.dateTimeFormatterBaas.print(date_validUntil));
        mReceivedDoc.put("notes", mTxtNotes.getText().toString());
        mReceivedDoc.put("archive", "false");
        mReceivedDoc.put("redeemedAt", "");
        mReceivedDoc.put("redeemedWhere", mTxtLocation.getText().toString());


        if("for_me".equals(mIntent.getStringExtra("type"))){
            //Content for_me voucher
            mReceivedDoc.put("type", "for_me");
            mReceivedDoc.put("dateOfReceipt", Config.dateTimeFormatterBaas.print(date_receivedAt));
            mReceivedDoc.put("receivedBy", mTxtReceivedFrom.getText().toString());
        }else{
            //Content from_me voucher
            mReceivedDoc.put("type", "from_me");
            mReceivedDoc.put("dateOfDelivery", Config.dateTimeFormatterBaas.print(date_receivedAt));
            mReceivedDoc.put("givenTo", mTxtReceivedFrom.getText().toString());
        }
    }


    private void addContentToVoucher(){

        DateTime date_validUntil = new DateTime(new Date());
        if(!mLblValidUntil.getText().toString().isEmpty()) {
            date_validUntil = Config.dateTimeFormatter.parseDateTime(mLblValidUntil.getText().toString());
        }

        DateTime date_receivedAt = new DateTime(new Date());
        if(!mLblReceivedAt.getText().toString().isEmpty()) {
            date_receivedAt = Config.dateTimeFormatter.parseDateTime(mLblReceivedAt.getText().toString());
        }

        //Update voucher object
        mVoucher.setName(mTxtVoucherName.getText().toString());
        mVoucher.setDateOfexpiration(date_validUntil);
        mVoucher.setNotes(mTxtNotes.getText().toString());
        mVoucher.setRedeemWhere(mTxtLocation.getText().toString());

        if("for_me".equals(mIntent.getStringExtra("type"))){
            mVoucherForMe = (VoucherForMe) mVoucher;
            mVoucherForMe.setDateOfReceipt(date_receivedAt);
            mVoucherForMe.setReceivedBy(mTxtReceivedFrom.getText().toString());
        }else{
            mVoucherFromMe = (VoucherFromMe) mVoucher;
            mVoucherFromMe.setDateOfDelivery(date_receivedAt);
            mVoucherFromMe.setGivenTo(mTxtReceivedFrom.getText().toString());
        }
    }

    private void editOnBaasBox(){
        //Set new content
        addContentToDocument();
        addContentToVoucher();
        mReceivedDoc.save(SaveMode.IGNORE_VERSION, new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> res) {
                mDialog.dismiss();

                if (res.isSuccess()) {
                    Log.d("LOG", "Document saved " + res.value().getId());
                    System.out.println("Save OK");
                    Intent resultIntent = new Intent();

                    Bundle bundle = new Bundle();
                    if ("from_me".equals(mIntent.getStringExtra("type"))) {
                        bundle.putParcelable("voucherParcelableEdited", mVoucherFromMe);
                    } else {
                        bundle.putParcelable("voucherParcelableEdited", mVoucherForMe);
                    }
                    resultIntent.putExtras(bundle);
                    resultIntent.putExtra("type", mIntent.getStringExtra("type"));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Log.e("LOG", "Error", res.error());
                    System.out.println("Save Fail");
                    setResult(RESULT_FAILED);
                    finish();
                }
            }
        });
    }

    private void retrieveOnBaasBox(){
        mDialog.show();
        mAddToken=BaasDocument.fetch("vtrack", mVoucher.getId(),receiveHandler);
    }


    private final BaasHandler<BaasDocument> receiveHandler= new BaasHandler<BaasDocument>() {
        @Override
        public void handle(BaasResult<BaasDocument> res) {
            mAddToken=null;
            if(res.isSuccess()) {
                mReceivedDoc = res.value();
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

}
