package ch.mobop.mse.vtrack;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherForMe;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Provides the logic for the detail mVoucher view.
 * Created by Simon on 16.03.2015.
 */
public class DetailVoucherActivity extends FragmentActivity{

    private final static int EDIT_CODE = 3;

    private TextView mTxtVoucherName;
    private TextView mTxtReceivedAt;
    private TextView mTxtValidUntil;
    private TextView mTxtPerson;
    private TextView mTxtNotes;
    private EditText mTxtLocation;
    private TextView mDesc_txtPerson;
    private TextView mDesc_txtReceivedAt;
    private Intent mIntent;
    private Voucher mVoucher;
    private BaasDocument mReceivedDoc;
    private boolean mRedeemed;
    private boolean mIsEdited;

    public static final int RESULT_SESSION_EXPIRED = Activity.RESULT_FIRST_USER+1;
    public static final int RESULT_FAILED = RESULT_SESSION_EXPIRED+1;

    private RequestToken mAddToken;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_voucher);

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        ColorDrawable color = new ColorDrawable(sharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        getActionBar().setBackgroundDrawable(color);

        mIntent = getIntent();
        mIsEdited = false;

        // Get UI component references.
        mTxtVoucherName = (TextView) findViewById(R.id.detail_txtVoucherName);
        mTxtReceivedAt = (TextView) findViewById(R.id.detail_txtReceivedAt);
        mTxtValidUntil = (TextView) findViewById(R.id.detail_txtValidUntil);
        mTxtPerson = (TextView) findViewById(R.id.detail_txtPerson);
        mTxtNotes = (TextView) findViewById(R.id.detail_txtNotes);
        mTxtLocation = (EditText) findViewById(R.id.detail_txtLocation);
        mDesc_txtPerson = (TextView) findViewById(R.id.detail_desc_txtPerson);
        mDesc_txtReceivedAt = (TextView) findViewById(R.id.detail_desc_txtReceivedAt);

        // Set Intent Data.
        mVoucher = getIntent().getParcelableExtra("voucherParcelable");

        mTxtVoucherName.setText(mVoucher.getName());
        mTxtNotes.setText(mVoucher.getNotes());
        mTxtLocation.setText(mVoucher.getRedeemWhere());
        DateTimeFormatter formatterVoucher = Config.dateTimeFormatter;
        mTxtValidUntil.setText(formatterVoucher.print(mVoucher.getDateOfexpiration()));

        if("for_me".equals(mIntent.getStringExtra("type"))){
            VoucherForMe voucherForMe = (VoucherForMe) mVoucher;
            mTxtPerson.setText(voucherForMe.getReceivedBy());
            mTxtReceivedAt.setText(formatterVoucher.print(voucherForMe.getDateOfReceipt()));
        }

        if("from_me".equals(mIntent.getStringExtra("type"))){
            VoucherFromMe voucherFromMe = (VoucherFromMe) mVoucher;
            mTxtPerson.setText(voucherFromMe.getGivenTo());
            mTxtReceivedAt.setText(formatterVoucher.print(voucherFromMe.getDateOfDelivery()));
            mDesc_txtPerson.setText("Given to");
            mDesc_txtReceivedAt.setText("Delivered at");
        }

        mDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Boolean.valueOf(mIntent.getStringExtra("archive"))){
            getMenuInflater().inflate(R.menu.voucher_detail_archive, menu);
        }else{
            getMenuInflater().inflate(R.menu.voucher_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        PopupMenu popup;

        switch (item.getItemId()) {

            case R.id.action_archive:

                View menuItemViewNew = findViewById(R.id.action_archive);
                popup = new PopupMenu(getApplicationContext(),menuItemViewNew);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.voucher_detail_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_redeemed:
                                mRedeemed = true;
                                archiveOnBaasBox();
                                break;
                            case R.id.action_expired:
                                mRedeemed = false;
                                archiveOnBaasBox();
                                break;
                            default: break;
                        }
                        return true;
                    }
                });

                // Showing the popup menu.
                popup.show();
                break;

            case R.id.action_edit:
                // Intent to edit the mVoucher
                Intent edit = new Intent(DetailVoucherActivity.this,NewVoucherActivity.class);

                // Add current mVoucher object to intent and reuse intent object type.
                Bundle bundle = new Bundle();
                bundle.putParcelable("voucherParcelable", mVoucher);
                edit.putExtras(bundle);
                edit.putExtra("intentType","edit");
                edit.putExtra("type", mIntent.getStringExtra("type"));

                startActivityForResult(edit,EDIT_CODE);
                break;

            case R.id.action_overflow:

                View menuItemViewNewOver = findViewById(R.id.action_overflow);
                popup = new PopupMenu(getApplicationContext(),menuItemViewNewOver);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.voucher_detail_delete, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_delete:
                                deleteVoucher();
                                break;
                            default: break;
                        }
                        return true;
                    }
                });

                // Showing the popup menu.
                popup.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==EDIT_CODE){
            if (resultCode==RESULT_OK){
                //Update text elements
                mIsEdited = true;
                Toast.makeText(this, "Edited voucher successfully", Toast.LENGTH_LONG).show();

                //Update text fields
                if("from_me".equals(data.getStringExtra("type"))){
                    VoucherFromMe voucherEdit = data.getParcelableExtra("voucherParcelableEdited");
                    mTxtPerson.setText(voucherEdit.getGivenTo());
                    mTxtReceivedAt.setText(Config.dateTimeFormatter.print(voucherEdit.getDateOfDelivery()));
                }else{
                    VoucherForMe voucherEdit = data.getParcelableExtra("voucherParcelableEdited");
                    mTxtPerson.setText(voucherEdit.getReceivedBy());
                    mTxtReceivedAt.setText(Config.dateTimeFormatter.print(voucherEdit.getDateOfReceipt()));
                }
                mVoucher = data.getParcelableExtra("voucherParcelableEdited");
                mTxtVoucherName.setText(mVoucher.getName());
                mTxtNotes.setText(mVoucher.getNotes());
                mTxtLocation.setText(mVoucher.getRedeemWhere());
                mTxtValidUntil.setText(Config.dateTimeFormatter.print(mVoucher.getDateOfexpiration()));

            } else if(resultCode==NewVoucherActivity.RESULT_SESSION_EXPIRED){
                startLoginScreen();
            } else if (resultCode==NewVoucherActivity.RESULT_FAILED){
                Toast.makeText(this, "Failed to add voucher.", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        // If the mVoucher was edited, refresh the list automaticly.
        if(mIsEdited){setResult(RESULT_OK);}
        super.onBackPressed();
    }

    private void deleteVoucher(){
        mDialog.setMessage("Deleting...");
        if(!mDialog.isShowing())mDialog.show();
        mAddToken= BaasDocument.fetch("vtrack", mVoucher.getId(), deleteHandler);
    }

    private final BaasHandler<BaasDocument> deleteHandler= new BaasHandler<BaasDocument>() {
        @Override
        public void handle(BaasResult<BaasDocument> res) {

            mAddToken=null;
            if(mDialog.isShowing())mDialog.dismiss();

            if(res.isSuccess()) {
                mReceivedDoc = res.value();
                mReceivedDoc.delete(new BaasHandler<Void>() {
                    @Override
                    public void handle(BaasResult<Void> res) {
                        if (res.isSuccess()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            setResult(RESULT_FAILED);
                            finish();
                        }
                    }
                });

            } else {
                setResult(RESULT_FAILED);
                finish();
            }
        }
    };


    private void archiveOnBaasBox(){
        mDialog.setMessage("Archiving...");
        if(!mDialog.isShowing())mDialog.show();
        mAddToken= BaasDocument.fetch("vtrack", mVoucher.getId(), receiveHandler);
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

    private void editOnBaasBox(){

        if(mRedeemed){
            mReceivedDoc.put("redeemed", "true");
        }else{
            mReceivedDoc.put("redeemed", "false");
        }
        mReceivedDoc.put("archive", "true");
        DateTime date_redeemedAt = new DateTime(new Date());
        mReceivedDoc.put("redeemedAt", Config.dateTimeFormatterBaas.print(date_redeemedAt));

        mReceivedDoc.save(SaveMode.IGNORE_VERSION, new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> res) {

                if (mDialog.isShowing()) mDialog.dismiss();

                if (res.isSuccess()) {
                    setResult(Constants.RESULT_ARCHIVED);
                    finish();
                } else {
                    Log.e("LOG", "Error", res.error());
                    setResult(RESULT_FAILED);
                    finish();
                }
            }
        });
    }

    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
