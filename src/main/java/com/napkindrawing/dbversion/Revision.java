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
