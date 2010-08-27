package com.napkindrawing.dbversion.loader;

import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;

public interface RevisionLoader extends Configurable {
    public Revision loadRevision(String profileName, Version version);
}
