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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tools.ant.BuildException;

import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;
import com.napkindrawing.dbversion.loader.RevisionLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;

public class JarRevisionsLoader extends JarLoader implements RevisionsLoader {
    
    private RevisionLoader revisionLoader;
    
    @Override
    public SortedSet<Revision> loadRevisions(String profileName) {

        SortedSet<Revision> revisions = new TreeSet<Revision>();
        
        /*
        File profileDir = getProfileDir(profileName);

        FilenameFilter sqlFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        };
        List<String> versions = new ArrayList<String>();
        for(File revFile : profileDir.listFiles(sqlFileFilter)) {
            String version = revFile.getName().substring(0, 5);
            if(versions.contains(version)) {
                throw new BuildException("Multiple resources detected for version " + version + " of profile " + profileName);
            }
            Revision revision = revisionLoader.loadRevision(profileName, new Version(version));
            revisions.add(revision);
            versions.add(version);
        }
        
        */
        
        return revisions;
    }

    public void setRevisionLoader(RevisionLoader revisionLoader) {
        this.revisionLoader = revisionLoader;
    }
}
