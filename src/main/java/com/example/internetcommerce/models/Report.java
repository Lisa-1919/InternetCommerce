package com.example.internetcommerce.models;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class Report implements Serializable {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Date reportCreateDate;
    private User manager;
    private File directory;

    private String fileType;

    public Report() {
    }

    public Report(LocalDate fromDate, LocalDate toDate, Date reportCreateDate, User manager, File directory, String fileType) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reportCreateDate = reportCreateDate;
        this.manager = manager;
        this.directory = directory;
        this.fileType = fileType;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Date getReportCreateDate() {
        return reportCreateDate;
    }

    public void setReportCreateDate(Date reportCreateDate) {
        this.reportCreateDate = reportCreateDate;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
