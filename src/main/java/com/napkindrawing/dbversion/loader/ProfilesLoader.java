package com.napkindrawing.dbversion.loader;

import java.util.List;

import com.napkindrawing.dbversion.Profile;

public interface ProfilesLoader extends Configurable {
    public List<Profile> loadProfiles();
    public void setProfileLoader(ProfileLoader profileLoader);
}
