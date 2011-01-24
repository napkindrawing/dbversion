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
    
    public boolean DEBUG = false;
    
    @Override
    public List<Profile> loadProfiles() {
        
    	if(DEBUG) System.out.println("JarProfilesLoader.loadProfiles: " + getJarPath());
    	
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
				if(DEBUG) System.out.println("   Jar Entry [isDirectory:"+entry.isDirectory()+"] >>> " + entry.getName());
				if(DEBUG) System.out.println("       " + entry.toString());
				if(entry.getName().matches("\\AMETA-INF.*") || entry.isDirectory()) {
					if(DEBUG) System.out.println("        skippin!");
					continue;
				}
				if(!entry.getName().matches("\\A[\\w\\-\\.\\:]+/\\d{5}.*\\.sql\\z")) {
					throw new BuildException("Unrecognized file in sql jar: " + entry.getName());
				}
				String profileName = entry.getName().substring(0, entry.getName().indexOf("/"));
				if(DEBUG) System.out.println("        Profile: " + profileName);
				Profile profile = profilesByName.get(profileName);
				if(profile == null) {
					if(DEBUG) System.out.println("        Adding Profile");
					profile = new Profile();
					profile.setName(profileName);
					profiles.add(profile);
					profilesByName.put(profileName, profile);
				}
				String versionNameFull = entry.getName().substring(entry.getName().indexOf("/")+1);
				if(DEBUG) System.out.println("         Full version Name: " + versionNameFull);
				String versionNum = versionNameFull.substring(0,5);
				if(DEBUG) System.out.println("         Version Num: " + versionNum);
				String versionNameComment = versionNameFull.length() > 9
				                          ? versionNameFull.replaceFirst("\\.sql", "").substring(6)
		                                  : "";
				byte[] sqlBytes = new byte[ (int) entry.getSize() ];
				if(DEBUG) System.out.println("         Reading " + entry.getSize() + " bytes");
				
				int count;
				byte[] sqlBuf = new byte[2048];
				int totalCount = 0;
				
				while(( count = zin.read(sqlBuf)) != -1) {
					if(DEBUG) System.out.println("         arrayCopy(sqlBuf, 0, sqlBytes," + totalCount+","+count+")");
					
					System.arraycopy(sqlBuf, 0, sqlBytes, totalCount, count);
					totalCount += count;
				}
				if(DEBUG) System.out.println("         Total bytes read: " + totalCount);
				
				String sql = new String(sqlBytes);
				
				if(DEBUG) System.out.println("==========SQL:=========================================");
				if(DEBUG) System.out.println(sql);
				if(DEBUG) System.out.println("======================================================");
				
				Revision revision = new Revision(new Version(versionNum));
				revision.setUpgradeScriptTemplate(sql);
				revision.assignUpgradeScriptTemplateChecksum();
				revision.setName(versionNameComment);
				
				profile.getRevisions().add(revision);
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
        return profiles;
    }

    @Override
    public void setProfileLoader(ProfileLoader profileLoader) {
        this.profileLoader = profileLoader;
    }
        
}
