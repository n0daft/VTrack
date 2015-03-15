package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Created by n0daft on 09.03.2015.
 */
public class Voucher {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    /** Simple name for the voucher given by the user */
    private String name;

    /** Date which states when the voucher will expire */
    private DateTime dateOfexpiration;

    /** Date which states when the voucher was redeemed */
    private DateTime redeemedAt;

    /** Location/Company where the voucher can be redeemed */
    private String redeemWhere;

    /** Any Notes */
    private String notes;

    /** BassBox ID */
    private String id;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public Voucher(String name, DateTime dateOfexpiration, String redeemWhere, String notes, DateTime redeemedAt, String id) {
        this.name = name;
        this.dateOfexpiration = dateOfexpiration;
        this.redeemedAt = redeemedAt;
        this.notes = notes;
        this.id = id;
        this.redeemWhere = redeemWhere;
    }

    /***************************************************************************
     *                                                                         *
     * Getters / Setters                                                       *
     *                                                                         *
     **************************************************************************/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRedeemWhere() {
        return redeemWhere;
    }

    public void setRedeemWhere(String redeemWhere) {
        this.redeemWhere = redeemWhere;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns the validity status of this voucher.
     * @return
     */
    public VoucherValidityStatusEnum getValidityStatus() {
        if(DateTime.now().isBefore(dateOfexpiration.minusMonths(1))){
            return VoucherValidityStatusEnum.valid;
        }else if(DateTime.now().isBefore(dateOfexpiration)){
            return VoucherValidityStatusEnum.soonToExpire;
        }else {
            return VoucherValidityStatusEnum.expired;
        }
    }
}
