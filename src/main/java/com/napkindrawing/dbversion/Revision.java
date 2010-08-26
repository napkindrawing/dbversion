package com.napkindrawing.dbversion;

public class Revision {
    
    private String version;
    private String name = "";
    private String upgradeScript;
    
    public Revision(String version) {
        this.version = version;
    }
    
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUpgradeScript() {
        return upgradeScript;
    }
    public void setUpgradeScript(String upgradeScript) {
        this.upgradeScript = upgradeScript;
    }
    
}
