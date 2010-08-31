/*
 * Copyright 2010 NapkinDrawing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.napkindrawing.dbversion;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

public class InstalledRevision extends Revision {
    
    private String profileName;
    private String upgradeScriptData;
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
            setName(rs.getString("upgrade_script_name"));
            setUpgradeScriptCompiled(rs.getString("upgrade_script_compiled"));
            setUpgradeScriptCompiledChecksum(rs.getString("upgrade_script_compiled_checksum"));
            setUpgradeScriptData(rs.getString("upgrade_script_data"));
            setUpgradeScriptTemplate(rs.getString("upgrade_script_template"));
            setUpgradeScriptTemplateChecksum(rs.getString("upgrade_script_template_checksum"));
            setPostUpgradeSchemaDump(rs.getString("post_upgrade_schema_dump"));
            setPostUpgradeSchemaDumpChecksum(rs.getString("post_upgrade_schema_dump_checksum"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public InstalledRevision(Profile profile, Revision revision) {
        setProfileName(profile.getName());
        setVersion(revision.getVersion());
        setName(revision.getName());
        setUpgradeScriptTemplate(revision.getUpgradeScriptTemplate());
        setUpgradeScriptTemplateChecksum(revision.getUpgradeScriptTemplateChecksum());
    }

    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getUpgradeScriptData() {
        return upgradeScriptData;
    }

    public void setUpgradeScriptData(String upgradeScriptData) {
        this.upgradeScriptData = upgradeScriptData;
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

    public void assignUpgradeScriptCompiledChecksum() {
        upgradeScriptCompiledChecksum = DigestUtils.md5Hex(upgradeScriptCompiled);
    }
    
}
