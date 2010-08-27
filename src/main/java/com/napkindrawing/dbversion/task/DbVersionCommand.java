package com.napkindrawing.dbversion.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.Property;

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

public abstract class DbVersionCommand extends JDBCTask {
    
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
        
    }
    
    private Map<String, Version> maxVersionByProfile = new HashMap<String, Version>();
    
    public Version getMaxVersion(String profileName) {
        return maxVersionByProfile.get(profileName);
    }    
    
    private Map<String,Profile> profilesByName = new HashMap<String,Profile>();
    
    public Profile getProfileByName(String profileName) {
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
        return maxInstalledVersionByProfile.get(profileName);
    }
    
    protected void summarizeInstalledRevisions() {
        for(InstalledRevision i : installedRevisions) {
            String pn = i.getProfileName();
            Version v = i.getVersion();
            if(!maxInstalledVersionByProfile.containsKey(pn)) {
                maxInstalledVersionByProfile.put(pn, v);
            } else {
                if(maxInstalledVersionByProfile.get(pn).compareTo(v) < 0) {
                    maxInstalledVersionByProfile.put(pn, v);
                }
            }
        }
    }
    
    protected void loadInstalledRevisions() {

        installedRevisions = new ArrayList<InstalledRevision>();
        
        Connection conn = getConnection();
        Statement stmt = null;
        
        try {
            System.out.println("Querying Database: " + getUrl());
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM __database_revision");
            
            installedRevisions = new ArrayList<InstalledRevision>();
            
            while(rs.next()) {
                installedRevisions.add(new InstalledRevision(rs));
            }
            
            System.out.println(installedRevisions.size() + " Revisions Read");
            
        } catch(SQLException e) {
            throw new RuntimeException("Error querying database", e);
        } finally  {
            if (stmt != null) {
                try {stmt.close();}catch (SQLException ignore) {}
            }
            if (conn != null) {
                try {conn.close();}catch (SQLException ignore) {}
            }
        }
        
        summarizeInstalledRevisions();
        
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
            if (conn != null) {
                try {conn.close();}catch (SQLException ignore) {}
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
        config.put(property.getName(), property.getValue());
    }

    public void setLoaderSpecClass(Class<? extends LoaderSpec> loaderSpecClass) {
        this.loaderSpecClass = loaderSpecClass;
    }
    
}
