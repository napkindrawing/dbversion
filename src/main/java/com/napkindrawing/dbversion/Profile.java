package com.napkindrawing.dbversion;

import java.util.SortedSet;
import java.util.TreeSet;

public class Profile {
    private String name;
    private SortedSet<Revision> revisions;
    private SortedSet<InstalledRevision> installedRevisions = new TreeSet<InstalledRevision>();
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public SortedSet<Revision> getRevisions() {
        return revisions;
    }
    public void setRevisions(SortedSet<Revision> revisions) {
        this.revisions = revisions;
    }
    public SortedSet<InstalledRevision> getInstalledRevisions() {
        return installedRevisions;
    }
    public void setInstalledRevisions(
            SortedSet<InstalledRevision> installedRevisions) {
        this.installedRevisions = installedRevisions;
    }
    public InstalledRevision getInstalledRevision(Version v) {
        for(InstalledRevision i : installedRevisions) {
            if(i.getVersion().equals(v)) {
                return i;
            }
        }
        return null;
    }
}
