package ch.mobop.mse.vtrack;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextView desc_txtPerson;
    private TextView desc_txtReceivedAt;
    private Intent intent;
    private Voucher voucher;
    private VoucherForMe voucherForMe;
    private VoucherFromMe voucherFromMe;
    private BaasDocument receivedDoc;

    private static final String PENDING_SAVE = "PENDING_SAVE";
    public static final int RESULT_SESSION_EXPIRED = Activity.RESULT_FIRST_USER+1;
    public static final int RESULT_FAILED = RESULT_SESSION_EXPIRED+1;

    private RequestToken mAddToken;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_voucher);

        intent = getIntent();

        // Get UI component references
        txtVoucherName = (TextView) findViewById(R.id.detail_txtVoucherName);
        txtReceivedAt = (TextView) findViewById(R.id.detail_txtReceivedAt);
        txtValidUntil = (TextView) findViewById(R.id.detail_txtValidUntil);
        txtPerson = (TextView) findViewById(R.id.detail_txtPerson);
        txtNotes = (TextView) findViewById(R.id.detail_txtNotes);
        desc_txtPerson = (TextView) findViewById(R.id.detail_desc_txtPerson);
        desc_txtReceivedAt = (TextView) findViewById(R.id.detail_desc_txtReceivedAt);

        // Set Intent Data
        voucher = getIntent().getParcelableExtra("voucherParcelable");

        txtVoucherName.setText(voucher.getName());
        txtNotes.setText(voucher.getNotes());
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
        mDialog.setMessage("Archiving...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voucher_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_archive:

                retrieveOnBaasBox();
                break;

            case R.id.action_edit:
                // Intent to edit the voucher
                Intent edit = new Intent(DetailVoucherActivity.this,NewVoucherActivity.class);

                // Add current voucher object to intent and reuse intent object type.
                Bundle bundle = new Bundle();
                updateVoucher();
                if("for_me".equals(intent.getStringExtra("type"))) {
                    bundle.putParcelable("voucherParcelable", voucherForMe);
                }else{
                    bundle.putParcelable("voucherParcelable", voucherFromMe);
                }
                edit.putExtras(bundle);
                edit.putExtra("intentType","edit");
                edit.putExtra("type", intent.getStringExtra("type"));

                startActivityForResult(edit,EDIT_CODE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveOnBaasBox(){
        mDialog.show();
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
        //Set new content
        mDialog.dismiss();
        receivedDoc.put("archive", "true");
        receivedDoc.put("redeemedAt", "2015.12.24");
        receivedDoc.save(SaveMode.IGNORE_VERSION,new BaasHandler<BaasDocument>(){
            @Override
            public void handle(BaasResult<BaasDocument> res) {
                mDialog.dismiss();

                if(res.isSuccess()){
                    Log.d("LOG", "Document saved " + res.value().getId());
                    System.out.println("Archived Voucher");
                    setResult(RESULT_OK);
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

    private void updateVoucher(){
        //Needed in case you edit a voucher multiple times

        DateTime date_validUntil = new DateTime(new Date());
        if(!txtValidUntil.getText().toString().isEmpty()) {
            date_validUntil = Config.dateTimeFormatter.parseDateTime(txtValidUntil.getText().toString());
        }

        DateTime date_receivedAt = new DateTime(new Date());
        if(!txtReceivedAt.getText().toString().isEmpty()) {
            date_receivedAt = Config.dateTimeFormatter.parseDateTime(txtReceivedAt.getText().toString());
        }

        String id = voucher.getId();
        String name = txtVoucherName.getText().toString();
        String notes = txtNotes.getText().toString();
        String redeemedWhere = "";
        DateTime redeemedAt = null;
        String receivedBy = txtPerson.getText().toString();

        if("for_me".equals(intent.getStringExtra("type"))){
            voucherForMe = new VoucherForMe(name,receivedBy,date_receivedAt,date_validUntil,redeemedWhere,notes,redeemedAt,id);
        }else{
            voucherFromMe =  new VoucherFromMe(name,receivedBy,date_receivedAt,date_validUntil,redeemedWhere,notes,redeemedAt,id);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==EDIT_CODE){
            if (resultCode==RESULT_OK){
                //Update text elements
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
                Voucher voucherEdit = data.getParcelableExtra("voucherParcelableEdited");
                txtVoucherName.setText(voucherEdit.getName());
                System.out.println("Name: " + voucherEdit.getName());
                txtNotes.setText(voucherEdit.getNotes());
                System.out.println("Notes: " + voucherEdit.getNotes());
                txtValidUntil.setText(Config.dateTimeFormatter.print(voucherEdit.getDateOfexpiration()));

            } else if(resultCode==NewVoucherActivity.RESULT_SESSION_EXPIRED){
               // startLoginScreen();
            } else if (resultCode==NewVoucherActivity.RESULT_FAILED){
                Toast.makeText(this, "Failed to add voucher", Toast.LENGTH_LONG).show();
            } else if (resultCode==NewVoucherActivity.RESULT_CANCELED){
                Toast.makeText(this, "Canceled new voucher", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




}
