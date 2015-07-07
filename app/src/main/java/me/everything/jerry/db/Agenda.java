package me.everything.jerry.db;

/**
 * Created by nitsan on 7/7/15.
 */
public class Agenda {
    private String contactName;
    private String contactNumber;
    private String agenda;

    public Agenda(String contactName, String contactNumber, String agenda) {
        this.agenda = agenda;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
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
}
