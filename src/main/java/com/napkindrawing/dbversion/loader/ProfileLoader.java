package com.napkindrawing.dbversion.loader;

import com.napkindrawing.dbversion.Profile;

public interface ProfileLoader extends Configurable {
    public Profile loadProfile(String name);
    public void setRevisionsLoader(RevisionsLoader revisionsLoader);
}
