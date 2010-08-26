package com.napkindrawing.dbversion.loader;

import com.napkindrawing.dbversion.Revision;

public interface RevisionLoader extends Configurable {
    public Revision loadRevision(String profileName, String version);
}
