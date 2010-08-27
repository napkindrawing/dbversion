package com.napkindrawing.dbversion;

import java.util.SortedSet;

public class Profile {
    private String name;
    private SortedSet<Revision> revisions;
    
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
}
