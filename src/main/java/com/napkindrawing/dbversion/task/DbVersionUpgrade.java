package com.napkindrawing.dbversion.task;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
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
    
    public DbVersionUpgrade() {
        super();
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
        
        System.out.printf("Upgrading database to latest version: %s\n", getUrl());
        
        for(String profileName : getProfileNamesArray()) {
            execute(getProfileByName(profileName));
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
            performUpgrade(profile, maxInstalledVersion);
        
        } else if(maxInstalledVersion.compareTo(maxVersion) == 0) {
        
            log("Profile is up-to-date  (Version "+maxVersion+")");
        
        } else if(maxInstalledVersion.compareTo(maxVersion) > 0) {
        
            throw new BuildException("Installed version is greater than max local version",getLocation());
        
        }
        
    }
 
    /**
     * 
     * @param profile
     * @param from - Exclusive version
     */
    public void performUpgrade(Profile profile, Version from) {
        log("Upgrading profile " + profile.getName());
        
        verifySchemaMatch(profile, from);
        
        try {
            for(Revision revision : profile.getRevisions()) {
                if(from.compareTo(revision.getVersion()) < 0 ) {
                    applyRevision(profile, revision);
                }
            }
        } finally {
            closeQuietly();
        }
        log("Upgrade complete");
        
    }
    
    private void verifySchemaMatch(Profile profile, Version from) {
        
        if(from == Version.NONE) {
            log("Not verifying schema for first upgrade script",Project.MSG_DEBUG);
            return;
        }
        
        InstalledRevision installedRev = profile.getInstalledRevision(from);
        
        if(installedRev == null) {
            throw new BuildException("Asked to upgrade from version " + from + " but no installed revision w/that version found", getLocation());
        }
        
        StringBuilder schemaDump = dumpSchema();
        String schemaDumpStr = schemaDump.toString();
        String schemaDumpCksum = DigestUtils.md5Hex(schemaDumpStr);
        
        String priorCksum = installedRev.getPostUpgradeSchemaDumpChecksum();
        
        if(!priorCksum.equals(schemaDumpCksum)) {
            log("Prior Schema ("+priorCksum+"):\n\n"+installedRev.getPostUpgradeSchemaDump(), Project.MSG_ERR);
            log("Current Schema ("+schemaDumpCksum+"):\n\n"+schemaDumpStr, Project.MSG_ERR);
            throw new BuildException("Prior upgrade schema dump doesn't match existing schema!", getLocation());
        }
        
    }

    public void applyRevision(Profile profile, Revision revision) {
        log("Applying revision " + revision.getVersion());
        log("Upgrade Script Template:\n\n" + revision.getUpgradeScriptTemplate() + "\n\n");
        
        CharSequenceReader templateReader = new CharSequenceReader(revision.getUpgradeScriptTemplate());
        
        String compiledTemplate = null;
        
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("restaurantName", "World!");
        
        Configuration fmConfig = new Configuration();
        try {
            Template fmTemplate = new Template(revision.getName(), templateReader, fmConfig);
            StringWriter templateWriter = new StringWriter();
            fmTemplate.process(data, templateWriter);
            compiledTemplate = templateWriter.toString();
        } catch (Exception e) {
            throw new BuildException(e, getLocation());
        }

        log("Upgrade Script Compiled:\n\n" + compiledTemplate + "\n\n");
        
        try {
            if (getConnection() == null) {
                throw new BuildException("Couldn't connect to database", getLocation());
            }
            runStatements(new CharSequenceReader(compiledTemplate), System.out);
            if (!isAutocommit()) {
                log("Committing transaction", Project.MSG_VERBOSE);
                getConnection().commit();
            }
        } catch(Exception e) {
            throw new BuildException(e,getLocation());
        }
        
        log("Logging upgrade revision", Project.MSG_VERBOSE);
        PreparedStatement logStmt = null;
        try {
            
            StringBuilder schemaDump = dumpSchema();
            
            String schemaDumpStr = schemaDump.toString();
            
            String logSql = loadResourceFile("com/napkindrawing/dbversion/logRevision.sql");
            logStmt = getConnection().prepareStatement(logSql);
            logStmt.setString(1, profile.getName());
            logStmt.setString(2, revision.getVersion().getId());
            logStmt.setString(3, revision.getName());
            logStmt.setString(4, revision.getUpgradeScriptTemplate());
            logStmt.setString(5, revision.getUpgradeScriptTemplateChecksum());
            logStmt.setString(6, compiledTemplate);
            logStmt.setString(7, DigestUtils.md5Hex(compiledTemplate));
            logStmt.setString(8, schemaDumpStr);
            logStmt.setString(9, DigestUtils.md5Hex(schemaDumpStr));
            
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
