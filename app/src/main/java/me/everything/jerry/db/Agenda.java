package me.everything.jerry.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nitsan on 7/7/15.
 */
public class Agenda implements Parcelable {
    public static final String KEY = "key_agenda";
    private String contactName;
    private String contactNumber;
    private String agenda;
    private String agendaSubject;
    private int seen;

    public Agenda(String contactName, String contactNumber, String agenda, String agendaSubject, int seen) {
        this.agenda = agenda;
        this.agendaSubject = agendaSubject;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.seen = seen;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAgenda() {
        return agenda;
    }

    public int getSeen() {
        return seen;
    }

    public String getAgendaSubject() {
        return agendaSubject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{contactName, contactNumber, agenda, agendaSubject});
        dest.writeInt(seen);
    }

    private Agenda(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        contactName = data[0];
        contactNumber = data[1];
        agenda = data[2];
        agendaSubject = data[3];
        seen = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Agenda createFromParcel(Parcel source) {
            return new Agenda(source);
        }

        @Override
        public Agenda[] newArray(int size) {
            return new Agenda[size];
        }
    };
}
