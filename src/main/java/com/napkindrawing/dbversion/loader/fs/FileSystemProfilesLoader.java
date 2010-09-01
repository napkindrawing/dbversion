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
            throw new RuntimeException("Profiles path <"+getProfilesDir()+"> must be readable directory");
        }
        
        List<Profile> profiles = new ArrayList<Profile>();

        for(File profileDirEntry : getProfilesDir().listFiles()) {
            if(profileDirEntry.getName().equals("config.properties")) {
                if(profileDirEntry.isDirectory()) {
                    throw new RuntimeException("Uhhhh config.properties should be a file...");
                }
                // TODO: load config.properties
                // config = ResourceUtils.
            } else if(profileDirEntry.isDirectory() && !profileDirEntry.getName().startsWith(".")) {
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
