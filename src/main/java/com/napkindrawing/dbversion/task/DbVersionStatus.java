package com.napkindrawing.dbversion.task;

import com.napkindrawing.dbversion.Version;



public class DbVersionStatus extends DbVersionProfileCommand {
    
    @Override
    public void execute() {
        
        super.execute();
        
        for(String profileName : getProfileNamesArray()) {
            System.out.printf("Profile: %s\n", profileName);
            
            Version maxInstalled = getMaxInstalledVersion(profileName);
            String maxInstalledDescr = maxInstalled == null ? "(NONE)" : maxInstalled.getId();
            
            Version max = getMaxVersion(profileName);
            String maxDescr = max == null ? "(NONE)" : max.getId();
            
            System.out.printf(
                "    Installed / Available: %s/%s\n",
                maxInstalledDescr,
                maxDescr
            );
        }
        
    }
}
