package com.napkindrawing.dbversion.loader.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.loader.RevisionLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;

public class FileSystemRevisionsLoader extends FileSystemLoader implements RevisionsLoader {
    
    private RevisionLoader revisionLoader;
    
    @Override
    public List<Revision> loadRevisions(String profileName) {

        List<Revision> revisions = new ArrayList<Revision>();
        
        File profileDir = getProfileDir(profileName);

        FilenameFilter sqlFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        };
        for(File revFile : profileDir.listFiles(sqlFileFilter)) {
            String version = revFile.getName().substring(0, 5);
            Revision revision = revisionLoader.loadRevision(profileName, version);
            revisions.add(revision);
        }
        return revisions;
    }

    public void setRevisionLoader(RevisionLoader revisionLoader) {
        this.revisionLoader = revisionLoader;
    }
}
