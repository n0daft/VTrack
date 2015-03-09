package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Created by n0daft on 01.03.2015.
 */
public class VoucherForMe {

    private DateTime dateOfReceipt;
    private DateTime dateOfexpiration;
    private String receivedBy;
    private String redeemAt;

    public VoucherForMe(DateTime dateOfReceipt, DateTime dateOfexpiration, String receivedBy, String redeemAt) {
        this.dateOfReceipt = dateOfReceipt;
        this.dateOfexpiration = dateOfexpiration;
        this.receivedBy = receivedBy;
        this.redeemAt = redeemAt;
    }

    public DateTime getDateOfReceipt() {
        return dateOfReceipt;
    }

    public void setDateOfReceipt(DateTime dateOfReceipt) {
        this.dateOfReceipt = dateOfReceipt;
    }

    public DateTime getDateOfexpiration() {
        return dateOfexpiration;
    }

    public void setDateOfexpiration(DateTime dateOfexpiration) {
        this.dateOfexpiration = dateOfexpiration;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getRedeemAt() {
        return redeemAt;
    }

    public void setRedeemAt(String redeemAt) {
        this.redeemAt = redeemAt;
    }
}
