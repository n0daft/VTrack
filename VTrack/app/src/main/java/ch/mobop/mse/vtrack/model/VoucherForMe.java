package ch.mobop.mse.vtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Represents a voucher which was given from a person to the user.
 * Created by n0daft on 01.03.2015.
 */
public class VoucherForMe extends Voucher implements Parcelable{

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

    public VoucherForMe(){

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
        dest.writeSerializable(dateOfReceipt);
        dest.writeString(receivedBy);
    }



    public static final Parcelable.Creator<VoucherForMe> CREATOR = new Creator<VoucherForMe>() {
        public VoucherForMe createFromParcel(Parcel source) {
            VoucherForMe voucherForMe = new VoucherForMe();
            voucherForMe.setName(source.readString());
            voucherForMe.setRedeemedAt(new DateTime(source.readSerializable()));
            voucherForMe.setDateOfexpiration(new DateTime(source.readSerializable()));
            voucherForMe.setRedeemWhere(source.readString());
            voucherForMe.setNotes(source.readString());
            voucherForMe.setId(source.readString());
            voucherForMe.setDateOfReceipt(new DateTime(source.readSerializable()));
            voucherForMe.setReceivedBy(source.readString());

            return voucherForMe;
        }

        @Override
        public VoucherForMe[] newArray(int size) {
            return new VoucherForMe[size];
        }

    };

}
