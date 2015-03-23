package ch.mobop.mse.vtrack;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.RequestToken;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

        //Set Intent Data

        Voucher voucher = getIntent().getParcelableExtra("voucherParcelable");

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
        mDialog.setMessage("Uploading...");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voucher_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_new_voucher_cancel:
                setResult(RESULT_CANCELED);
                finish();

                break;

            case R.id.action_edit:

                //Intent to edit the voucher
                Intent edit = new Intent(DetailVoucherActivity.this,NewVoucherActivity.class);
                edit.putExtra("intentType","edit");
                edit.putExtra("baasID",intent.getStringExtra("baasID"));
                edit.putExtra("name",intent.getStringExtra("name"));
                edit.putExtra("notes",intent.getStringExtra("notes"));
                edit.putExtra("archive",intent.getStringExtra("archive"));
                edit.putExtra("redeemedAt",intent.getStringExtra("redeemedAt"));
                edit.putExtra("redeemedWhere",intent.getStringExtra("redeemedWhere"));
                edit.putExtra("dateOfexpiration",intent.getStringExtra("dateOfexpiration"));

                if("from_me".equals(intent.getStringExtra("type"))){
                    edit.putExtra("dateOfDelivery",intent.getStringExtra("dateOfDelivery"));
                    edit.putExtra("givenTo",intent.getStringExtra("givenTo"));
                    edit.putExtra("type","from_me");
                }else{
                    edit.putExtra("dateOfReceipt",intent.getStringExtra("dateOfReceipt"));
                    edit.putExtra("receivedBy",intent.getStringExtra("receivedBy"));
                    edit.putExtra("type","for_me");
                }

                startActivityForResult(edit,EDIT_CODE);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==EDIT_CODE){
            if (resultCode==RESULT_OK){

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
