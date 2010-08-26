package com.napkindrawing.dbversion.loader.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.ProfilesLoader;

public class FileSystemProfilesLoader extends FileSystemLoader implements ProfilesLoader {
    
    ProfileLoader profileLoader;
    
    @Override
    public List<Profile> loadProfiles() {
        
        if(!getProfilesDir().isDirectory() || !getProfilesDir().canRead()) {
            throw new RuntimeException("Profiles path must be readable directory");
        }
        
        List<Profile> profiles = new ArrayList<Profile>();

        for(File profileDirEntry : getProfilesDir().listFiles()) {
            if(profileDirEntry.getName().equals("config.properties")) {
                if(profileDirEntry.isDirectory()) {
                    throw new RuntimeException("Uhhhh config.properties should be a file...");
                }
                // TODO: load config.properties
                // config = ResourceUtils.
            } else if(profileDirEntry.isDirectory()) {
                profiles.add(profileLoader.loadProfile(profileDirEntry.getName()));
            }
        }
        
        return profiles;
    }

    @Override
    public void setProfileLoader(ProfileLoader profileLoader) {
        this.profileLoader = profileLoader;
    }
        
}
