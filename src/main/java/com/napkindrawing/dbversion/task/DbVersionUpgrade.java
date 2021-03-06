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
package com.napkindrawing.dbversion.task;

import java.io.File;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.napkindrawing.dbversion.InstalledRevision;
import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DbVersionUpgrade extends DbVersionProfileCommand {
    
    public String templateData;
    
    public String templateDataFiles;
    
    protected JSONObject parsedTemplateData = new JSONObject();
    
    public DbVersionUpgrade() {
        super();
    }
    
    @Override
    public void execute() throws BuildException {
        
        super.execute();
        
        parseTemplateData();
        
        System.out.printf("Upgrading database to latest version: %s\n", getUrl());
        
        try {
            verifySchemaMatch();
            for(String profileName : getProfileNamesArray()) {
                log("Upgrading profile: " + profileName);
                execute(getProfileByName(profileName));
            }
        } finally {
            closeQuietly();
        }
        
    }
    
    public void execute(Profile profile) {
        
        System.out.printf("Checking profile for upgrade: %s\n",profile.getName());
        
        Version maxInstalledVersion = getMaxInstalledVersion(profile.getName());
        Version maxVersion = getMaxVersion(profile.getName());
        
        if(maxVersion == Version.NONE) {
            
            log("No revisions available for install");
            
        } else if(maxInstalledVersion.compareTo(maxVersion) < 0) {
            
            log("Profile needs upgrade");
            log("Installed: " + maxInstalledVersion,Project.MSG_VERBOSE);
            log("Available: " + maxVersion,Project.MSG_VERBOSE);
            
            verifyPriorInstalledRevisions(profile, maxInstalledVersion);
            
            performUpgrade(profile, maxInstalledVersion);
        
        } else if(maxInstalledVersion.compareTo(maxVersion) == 0) {
        
            log("Profile is up-to-date  (Version "+maxVersion+")");
            
            verifyPriorInstalledRevisions(profile, maxVersion);
        
        } else if(maxInstalledVersion.compareTo(maxVersion) > 0) {
        
            throw new BuildException("Installed version is greater than max local version",getLocation());
        
        }
    }
    
    protected Map<Profile,Map<Version,String>> compiledTemplates = new HashMap<Profile,Map<Version,String>>();
    
    protected Configuration _fmConfig = new Configuration(); 
    
    protected String getCompiledTemplate(Profile profile, Revision revision) {

        if(compiledTemplates.containsKey(profile) && compiledTemplates.get(profile).containsKey(revision.getVersion())) {
            return compiledTemplates.get(profile).get(revision.getVersion());
        }
        
        CharSequenceReader templateReader = new CharSequenceReader(revision.getUpgradeScriptTemplate());
        
        String compiledTemplate = null;
        
        try {
            Template fmTemplate = new Template(revision.getName(), templateReader, _fmConfig);
            StringWriter templateWriter = new StringWriter();
            fmTemplate.process(parsedTemplateData, templateWriter);
            compiledTemplate = templateWriter.toString();
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        }
        
        if(!compiledTemplates.containsKey(profile)) {
            compiledTemplates.put(profile, new HashMap<Version,String>());
        }
        compiledTemplates.get(profile).put(revision.getVersion(), compiledTemplate);

        return compiledTemplate;
    }
    
    protected void parseTemplateData() {
        
        if(getConnection() != null) {
            try {
                parsedTemplateData.put("dbSchema", getConnection().getCatalog());
            } catch (SQLException e) {
                throw new BuildException("Couldn't extract catalog from connection", getLocation());
            }
        }
        
        if(templateDataFiles != null && !templateDataFiles.isEmpty()) {
            try {
                String[] files = templateDataFiles.split("\\s*,\\s*");
                log("Totally parsing " + files.length + " files!!!",Project.MSG_INFO);
                for(String file : files) {
                    String fileContents = FileUtils.readFileToString(new File(file));
                    parsedTemplateData.putAll((JSONObject) JSONSerializer.toJSON(fileContents));
                }
            } catch (Exception e) {
                throw new BuildException(e, getLocation());
            }
        }
        if(templateData != null && !templateData.isEmpty()){
            parsedTemplateData.putAll((JSONObject) JSONSerializer.toJSON(templateData));            
        }
        
    }
    
    public void setTemplateData(String templateData) {
        this.templateData = templateData;
    }

    public void setTemplateDataFiles(String templateDataFiles) {
        this.templateDataFiles = templateDataFiles;
    }
    
    public String getParsedTemplateData() {
        return parsedTemplateData.toString();
    }

    /**
     * 
     * @param profile
     * @param from - Exclusive version
     */
    public void performUpgrade(Profile profile, Version from) {
        log("Upgrading profile " + profile.getName());
        
            for(Revision revision : profile.getRevisions()) {
                if(from.compareTo(revision.getVersion()) < 0 ) {
                    applyRevision(profile, revision);
                }
            }
        log("Upgrade complete for profile " + profile.getName());
        
    }
    
    private void verifyPriorInstalledRevisions(Profile profile, Version upTo) {
        
        if(upTo == Version.NONE) {
            return;
        }
        
        log("Verifying prior installed revisions up to " + upTo);
        for(Revision rev : profile.getRevisions()) {
            if(rev.getVersion().compareTo(upTo) <= 0) {
                
                log("Verifying installed revision " + rev.getVersion());
                
                InstalledRevision installedRev = profile.getInstalledRevision(rev.getVersion());
                if(!rev.getUpgradeScriptTemplateChecksum().equals(installedRev.getUpgradeScriptTemplateChecksum())) {
                    logDiffs("Installed Template", "Local Template", installedRev.getUpgradeScriptTemplate(), rev.getUpgradeScriptTemplate());
                    throw new BuildException("Prior installed revision " + rev.getVersion() + " has different template checksum!", getLocation());
                }
                
                String compiledTmpl = getCompiledTemplate(profile, rev);
                String compiledTmplCksum = DigestUtils.md5Hex(compiledTmpl);
                
                if(!installedRev.getUpgradeScriptCompiledChecksum().equals(compiledTmplCksum)) {
                    logDiffs("Installed Compiled Template", "Local Compiled Template", installedRev.getUpgradeScriptCompiled(), compiledTmpl);
                    throw new BuildException("Prior installed revision " + rev.getVersion() + " compiled template differs from local!", getLocation());
                }
                
            }
        }
    }

    private void verifySchemaMatch() {

        log("Verifying schema", Project.MSG_DEBUG);
        
        InstalledRevision installedRev = getLastInstalledRevision();
        
        if(installedRev == null) {
            log("No installed revisions, not verifying schema",Project.MSG_DEBUG);
            return;
        }
        
        StringBuilder schemaDump = dumpSchema();
        String schemaDumpStr = schemaDump.toString();
        String schemaDumpCksum = DigestUtils.md5Hex(schemaDumpStr);
        
        String priorCksum = installedRev.getPostUpgradeSchemaDumpChecksum();
        
        if(!priorCksum.equals(schemaDumpCksum)) {
            logDiffs("Prior Schema", "Current Schema", installedRev.getPostUpgradeSchemaDump(), schemaDumpStr);
            throw new BuildException("Prior upgrade schema dump doesn't match existing schema!", getLocation());
        }
        
    }
    
    protected void logDiffs(String title1, String title2, String text1, String text2) {

        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> diffs = dmp.diff_main(text1, text2);

        dmp.diff_cleanupSemantic(diffs);
        
        log("================================================================================");  
        log(""+title1+":");
        log("--------------------------------------------------------------------------------");
        log(text1);
        log("================================================================================");  

        log("================================================================================");  
        log(""+title2+":");
        log("--------------------------------------------------------------------------------");
        log(text2);
        log("================================================================================");  

        log("================================================================================");  
        log("Diffs:");
        
        for(Diff diff : diffs) {
            String headerText = "";
            switch(diff.operation) {
                case INSERT:
                    headerText = "Unexpected Text";
                    break;
                case DELETE:
                    headerText = "Missing Text";
                    break;
                case EQUAL:
                    continue;
            }
            log("================================================================================");  
            log(headerText);
            log("--------------------------------------------------------------------------------");
            log(diff.text);
            log("================================================================================");
        }
    }

    public void applyRevision(Profile profile, Revision revision) {
        log("Applying revision " + revision.getVersion());
        log("Upgrade Script Template:\n\n" + revision.getUpgradeScriptTemplate() + "\n\n", Project.MSG_DEBUG);
        
        String compiledTemplate = getCompiledTemplate(profile, revision);
        
        log("Upgrade Script Compiled:\n\n" + compiledTemplate + "\n\n", Project.MSG_DEBUG);
        
        try {
            if (getConnection() == null) {
                throw new BuildException("Couldn't connect to database", getLocation());
            }
            runStatements(new CharSequenceReader(compiledTemplate), System.out);
            if (!isAutocommit()) {
                log("Committing transaction", Project.MSG_DEBUG);
                getConnection().commit();
            }
        } catch(Exception e) {
            throw new BuildException(e,getLocation());
        }
        
        InstalledRevision installedRevision = new InstalledRevision(profile, revision);
        installedRevision.setUpgradeScriptCompiled(compiledTemplate);
        installedRevision.assignUpgradeScriptCompiledChecksum();
        installedRevision.setUpgradeScriptData(getParsedTemplateData());
        
        logRevision(installedRevision);
        
    }
    
    protected void logRevision(InstalledRevision installedRevision) {
        log("Logging upgrade revision", Project.MSG_VERBOSE);
        PreparedStatement logStmt = null;
        try {
            
            StringBuilder schemaDump = dumpSchema();
            
            String schemaDumpStr = schemaDump.toString();
            
            String logSql = loadResourceFile("com/napkindrawing/dbversion/logRevision.sql");
            logStmt = getConnection().prepareStatement(logSql);
            logStmt.setString(1, installedRevision.getProfileName());
            logStmt.setString(2, installedRevision.getVersion().getId());
            logStmt.setString(3, installedRevision.getName());
            logStmt.setString(4, installedRevision.getUpgradeScriptTemplate());
            logStmt.setString(5, installedRevision.getUpgradeScriptTemplateChecksum());
            logStmt.setString(6, installedRevision.getUpgradeScriptData());
            logStmt.setString(7, installedRevision.getUpgradeScriptCompiled());
            logStmt.setString(8, installedRevision.getUpgradeScriptCompiledChecksum());
            logStmt.setString(9, schemaDumpStr);
            logStmt.setString(10, DigestUtils.md5Hex(schemaDumpStr));
            logStmt.setLong(11,System.currentTimeMillis());
            
            logStmt.execute();
            
            if (!isAutocommit()) {
                log("Committing transaction", Project.MSG_VERBOSE);
                getConnection().commit();
            }
        } catch(Exception e) {
            throw new BuildException(e,getLocation());
        } finally {
            try {
                if(logStmt != null) logStmt.close();
            } catch (SQLException e) {
            }
        }
    }

    protected StringBuilder dumpSchema() {
        
        StringBuilder dump = new StringBuilder();
        SortedSet<String> tableNames = new TreeSet<String>();
        
        Statement stmt = null;
        
        try {
            stmt = getConnection().createStatement();
            ResultSet tablesRs = stmt.executeQuery("SHOW TABLES");
            while(tablesRs.next()) {
                tableNames.add(tablesRs.getString(1));
            }
            
            for(String tableName : tableNames) {
                ResultSet describeRs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
                if(!describeRs.next()) {
                    throw new BuildException("Couldn't retrieve create sql for table " + tableName);
                }
                String tableCreateSql = describeRs.getString(2);
                tableCreateSql = tableCreateSql.replaceFirst(" AUTO_INCREMENT=\\d+ ", " ");
                dump.append(tableCreateSql);
                dump.append("\n;\n");
            }
            
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        } finally {
            if(stmt != null) {
                try { stmt.close(); } catch(Exception e) { }
            }
        }
        
        return dump;
    }
    
    
    
}
