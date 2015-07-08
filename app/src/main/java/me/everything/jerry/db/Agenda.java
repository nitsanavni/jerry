package me.everything.jerry.db;

/**
 * Created by nitsan on 7/7/15.
 */
public class Agenda {
    private String contactName;
    private String contactNumber;
    private String agenda;
    private int seen;


    public Agenda(String contactName, String contactNumber, String agenda, int seen) {
        this.agenda = agenda;
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
}
