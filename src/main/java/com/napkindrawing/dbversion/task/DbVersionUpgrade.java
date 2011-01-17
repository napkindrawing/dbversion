package com.napkindrawing.dbversion.task;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.tools.ant.BuildException;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DbVersionUpgrade extends DbVersionProfileCommand {
    
    public DbVersionUpgrade() {
        super();
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
        
        System.out.printf("Upgrading database to latest version: %s\n", getUrl());
        
        for(String profileName : getProfileNamesArray()) {
            execute(getProfileByName(profileName));
        }
        
    }
    
    public void execute(Profile profile) {
        
        System.out.printf("Checking profile for upgrade: %s\n",profile.getName());
        
        Version maxInstalledVersion = getMaxInstalledVersion(profile.getName());
        Version maxVersion = getMaxVersion(profile.getName());
        
        if(maxVersion == null) {
            System.out.println("No revisions available for install");
        } else if(maxInstalledVersion == null) {
            System.out.println("Profile has no installed revisions, performing full upgrade");
            performFullUpgrade(profile);
        } else if(maxInstalledVersion.compareTo(maxVersion) == 0) {
            System.out.printf("Profile is up-to-date  (Version %s)\n",maxVersion);
        } else if(maxInstalledVersion.compareTo(maxVersion) > 0) {
            throw new BuildException("Installed version is greater than max local version");
        } else {
            performUpgrade(profile, maxInstalledVersion);
        }
        
    }
 
    /**
     * 
     * @param profile
     */
    public void performFullUpgrade(Profile profile) {
        System.out.printf("Upgrading profile %s\n", profile.getName());
        for(Revision revision : profile.getRevisions()) {
            applyRevision(profile, revision);
        }
        System.out.println("Upgrade complete");
    }
 
    /**
     * 
     * @param profile
     * @param from - Exclusive version
     */
    public void performUpgrade(Profile profile, Version from) {
        System.out.printf("Upgrading profile %s\n", profile.getName());
        for(Revision revision : profile.getRevisions()) {
            if(from.compareTo(revision.getVersion()) < 0 ) {
                applyRevision(profile, revision);
            }
        }
        System.out.println("Upgrade complete");
    }
    
    public void applyRevision(Profile profile, Revision revision) {
        System.out.printf("Applying revision %s\n", revision.getVersion());
        System.out.println("Upgrade Script Template:\n\n" + revision.getUpgradeScriptTemplate() + "\n\n");
        
        CharSequenceReader templateReader = new CharSequenceReader(revision.getUpgradeScriptTemplate());
        
        String compiledTemplate = null;
        
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("restaurantName", "World!");
        
        Configuration fmConfig = new Configuration();
        try {
            Template fmTemplate = new Template(revision.getName(), templateReader, fmConfig);
            StringWriter templateWriter = new StringWriter();
            fmTemplate.process(data, templateWriter);
            compiledTemplate = templateWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error compiling template", e);
        }
        
        System.out.println("Upgrade Script Compiled:\n\n" + compiledTemplate + "\n\n");

    }
    
}
