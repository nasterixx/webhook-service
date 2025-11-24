package com.example.webhook.model.payload;

import java.util.List;

public class PurchaseCreatedPayload {

    private String pId;
    private String tag;
    private String value;
    private List<TagValue> accounts;
    private String analysisId;
    private String filename;
    private String user;
    private String creationTimestamp;

    public static class TagValue {
        private String tag;
        private String value;
        public TagValue() {}
        public TagValue(String tag, String value) { this.tag = tag; this.value = value; }
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public String getpId() { return pId; }
    public void setpId(String pId) { this.pId = pId; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public List<TagValue> getAccounts() { return accounts; }
    public void setAccounts(List<TagValue> accounts) { this.accounts = accounts; }
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(String creationTimestamp) { this.creationTimestamp = creationTimestamp; }
}
