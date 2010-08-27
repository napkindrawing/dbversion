package com.napkindrawing.dbversion.task;

import org.apache.tools.ant.BuildException;

public class DbVersionProfileCommand extends DbVersionCommand {

    private String profileNames;
    
    @Override
    public void execute() {
        super.execute();
        if(profileNames == null || profileNames.isEmpty()) {
            throw new BuildException("Task " + getTaskType() + " requires the 'profileNames' attribute");
        }
    }

    public String getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(String profileNames) {
        this.profileNames = profileNames;
    }
    
    public String[] getProfileNamesArray() {
        return profileNames.split(",");
    }
    
}
