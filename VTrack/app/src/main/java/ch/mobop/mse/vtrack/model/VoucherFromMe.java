package ch.mobop.mse.vtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Represents a voucher which was given away from the user to another person.
 * Created by n0daft on 09.03.2015.
 */
public class VoucherFromMe extends Voucher implements Parcelable {
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

    public VoucherFromMe(String name, String givenTo, DateTime dateOfDelivery, DateTime dateOfexpiration, String redeemWhere, String notes, DateTime redeemedAt, String id) {
        super(name, dateOfexpiration, redeemWhere, notes, redeemedAt, id);
        this.dateOfDelivery = dateOfDelivery;
        this.givenTo = givenTo;
    }

    public VoucherFromMe(){

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

    /***************************************************************************
     *                                                                         *
     * Parcelable Interface Implementation                                     *
     *                                                                         *
     **************************************************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeSerializable(getRedeemedAt());
        dest.writeSerializable(getDateOfexpiration());
        dest.writeString(getRedeemWhere());
        dest.writeString(getNotes());
        dest.writeString(getId());
        dest.writeSerializable(dateOfDelivery);
        dest.writeString(givenTo);
    }



    public static final Parcelable.Creator<VoucherFromMe> CREATOR = new Creator<VoucherFromMe>() {
        public VoucherFromMe createFromParcel(Parcel source) {
            VoucherFromMe voucherFromMe = new VoucherFromMe();
            voucherFromMe.setName(source.readString());
            voucherFromMe.setRedeemedAt(new DateTime(source.readSerializable()));
            voucherFromMe.setDateOfexpiration(new DateTime(source.readSerializable()));
            voucherFromMe.setRedeemWhere(source.readString());
            voucherFromMe.setNotes(source.readString());
            voucherFromMe.setId(source.readString());
            voucherFromMe.setDateOfDelivery(new DateTime(source.readSerializable()));
            voucherFromMe.setGivenTo(source.readString());

            return voucherFromMe;
        }

        @Override
        public VoucherFromMe[] newArray(int size) {
            return new VoucherFromMe[size];
        }

    };
}
