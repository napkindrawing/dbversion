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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.SQLExec;

import com.napkindrawing.dbversion.InstalledRevision;
import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;
import com.napkindrawing.dbversion.loader.LoaderSpec;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.ProfilesLoader;
import com.napkindrawing.dbversion.loader.RevisionLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;
import com.napkindrawing.dbversion.loader.fs.FileSystemLoader;

public abstract class DbVersionCommand extends SQLExec  {
    
    private List<Profile> profiles = new ArrayList<Profile>();
    private Properties config = new Properties();
    
    private Class<? extends LoaderSpec> loaderSpecClass = FileSystemLoader.class;

    private ProfilesLoader profilesLoader;
    private ProfileLoader profileLoader;
    private RevisionsLoader revisionsLoader;
    private RevisionLoader revisionLoader;
    
    protected Boolean checkInitTables = true;
    
    protected List<InstalledRevision> installedRevisions = null;
    
    public DbVersionCommand() {
        super();
    }

    private Map<Integer,String> _urlHostSchema = new HashMap<Integer,String>();

    public String getHostSchema() {
        if(getUrl() == null || getUrl().isEmpty()) {
            return null;
        }
        if(_urlHostSchema.containsKey(getUrl().hashCode())) {
            return _urlHostSchema.get(getUrl().hashCode());
        }
        Pattern p = Pattern.compile("^jdbc:\\w+://([\\w\\.\\-\\_]+/[\\w\\.\\-\\_]+).*$");
        Matcher m = p.matcher(getUrl());
        if(!m.matches()) {
            throw new BuildException("Couldn't extract hostname and schema from jdbc url: " + getUrl());
        }
        String hostSchema = m.group(1);
        _urlHostSchema.put(getUrl().hashCode(), hostSchema);
        return hostSchema;
    }
    
    protected String prependLogPrefix(String msg) {
        String hostSchema = getHostSchema();
        if(hostSchema != null && !hostSchema.isEmpty()) {
            String prefix = "[" + hostSchema + "] ";
            if(msg.startsWith(prefix)) {
                return msg;
            }
            return  prefix + msg;
        }
        return msg;
    }
    
    @Override
    public void log(String msg, int msgLevel) {
        super.log(prependLogPrefix(msg), msgLevel);
    }

    @Override
    public void log(String msg, Throwable t, int msgLevel) {
        super.log(prependLogPrefix(msg), t, msgLevel);
    }

    @Override
    public void log(String msg) {
        super.log(prependLogPrefix(msg));
    }

    @Override
    public void log(Throwable t, int msgLevel) {
        super.log(t, msgLevel);
    }

    @Override
    public void init() {
        super.init();

        LoaderSpec loaderSpec;
        
        try {
            loaderSpec = loaderSpecClass.newInstance();
            profilesLoader = loaderSpec.getProfilesLoaderClass().newInstance();
            profileLoader = loaderSpec.getProfileLoaderClass().newInstance();
            revisionsLoader = loaderSpec.getRevisionsLoaderClass().newInstance();
            revisionLoader = loaderSpec.getRevisionLoaderClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing loaders from spec " + loaderSpecClass.getName(),e);
        }
        
        profilesLoader.setProfileLoader(profileLoader);
        profileLoader.setRevisionsLoader(revisionsLoader);
        revisionsLoader.setRevisionLoader(revisionLoader);
        
    }
    
    public void execute() {

        profilesLoader.configure(config);
        profileLoader.configure(config);
        revisionsLoader.configure(config);
        revisionLoader.configure(config);
        
        setProfiles( profilesLoader.loadProfiles() );
        summarizeProfiles();
        
        if( checkInitTables && !canQueryRevisionTable()) {
            throw new BuildException("Database at " + getUrl() + " not initialized");
        }
        if(checkInitTables) {
            loadInstalledRevisions();
        }
    }
    
    private Map<String, Version> maxVersionByProfile = new HashMap<String, Version>();
    
    public Version getMaxVersion(String profileName) {
        Version v = maxVersionByProfile.get(profileName);
        return v == null ? Version.NONE : v;
    }    
    
    private Map<String,Profile> profilesByName = new HashMap<String,Profile>();
    
    public Profile getProfileByName(String profileName) {
        if(!profilesByName.containsKey(profileName)) {
            throw new BuildException("No profile named '"+profileName+"'");
        }
        return profilesByName.get(profileName);
    }
    
