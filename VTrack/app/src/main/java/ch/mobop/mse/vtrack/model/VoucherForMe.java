package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Created by n0daft on 01.03.2015.
 */
public class VoucherForMe extends Voucher {

    private DateTime dateOfReceipt;
    private DateTime dateOfexpiration;
    private String receivedBy;
    private String redeemAt;
    private String name;

    public VoucherForMe(DateTime dateOfReceipt, DateTime dateOfexpiration, String receivedBy, String redeemAt, String name) {
        this.dateOfReceipt = dateOfReceipt;
        this.dateOfexpiration = dateOfexpiration;
        this.receivedBy = receivedBy;
        this.redeemAt = redeemAt;
        this.name = name;
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

    @Override
    public VoucherValidityStatusEnum getValidityStatus() {
        if(dateOfexpiration.isBefore(DateTime.now().minusMonths(1))){
            return VoucherValidityStatusEnum.valid;
        }else if(dateOfexpiration.isBefore(DateTime.now())){
            return VoucherValidityStatusEnum.soonToExpire;
        }else {
            return VoucherValidityStatusEnum.expired;
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
