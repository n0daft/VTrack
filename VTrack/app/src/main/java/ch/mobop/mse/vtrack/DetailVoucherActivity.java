package ch.mobop.mse.vtrack;


import android.app.Activity;
import android.content.Intent;
import android.app.ProgressDialog;
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
        txtVoucherName.setText(intent.getStringExtra("name"));
        txtPerson.setText(intent.getStringExtra("receivedBy"));
        txtNotes.setText(intent.getStringExtra("notes"));
        txtValidUntil.setText(intent.getStringExtra("dateOfexpiration"));
        txtReceivedAt.setText(intent.getStringExtra("dateOfReceipt"));

        if("from_me".equals(intent.getStringExtra("type"))){
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




}
