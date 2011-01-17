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
package com.napkindrawing.dbversion.loader.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import com.napkindrawing.dbversion.loader.Configurable;
import com.napkindrawing.dbversion.loader.LoaderSpec;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.ProfilesLoader;
import com.napkindrawing.dbversion.loader.RevisionLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;


public class FileSystemLoader implements Configurable, LoaderSpec {

    File profilesDir;
    
    public class MatchingFilenameFilter implements FilenameFilter {
        private String desiredName;
        public MatchingFilenameFilter(String desiredName) {
            this.desiredName = desiredName;
        }
        @Override
        public boolean accept(File dir, String name) {
            return name.equals(desiredName);
        }
    }
    
    public class StartsWithFilenameFilter implements FilenameFilter {
        private String prefix;
        public StartsWithFilenameFilter(String prefix) {
            this.prefix = prefix;
        }
        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(prefix);
        }
    }
    
    protected File getProfileDir(String profileName) {

        File[] profileDirs = getProfilesDir().listFiles(new MatchingFilenameFilter(profileName));
        
        if(profileDirs.length == 0) {
            throw new RuntimeException("Profile '"+profileName+"' not found in profile directory "+getProfilesDir().getPath());
        }
        
        if(!profileDirs[0].isDirectory() || !profileDirs[0].canRead()) {
            throw new RuntimeException("Profile path must be readable directory");
        }
        
        return profileDirs[0];
    }
    
    @Override
    public void configure(Properties properties) {
        profilesDir = new File(properties.getProperty("profiles.path", System.getProperty("user.dir")));
    }

    public File getProfilesDir() {
        return profilesDir;
    }

    public void setProfilesDir(File profilesDir) {
        this.profilesDir = profilesDir;
    }

    @Override
    public Class<? extends ProfilesLoader> getProfilesLoaderClass() {
        return FileSystemProfilesLoader.class;
    }

    @Override
    public Class<? extends ProfileLoader> getProfileLoaderClass() {
        return FileSystemProfileLoader.class;
    }

    @Override
    public Class<? extends RevisionsLoader> getRevisionsLoaderClass() {
        return FileSystemRevisionsLoader.class;
    }

    @Override
    public Class<? extends RevisionLoader> getRevisionLoaderClass() {
        // TODO Auto-generated method stub
        return FileSystemRevisionLoader.class;
    }
    
    

}
