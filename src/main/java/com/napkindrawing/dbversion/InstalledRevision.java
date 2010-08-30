package com.napkindrawing.dbversion;

import java.sql.ResultSet;
import java.util.Date;

public class InstalledRevision extends Revision {
    
    private String profileName;
    private String upgradeScriptName;
    private String upgradeScriptCompiledChecksum;
    private String upgradeScriptCompiled;
    private String postUpgradeSchemaDump;
    private String postUpgradeSchemaDumpChecksum;
    private Date upgradeDate;
    
    public InstalledRevision(Version version) {
        super(version);
    }
    
    public InstalledRevision(String profileName, Version version) {
        super(version);
        setProfileName(profileName);
    }
    
    public InstalledRevision(ResultSet rs) {
        super();
        try {
            setProfileName(rs.getString("profile"));
            setVersion(new Version(rs.getString("version")));
            setUpgradeDate(rs.getDate("upgrade_date"));
            setUpgradeScriptName(rs.getString("upgrade_script_name"));
            setUpgradeScriptCompiledChecksum(rs.getString("upgrade_script_compiled_checksum"));
            setUpgradeScriptTemplateChecksum(rs.getString("upgrade_script_template_checksum"));
            setPostUpgradeSchemaDumpChecksum(rs.getString("post_upgrade_schema_dump_checksum"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    public String getUpgradeScriptName() {
        return upgradeScriptName;
    }

    public void setUpgradeScriptName(String upgradeScriptName) {
        this.upgradeScriptName = upgradeScriptName;
    }

    public String getUpgradeScriptCompiledChecksum() {
        return upgradeScriptCompiledChecksum;
    }
    public void setUpgradeScriptCompiledChecksum(
            String upgradeScriptCompiledChecksum) {
        this.upgradeScriptCompiledChecksum = upgradeScriptCompiledChecksum;
    }
    public String getUpgradeScriptCompiled() {
        return upgradeScriptCompiled;
    }
    public void setUpgradeScriptCompiled(String upgradeScriptCompiled) {
        this.upgradeScriptCompiled = upgradeScriptCompiled;
    }
    public String getPostUpgradeSchemaDump() {
        return postUpgradeSchemaDump;
    }
    public void setPostUpgradeSchemaDump(String postUpgradeSchemaDump) {
        this.postUpgradeSchemaDump = postUpgradeSchemaDump;
    }
    public String getPostUpgradeSchemaDumpChecksum() {
        return postUpgradeSchemaDumpChecksum;
    }
    public void setPostUpgradeSchemaDumpChecksum(
            String postUpgradeSchemaDumpChecksum) {
        this.postUpgradeSchemaDumpChecksum = postUpgradeSchemaDumpChecksum;
    }

    public Date getUpgradeDate() {
        return upgradeDate;
    }

    public void setUpgradeDate(Date upgradeDate) {
        this.upgradeDate = upgradeDate;
    }
    
}
