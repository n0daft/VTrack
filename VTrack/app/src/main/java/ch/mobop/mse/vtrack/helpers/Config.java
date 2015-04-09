package ch.mobop.mse.vtrack.helpers;

import android.graphics.drawable.ColorDrawable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Provides global configurations.
 * Created by n0daft on 23.03.2015.
 */
public abstract class Config {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDate();
    public static final DateTimeFormatter dateTimeFormatterBaas = DateTimeFormat.forPattern("yyyy.MM.dd");
    public static final int RESULT_ARCHIVED = 11;

    public static ColorDrawable defaultActionBarColor = new ColorDrawable(0xff005d66);

    /** */
    public static int defaultValidityThreshold = 1;
    public static int currentValidityThreshold;

}
