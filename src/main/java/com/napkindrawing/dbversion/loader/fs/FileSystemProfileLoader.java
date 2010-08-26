package com.napkindrawing.dbversion.loader.fs;

import java.io.File;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;

public class FileSystemProfileLoader extends FileSystemLoader implements ProfileLoader {
    
    RevisionsLoader revisionsLoader;
    
    @Override
    public Profile loadProfile(String profileName) {
        
        File profileDir = getProfileDir(profileName);
        
        Profile profile = new Profile();
        profile.setName(profileDir.getName());
        
        profile.setRevisions(revisionsLoader.loadRevisions(profileName));
                
        return profile;
    }

    @Override
    public void setRevisionsLoader(RevisionsLoader revisionsLoader) {
        this.revisionsLoader = revisionsLoader;
    }
    
}