    protected void summarizeProfiles() {
        for(Profile profile : getProfiles()) {
            profilesByName.put(profile.getName(), profile);
            for(Revision revision : profile.getRevisions()) {
                String pn = profile.getName();
                Version v = revision.getVersion();
                if(!maxVersionByProfile.containsKey(pn)) {
                    maxVersionByProfile.put(pn, v);
                } else {
                    if(maxVersionByProfile.get(pn).compareTo(v) < 0) {
                        maxVersionByProfile.put(pn, v);
                    }
                }
            }
        }
    }

    private Map<String, Version> maxInstalledVersionByProfile = new HashMap<String, Version>();
    
    public Version getMaxInstalledVersion(String profileName) {
        Version v = maxInstalledVersionByProfile.get(profileName);
        return v == null ? Version.NONE : v;
    }
    
    protected void summarizeInstalledRevisions() {
        for(InstalledRevision i : installedRevisions) {
            String pn = i.getProfileName();
            Version v = i.getVersion();
            
            getProfileByName(pn).getInstalledRevisions().add(i);
            
            if(!installedProfileNames.contains(pn)) {
                installedProfileNames.add(pn);
            }

            if(lastInstalledRevision == null ||
                    lastInstalledRevision.getUpgradeDate().compareTo(i.getUpgradeDate()) < 0) {
                lastInstalledRevision = i;
            }
            
            if(!maxInstalledVersionByProfile.containsKey(pn)) {
                maxInstalledVersionByProfile.put(pn, v);
            } else {
                if(maxInstalledVersionByProfile.get(pn).compareTo(v) < 0) {
                    maxInstalledVersionByProfile.put(pn, v);
                }
            }
        }
    }

    protected InstalledRevision lastInstalledRevision = null;

    public InstalledRevision getLastInstalledRevision() {
        if(installedRevisions == null) {
            loadInstalledRevisions();
        }
        return lastInstalledRevision;
    }
    
    protected void loadInstalledRevisions() {

        installedRevisions = new ArrayList<InstalledRevision>();
        
        Connection conn = getConnection();
        Statement stmt = null;
        
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM __database_revision");
            
            installedRevisions = new ArrayList<InstalledRevision>();
            
            while(rs.next()) {
                installedRevisions.add(new InstalledRevision(rs));
            }
            
            log(installedRevisions.size() + " Revisions Read", Project.MSG_DEBUG);
            
        } catch(SQLException e) {
            throw new RuntimeException("Error querying database", e);
        } finally  {
            if (stmt != null) {
                try {stmt.close();}catch (SQLException ignore) {}
            }
        }
        
        summarizeInstalledRevisions();
        
    }
    
    protected List<String> installedProfileNames = new ArrayList<String>();
    
    public List<String> getInstalledProfileNames() {
        if(installedProfileNames == null) {
            loadInstalledRevisions();
        }
        return installedProfileNames;
    }
    
    public List<InstalledRevision> getInstalledRevisions() {
        if(installedRevisions == null) {
            loadInstalledRevisions();
        }
        return installedRevisions;
    }

    public boolean canQueryRevisionTable() {
        Connection conn = getConnection();
        Statement stmt = null;
        
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM __database_revision");
            
            if(rs.first()) {
                return true;
            }
            
        } catch(SQLException e) {
            if(e.getMessage().matches("^Table.*doesn't exist.*$")) {
                // MySQL
            } else {
                throw new RuntimeException("Error querying database", e);
            }
        } finally  {
            if (stmt != null) {
                try {stmt.close();}catch (SQLException ignore) {}
            }
        }
        return false;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
    
    public void addConfiguredLoaderProperty(Property property) {
        log("Adding loaderProperty: " + property.getName() +" => " + property.getValue(), Project.MSG_DEBUG);
        config.put(property.getName(), property.getValue());
    }

    public void setLoaderSpecClass(Class<? extends LoaderSpec> loaderSpecClass) {
        this.loaderSpecClass = loaderSpecClass;
    }
    
    protected String loadResourceFile(String path) {
        InputStream createInputStream = getLoader().getResourceAsStream(path);
        
        if(createInputStream == null) {
            throw new RuntimeException("Couldnt' load resource file: " + path);
        }
        
        String contents;
        
        try {
            contents = IOUtils.toString(createInputStream);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        
        return contents;
    }
    
    protected void closeQuietly() {
        try {
            if (getStatement() != null) {
                getStatement().close();
            }
        } catch (SQLException ex) {
            // ignore
        }
        try {
            if (getConnection() != null) {
                getConnection().close();
            }
        } catch (SQLException ex) {
            // ignore
        }
    }
    
}
