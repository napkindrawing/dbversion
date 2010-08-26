package com.napkindrawing.dbversion.loader;

import java.util.List;

import com.napkindrawing.dbversion.Revision;

public interface RevisionsLoader extends Configurable {
    public List<Revision> loadRevisions(String profileName);
    public void setRevisionLoader(RevisionLoader revisionLoader);
}
