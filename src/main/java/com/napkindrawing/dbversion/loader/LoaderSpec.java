package com.napkindrawing.dbversion.loader;

public interface LoaderSpec {
    public Class<? extends ProfilesLoader>getProfilesLoaderClass();
    public Class<? extends ProfileLoader>getProfileLoaderClass();
    public Class<? extends RevisionsLoader>getRevisionsLoaderClass();
    public Class<? extends RevisionLoader>getRevisionLoaderClass();
}
