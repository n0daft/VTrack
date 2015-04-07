package ch.mobop.mse.vtrack.widgets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Custom dialog fragment which represents a date picker popup.
 * Created by n0daft on 02.03.2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int callerId = -1;

    public interface EditDateDialogListener {
        void onFinishEditDialog(int year, int month, int day, int callerId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        System.out.println("onCreateDialog");

        Bundle arguments = getArguments();
        if(arguments != null){
            callerId = arguments.getInt("callerId");
        }

        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        EditDateDialogListener activity = (EditDateDialogListener) getActivity();
        activity.onFinishEditDialog(year, month, day, callerId);
        this.dismiss();
    }




}
