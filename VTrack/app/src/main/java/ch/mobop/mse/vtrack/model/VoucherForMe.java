package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Represents a voucher which was given from a person to the user.
 * Created by n0daft on 01.03.2015.
 */
public class VoucherForMe extends Voucher {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    /** Date which states when the user received the voucher */
    private DateTime dateOfReceipt;

    /** From whom the voucher was received */
    private String receivedBy;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public VoucherForMe(String name, String receivedBy, DateTime dateOfReceipt, DateTime dateOfexpiration, String redeemWhere, String notes, DateTime redeemedAt, String id) {
        super(name,dateOfexpiration, redeemWhere, notes, redeemedAt, id);
        this.dateOfReceipt = dateOfReceipt;
        this.receivedBy = receivedBy;
    }

    /***************************************************************************
     *                                                                         *
     * Getters / Setters                                                       *
     *                                                                         *
     **************************************************************************/

    public DateTime getDateOfReceipt() {
        return dateOfReceipt;
    }

    public void setDateOfReceipt(DateTime dateOfReceipt) {
        this.dateOfReceipt = dateOfReceipt;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

}
