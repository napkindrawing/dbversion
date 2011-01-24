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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.Version;


public class DbVersionStatus extends DbVersionCommand {
    
    @Override
    public void execute() {
        
        super.execute();
        
        for(String profileName : getInstalledProfileNames()) {
            
            Version maxInstalled = getMaxInstalledVersion(profileName);
            Version max = getMaxVersion(profileName);

            String msg = maxInstalled.equals(max)
                       ? ""
                       : "   <=== OUT OF DATE";
            
            System.out.printf(
                "Installed/Available: %s/%s  Profile: %s%s\n",
                maxInstalled,
                max,
                profileName,
                msg
            );
        }
        
    }
    
    /**
     * Runs this task without printing a summary to standard out.
     */
    public List<Profile> executeStatus() {
        super.execute();

        return getProfiles();
    }
    
    public static void main(String[] args) {
        
        System.out.println("Howdy!");
        
        DbVersionStatus dbvStatus = new DbVersionStatus();
        
        Project project = new Project();
        project.setName("Testing");
        
        dbvStatus.setProject(project);
        dbvStatus.setUserid("root");
        dbvStatus.setPassword("toor");
        dbvStatus.setDriver("com.mysql.jdbc.Driver");
        dbvStatus.setUrl("jdbc:mysql://localhost/global");
        
        Property profilePath = new Property();
        profilePath.setName("profiles.path");
        profilePath.setValue("/home/mwalker/workspace/mwalker-enroll/enroll-database/src/main/sql");
        
        dbvStatus.addConfiguredLoaderProperty(profilePath);

        dbvStatus.init();
        List<Profile> profiles = dbvStatus.executeStatus();
        
        for(Profile profile : profiles) {
            
            System.out.printf(
                "Installed/Available: %s/%s  Profile: %s\n",
                profile.getMaxInstalledVersion(),
                profile.getMaxVersion(),
                profile.getName()
            );
        }        
    }
    
}
