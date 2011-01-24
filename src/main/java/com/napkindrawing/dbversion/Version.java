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

public class Version implements Comparable<Version> {
    
    private String id;
    
    public static final Version NONE = new Version();
    
    private Version() {
        id = "";
    }
    
    public Version(String id) {
        setId(id);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        if(id == null || id.isEmpty()) {
            throw new RuntimeException("Version id may not be null or empty");
        }
        this.id = id;
    }
    public boolean equals(Object o) {
        return id == null
             ? o == null
             : id.equals(((Version) o).getId());
    }
    public int compareTo(Version v) {
        return id.compareTo(v.getId());
    }
    public int hashCode() {
        return id.hashCode();
    }
    public String toString() {
        return id.isEmpty() ? "[N/A]" : id;
    }
}
