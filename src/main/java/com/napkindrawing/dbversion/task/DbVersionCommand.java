package com.napkindrawing.dbversion.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.Property;

import com.napkindrawing.dbversion.Profile;
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
