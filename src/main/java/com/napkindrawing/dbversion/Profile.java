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
package com.napkindrawing.dbversion;

import java.util.SortedSet;
import java.util.TreeSet;

public class Profile {
    private String name;
    private SortedSet<Revision> revisions = new TreeSet<Revision>();
    private SortedSet<InstalledRevision> installedRevisions = new TreeSet<InstalledRevision>();
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public SortedSet<Revision> getRevisions() {
        return revisions;
    }
    public void setRevisions(SortedSet<Revision> revisions) {
        this.revisions = revisions;
    }
    public SortedSet<InstalledRevision> getInstalledRevisions() {
        return installedRevisions;
    }
    public void setInstalledRevisions(
            SortedSet<InstalledRevision> installedRevisions) {
        this.installedRevisions = installedRevisions;
    }
    public InstalledRevision getInstalledRevision(Version v) {
        for(InstalledRevision i : installedRevisions) {
            if(i.getVersion().equals(v)) {
                return i;
            }
        }
        return null;
    }
}
