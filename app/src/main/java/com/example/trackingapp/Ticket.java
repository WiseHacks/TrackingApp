package com.example.trackingapp;

public class Ticket {
    //

    public String ticketProjectId, raisedByUserId,assignedToDevId,ticketSubject,ticketDesc,ticketPriority,ticketStatusByAdmin,ticketStatusByDev,ticketModified,ticketPatchByDev;

    // This is Ticket for now.

    public Ticket() {
    }

    public Ticket(String ticketProjectId, String raisedByUserId, String assignedToDevId, String ticketSubject, String ticketDesc, String ticketPriority, String ticketStatusByAdmin, String ticketStatusByDev, String ticketModified, String ticketPatchByDev) {
        this.ticketProjectId = ticketProjectId;
        this.raisedByUserId = raisedByUserId;
        this.assignedToDevId = assignedToDevId;
        this.ticketSubject = ticketSubject;
        this.ticketDesc = ticketDesc;
        this.ticketPriority = ticketPriority;
        this.ticketStatusByAdmin = ticketStatusByAdmin;
        this.ticketStatusByDev = ticketStatusByDev;
        this.ticketModified = ticketModified;
        this.ticketPatchByDev = ticketPatchByDev;
    }

    public String getTicketProjectId() {
        return ticketProjectId;
    }

    public void setTicketProjectId(String ticketProjectId) {
        this.ticketProjectId = ticketProjectId;
    }

    public String getRaisedByUserId() {
        return raisedByUserId;
    }

    public void setRaisedByUserId(String raisedByUserId) {
        this.raisedByUserId = raisedByUserId;
    }

    public String getAssignedToDevId() {
        return assignedToDevId;
    }

    public void setAssignedToDevId(String assignedToDevId) {
        this.assignedToDevId = assignedToDevId;
    }

    public String getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
    }

    public String getTicketDesc() {
        return ticketDesc;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public String getTicketPriority() {
        return ticketPriority;
    }

    public void setTicketPriority(String ticketPriority) {
        this.ticketPriority = ticketPriority;
    }

    public String getTicketStatusByAdmin() {
        return ticketStatusByAdmin;
    }

    public void setTicketStatusByAdmin(String ticketStatusByAdmin) {
        this.ticketStatusByAdmin = ticketStatusByAdmin;
    }

    public String getTicketStatusByDev() {
        return ticketStatusByDev;
    }

    public void setTicketStatusByDev(String ticketStatusByDev) {
        this.ticketStatusByDev = ticketStatusByDev;
    }

    public String getTicketModified() {
        return ticketModified;
    }

    public void setTicketModified(String ticketModified) {
        this.ticketModified = ticketModified;
    }

    public String getTicketPatchByDev() {
        return ticketPatchByDev;
    }

    public void setTicketPatchByDev(String ticketPatchByDev) {
        this.ticketPatchByDev = ticketPatchByDev;
    }
}
