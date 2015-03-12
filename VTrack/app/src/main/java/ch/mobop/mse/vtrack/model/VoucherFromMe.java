package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Created by n0daft on 09.03.2015.
 */
public class VoucherFromMe extends Voucher {

    private DateTime dateOfReceipt;
    private DateTime dateOfexpiration;
    private DateTime redeemedAt;
    private String givenTo;
    private String redeemWhere;
    private String name;

    public VoucherFromMe(DateTime dateOfReceipt, DateTime dateOfexpiration, String givenTo, DateTime redeemedAt, String redeemWhere, String name) {
        this.dateOfReceipt = dateOfReceipt;
        this.dateOfexpiration = dateOfexpiration;
        this.redeemedAt = redeemedAt;
        this.givenTo = givenTo;
        this.redeemWhere = redeemWhere;
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

    public DateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(DateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public String getGivenTo() {
        return givenTo;
    }

    public void setGivenTo(String givenTo) {
        this.givenTo = givenTo;
    }

    public String getRedeemWhere() {
        return redeemWhere;
    }

    public void setRedeemWhere(String redeemWhere) {
        this.redeemWhere = redeemWhere;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
