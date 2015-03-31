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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;
import ch.mobop.mse.vtrack.model.Voucher;
import ch.mobop.mse.vtrack.model.VoucherForMe;
import ch.mobop.mse.vtrack.model.VoucherFromMe;

/**
 * Created by Simon on 16.03.2015.
 */
public class DetailVoucherActivity extends FragmentActivity{

    private final static int EDIT_CODE = 3;

    private TextView txtVoucherName;
    private TextView txtReceivedAt;
    private TextView txtValidUntil;
    private TextView txtPerson;
    private TextView txtNotes;
    private EditText txtLocation;
    private TextView desc_txtPerson;
    private TextView desc_txtReceivedAt;
    private Intent intent;
    private Voucher voucher;
    private VoucherForMe voucherForMe;
    private VoucherFromMe voucherFromMe;
    private BaasDocument receivedDoc;
    private boolean redeemed;
    private boolean isEdited;

    private static final String PENDING_SAVE = "PENDING_SAVE";
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

        intent = getIntent();
        isEdited = false;

        // Get UI component references
        txtVoucherName = (TextView) findViewById(R.id.detail_txtVoucherName);
        txtReceivedAt = (TextView) findViewById(R.id.detail_txtReceivedAt);
        txtValidUntil = (TextView) findViewById(R.id.detail_txtValidUntil);
        txtPerson = (TextView) findViewById(R.id.detail_txtPerson);
        txtNotes = (TextView) findViewById(R.id.detail_txtNotes);
        txtLocation = (EditText) findViewById(R.id.detail_txtLocation);
        desc_txtPerson = (TextView) findViewById(R.id.detail_desc_txtPerson);
        desc_txtReceivedAt = (TextView) findViewById(R.id.detail_desc_txtReceivedAt);

        // Set Intent Data
        voucher = getIntent().getParcelableExtra("voucherParcelable");

        txtVoucherName.setText(voucher.getName());
        txtNotes.setText(voucher.getNotes());
        txtLocation.setText(voucher.getRedeemWhere());
        DateTimeFormatter formatterVoucher = DateTimeFormat.forPattern("dd.MM.yy");
        txtValidUntil.setText(formatterVoucher.print(voucher.getDateOfexpiration()));

        if("for_me".equals(intent.getStringExtra("type"))){
            VoucherForMe voucherForMe = (VoucherForMe) voucher;
            txtPerson.setText(voucherForMe.getReceivedBy());
            txtReceivedAt.setText(formatterVoucher.print(voucherForMe.getDateOfReceipt()));
        }

        if("from_me".equals(intent.getStringExtra("type"))){
            VoucherFromMe voucherFromMe = (VoucherFromMe) voucher;
            txtPerson.setText(voucherFromMe.getGivenTo());
            txtReceivedAt.setText(formatterVoucher.print(voucherFromMe.getDateOfDelivery()));
            desc_txtPerson.setText("Given to");
            desc_txtReceivedAt.setText("Delivered at");
        }

        mDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Boolean.valueOf(intent.getStringExtra("archive"))){
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
                                redeemed = true;
                                archiveOnBaasBox();
                                break;
                            case R.id.action_expired:
                                redeemed = false;
                                archiveOnBaasBox();
                                break;
                            default: break;
                        }
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();
                break;

            case R.id.action_edit:
                // Intent to edit the voucher
                Intent edit = new Intent(DetailVoucherActivity.this,NewVoucherActivity.class);

                // Add current voucher object to intent and reuse intent object type.
                Bundle bundle = new Bundle();
                bundle.putParcelable("voucherParcelable", voucher);
                edit.putExtras(bundle);
                edit.putExtra("intentType","edit");
                edit.putExtra("type", intent.getStringExtra("type"));

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
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteVoucher(){
        mDialog.setMessage("Deleting...");
        if(!mDialog.isShowing())mDialog.show();
        mAddToken= BaasDocument.fetch("vtrack", voucher.getId(), deleteHandler);
    }

    private final BaasHandler<BaasDocument> deleteHandler= new BaasHandler<BaasDocument>() {
        @Override
        public void handle(BaasResult<BaasDocument> res) {

            mAddToken=null;
            if(mDialog.isShowing())mDialog.dismiss();

            if(res.isSuccess()) {
                receivedDoc = res.value();
                receivedDoc.delete(new BaasHandler<Void>() {
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
        mAddToken= BaasDocument.fetch("vtrack", voucher.getId(), receiveHandler);
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

    private void editOnBaasBox(){

        if(redeemed){
            receivedDoc.put("redeemed", "true");
        }else{
            receivedDoc.put("redeemed", "false");
        }
        receivedDoc.put("archive", "true");
        DateTime date_redeemedAt = new DateTime(new Date());
        receivedDoc.put("redeemedAt", Config.dateTimeFormatterBaas.print(date_redeemedAt));

        receivedDoc.save(SaveMode.IGNORE_VERSION,new BaasHandler<BaasDocument>(){
            @Override
            public void handle(BaasResult<BaasDocument> res) {

                if(mDialog.isShowing())mDialog.dismiss();

                if(res.isSuccess()){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==EDIT_CODE){
            if (resultCode==RESULT_OK){
                //Update text elements
                isEdited = true;
                Toast.makeText(this, "Edited voucher successfully", Toast.LENGTH_LONG).show();

                //Update text fields
                if("from_me".equals(data.getStringExtra("type"))){
                    VoucherFromMe voucherEdit = data.getParcelableExtra("voucherParcelableEdited");
                    txtPerson.setText(voucherEdit.getGivenTo());
                    txtReceivedAt.setText(Config.dateTimeFormatter.print(voucherEdit.getDateOfDelivery()));
                }else{
                    VoucherForMe voucherEdit = data.getParcelableExtra("voucherParcelableEdited");
                    txtPerson.setText(voucherEdit.getReceivedBy());
                    txtReceivedAt.setText(Config.dateTimeFormatter.print(voucherEdit.getDateOfReceipt()));
                }
                voucher = data.getParcelableExtra("voucherParcelableEdited");
                txtVoucherName.setText(voucher.getName());
                txtNotes.setText(voucher.getNotes());
                txtLocation.setText(voucher.getRedeemWhere());
                txtValidUntil.setText(Config.dateTimeFormatter.print(voucher.getDateOfexpiration()));

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
        //If the voucher was edited, refresh the list automaticly
        if(isEdited){setResult(RESULT_OK);}
        super.onBackPressed();
    }

    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
