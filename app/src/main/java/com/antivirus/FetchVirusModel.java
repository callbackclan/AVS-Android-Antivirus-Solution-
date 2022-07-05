package com.antivirus;

/* loaded from: classes.dex */
public class FetchVirusModel {
    int Id;
    String VirusName;
    String VirusTag;

    public FetchVirusModel() {
    }

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getVirusName() {
        return this.VirusName;
    }

    public void setVirusName(String virusName) {
        this.VirusName = virusName;
    }

    public String getVirusTag() {
        return this.VirusTag;
    }

    public void setVirusTag(String virusTag) {
        this.VirusTag = virusTag;
    }

    public FetchVirusModel(int Id, String VirusName, String VirusTag) {
        this.Id = Id;
        this.VirusName = VirusName;
        this.VirusTag = VirusTag;
    }

    public FetchVirusModel(String VirusName, String VirusTag) {
        this.VirusName = VirusName;
        this.VirusTag = VirusTag;
    }
}