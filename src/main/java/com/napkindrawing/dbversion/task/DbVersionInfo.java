package com.napkindrawing.dbversion.task;

import org.apache.tools.ant.BuildException;

import com.napkindrawing.dbversion.Profile;

public class DbVersionInfo extends DbVersionCommand {
    
    public DbVersionInfo() {
        super();
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
        System.out.println("info! profile count " + getProfiles().size());
        for(Profile profile : getProfiles()) {
            System.out.println("Profile: " + profile.getName()); 
            System.out.println("    Revisions: " + profile.getRevisions().size());
        }
    }
    
}
