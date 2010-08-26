package com.napkindrawing.dbversion;

import java.util.List;

public class Profile {
    private String name;
    private List<Revision> revisions;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Revision> getRevisions() {
        return revisions;
    }
    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }
}
