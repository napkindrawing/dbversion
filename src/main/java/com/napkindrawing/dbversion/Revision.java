package com.napkindrawing.dbversion;

import org.apache.commons.codec.digest.DigestUtils;

public class Revision implements Comparable<Revision> {
    
    private Version version;
    private String name = "";
    private String upgradeScriptTemplate;
    private String upgradeScriptTemplateChecksum;

    public Revision() {
        
    }
    
    public Revision(Version version) {
        this.version = version;
    }
    
    public Version getVersion() {
        return version;
    }
    public void setVersion(Version version) {
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUpgradeScriptTemplate() {
        return upgradeScriptTemplate;
    }
    public void setUpgradeScriptTemplate(String upgradeScriptTemplate) {
        this.upgradeScriptTemplate = upgradeScriptTemplate;
    }

    public String getUpgradeScriptTemplateChecksum() {
        return upgradeScriptTemplateChecksum;
    }

    public void setUpgradeScriptTemplateChecksum(
            String upgradeScriptTemplateChecksum) {
        this.upgradeScriptTemplateChecksum = upgradeScriptTemplateChecksum;
    }

    @Override
    public int compareTo(Revision o) {
        return version.compareTo(o.getVersion());
    }

    public void assignUpgradeScriptTemplateChecksum() {
        upgradeScriptTemplateChecksum = DigestUtils.md5Hex(upgradeScriptTemplate);
    }
    
}
