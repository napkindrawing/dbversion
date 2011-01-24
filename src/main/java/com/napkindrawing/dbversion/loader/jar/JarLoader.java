/*
 * Copyright 2010 NapkinDrawing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.napkindrawing.dbversion.loader.jar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

import com.napkindrawing.dbversion.loader.Configurable;
import com.napkindrawing.dbversion.loader.LoaderSpec;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.ProfilesLoader;
import com.napkindrawing.dbversion.loader.RevisionLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;

/**
 * <p>
 * Loads all SQL files from a JAR file stored somewhere in the classpath.
 * Note that this is *not* a JAR file *IN* the classpath, but a JAR file
 * inside some JAR file, so it's accessible via the old standard: 
 * </p>
 * <code>Class.getClassLoader().getResourceAsStream("/path/to/some/sql.jar")</code>
 * @author walkers
 *
 */
public class JarLoader implements Configurable, LoaderSpec {

    String jarPath;
    
    @Override
    public void configure(Properties properties) {
        jarPath = properties.getProperty("jar.path");
    }

    public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}

	@Override
    public Class<? extends ProfilesLoader> getProfilesLoaderClass() {
        return JarProfilesLoader.class;
    }

    @Override
    public Class<? extends ProfileLoader> getProfileLoaderClass() {
        return JarProfileLoader.class;
    }

    @Override
    public Class<? extends RevisionsLoader> getRevisionsLoaderClass() {
        return JarRevisionsLoader.class;
    }

    @Override
    public Class<? extends RevisionLoader> getRevisionLoaderClass() {
        return JarRevisionLoader.class;
    }
}
