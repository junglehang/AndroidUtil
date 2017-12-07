package com.test.androidutil.utils.appUploadUtil;

public class UpdateInfo {
    private String version;
    private String description;
    private String url;
    private String forceUpdate;
    private String[] descritions;

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String[] getDescriptions() {
        return descritions;
    }

    public void setDescritions(String[] descritions) {
        this.descritions = descritions;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
