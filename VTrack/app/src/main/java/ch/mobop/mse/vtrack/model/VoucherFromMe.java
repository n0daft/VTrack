package ch.mobop.mse.vtrack.model;

import org.joda.time.DateTime;

/**
 * Represents a voucher which was given away from the user to another person.
 * Created by n0daft on 09.03.2015.
 */
public class VoucherFromMe extends Voucher {
    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    /** Date which states when the user gave the voucher to someone */
    private DateTime dateOfDelivery;

    /** To whom the voucher was given */
    private String givenTo;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public VoucherFromMe(String name, String givenTo, DateTime dateOfDelivery, DateTime dateOfexpiration, String redeemWhere, String notes, DateTime redeemedAt) {
        super(name, dateOfexpiration, redeemWhere, notes, redeemedAt);
        this.dateOfDelivery = dateOfDelivery;
        this.givenTo = givenTo;
    }

    /***************************************************************************
     *                                                                         *
     * Getters / Setters                                                       *
     *                                                                         *
     **************************************************************************/

    public DateTime getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(DateTime dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public String getGivenTo() {
        return givenTo;
    }

    public void setGivenTo(String givenTo) {
        this.givenTo = givenTo;
    }
}
