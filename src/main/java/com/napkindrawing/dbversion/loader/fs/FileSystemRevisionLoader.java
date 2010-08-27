package com.napkindrawing.dbversion.loader.fs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;
import com.napkindrawing.dbversion.loader.RevisionLoader;

public class FileSystemRevisionLoader extends FileSystemLoader implements RevisionLoader {

    @Override
    public Revision loadRevision(String profileName, Version version) {
        
        File revisionFile = getRevisionFile(profileName, version);

        Revision revision = new Revision(version);
        
        if(revisionFile.getName().length() > 10) {
            // Strip out version prefix and ".sql" suffix
            revision.setName(revisionFile.getName().substring(7,revisionFile.getName().length()-4));
        }
        
        try {
            revision.setUpgradeScriptTemplate( FileUtils.readFileToString(revisionFile, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error reading upgrade script", e);
        }
        
        return revision;
    }
    
    protected File getRevisionFile(String profileName, Version version) {

        File[] versionFiles = getProfileDir(profileName).listFiles(new StartsWithFilenameFilter(version.getId()));
        
        if(versionFiles.length == 0) {
            throw new RuntimeException("Profile '"+profileName+"' does not have version '"+version+"'");
        }
        
        if(!versionFiles[0].isFile() || !versionFiles[0].canRead()) {
            throw new RuntimeException("Version file must be readable file");
        }
        
        return versionFiles[0];
    }
    
}
