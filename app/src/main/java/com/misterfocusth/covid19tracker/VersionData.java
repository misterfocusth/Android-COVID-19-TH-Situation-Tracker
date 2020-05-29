package com.misterfocusth.covid19tracker;

public class VersionData {

    private String versionCode, versionName, downloadUrl;

    public VersionData() {
    }

    public VersionData(String versionCode, String versionName, String downloadUrl) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.downloadUrl = downloadUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
