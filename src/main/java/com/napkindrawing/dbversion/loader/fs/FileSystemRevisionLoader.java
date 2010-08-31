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
package com.napkindrawing.dbversion.loader.fs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.napkindrawing.dbversion.Revision;
import com.napkindrawing.dbversion.Version;
import com.napkindrawing.dbversion.loader.RevisionLoader;

public class FileSystemRevisionLoader extends FileSystemLoader implements RevisionLoader {

    @Override
    public Revision loadRevision(String profileName, Version version) {
        
        File revisionFile = getRevisionFile(profileName, version);

        Revision revision = new Revision(version);
        
        if(revisionFile.getName().length() > 10) {
            // Strip out version prefix and ".sql" suffix
            revision.setName(revisionFile.getName().substring(6,revisionFile.getName().length()-4));
        }
        
        try {
            revision.setUpgradeScriptTemplate( FileUtils.readFileToString(revisionFile, "UTF-8"));
            revision.assignUpgradeScriptTemplateChecksum();
        } catch (IOException e) {
            throw new RuntimeException("Error reading upgrade script", e);
        }
        
        return revision;
    }
    
    protected File getRevisionFile(String profileName, Version version) {

        File[] versionFiles = getProfileDir(profileName).listFiles(new StartsWithFilenameFilter(version.getId()));
        
        if(versionFiles.length == 0) {
            throw new RuntimeException("Profile '"+profileName+"' does not have version '"+version+"'");
        }
        
        if(!versionFiles[0].isFile() || !versionFiles[0].canRead()) {
            throw new RuntimeException("Version file must be readable file");
        }
        
        return versionFiles[0];
    }
    
}
