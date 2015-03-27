package ch.mobop.mse.vtrack.helpers;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Provides global configurations.
 * Created by n0daft on 23.03.2015.
 */
public abstract class Config {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yy");
    public static final DateTimeFormatter dateTimeFormatterBaas = DateTimeFormat.forPattern("yyyy.MM.dd");
    public static final int RESULT_ARCHIVED    = 11;


}
