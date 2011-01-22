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
package com.napkindrawing.dbversion.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.ant.BuildException;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.ProfilesLoader;

public class JarProfilesLoader extends JarLoader implements ProfilesLoader {
    
    ProfileLoader profileLoader;
    
    @Override
    public List<Profile> loadProfiles() {
        
    	System.out.println("JarProfilesLoader.loadProfiles: " + getJarPath());
    	
    	if(getJarPath() == null) {
    		throw new BuildException("jarPath is null!");
    	}
    	
        List<Profile> profiles = new ArrayList<Profile>();
        Map<String,Profile> profilesByName = new HashMap<String,Profile>();
        
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(getJarPath());
        ZipInputStream zin = new ZipInputStream(in);
        
        ZipEntry entry = null;
        
        try {
			while((entry = zin.getNextEntry()) != null) {
				System.out.println("   Jar Entry [isDirectory:"+entry.isDirectory()+"] >>> " + entry.getName());
				System.out.println("       " + entry.toString());
				if(entry.getName().matches("\\AMETA-INF.*") || entry.isDirectory()) {
					System.out.println("        skippin!");
					continue;
				}
				if(!entry.getName().matches("\\A\\w+/\\d{5}.*\\.sql\\z")) {
					throw new BuildException("Unrecognized file in sql jar: " + entry.getName());
				}
				String profileName = entry.getName().substring(0, entry.getName().indexOf("/"));
				System.out.println("        Profile: " + profileName);
				Profile profile = profilesByName.get(profileName);
				if(profile == null) {
					System.out.println("        Adding Profile");
					profile = new Profile();
					profile.setName(profileName);
					profiles.add(profile);
					profilesByName.put(profileName, profile);
				}
				String versionNameFull = entry.getName().substring(entry.getName().indexOf("/")+1);
				System.out.println("         Full version Name: " + versionNameFull);
				String versionNum = versionNameFull.substring(0,5);
				System.out.println("         Version Num: " + versionNum);
				
				byte[] sqlBytes = new byte[ (int) entry.getSize() ];
				System.out.println("         Reading " + entry.getSize() + " bytes");
				
				int count;
				byte[] sqlBuf = new byte[2048];
				int totalCount = 0;
				
				while(( count = zin.read(sqlBuf)) != -1) {
					System.out.println("         arrayCopy(sqlBuf, 0, sqlBytes," + totalCount+","+count+")");
					
					System.arraycopy(sqlBuf, 0, sqlBytes, totalCount, count);
					totalCount += count;
				}
				System.out.println("         Total bytes read: " + totalCount);
				
				String sql = new String(sqlBytes);
				
				System.out.println("==========SQL:=========================================");
				System.out.println(sql);
				System.out.println("======================================================");
				
				Revision revision = new Revision(new Version(versionNum));
				revision.setUpgradeScriptTemplate(sql);
				revision.assignUpgradeScriptTemplateChecksum();
				
				profile.getRevisions().add(revision);
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
        
    	/*
        if(!getProfilesDir().isDirectory() || !getProfilesDir().canRead()) {
            throw new RuntimeException("Profiles path <"+getProfilesDir()+"> must be readable directory");
        }

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
        */
        
        return profiles;
    }

    @Override
    public void setProfileLoader(ProfileLoader profileLoader) {
        this.profileLoader = profileLoader;
    }
        
}
