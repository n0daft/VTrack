package ch.mobop.mse.vtrack;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ch.mobop.mse.vtrack.widgets.DatePickerFragment;

/**
 * Created by n0daft on 01.03.2015.
 */
public class NewVoucherActivity extends FragmentActivity  implements DatePickerFragment.EditDateDialogListener {

    private TextView lblReceivedAt;
    private TextView lblValidUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voucher);

        lblReceivedAt = (TextView) findViewById(R.id.txtReceivedAt);
        lblValidUntil = (TextView) findViewById(R.id.txtValidUntil);
    }

    public void handleReceivedAt(View v){

        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("callerId", 1);
        newFragment.setArguments(args);

        newFragment.show(getSupportFragmentManager(), "datePicker");
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
