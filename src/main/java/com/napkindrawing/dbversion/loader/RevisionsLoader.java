package com.napkindrawing.dbversion.loader;

import java.util.SortedSet;

import com.napkindrawing.dbversion.Revision;

public interface RevisionsLoader extends Configurable {
    public SortedSet<Revision> loadRevisions(String profileName);
    public void setRevisionLoader(RevisionLoader revisionLoader);
}
