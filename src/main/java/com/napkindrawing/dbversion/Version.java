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
        return id.isEmpty() ? "[NONE]" : id;
    }
}
