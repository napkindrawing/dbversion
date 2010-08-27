package com.napkindrawing.dbversion;

public class Version implements Comparable<Version> {
    private String id;
    public Version(String id) {
        setId(id);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean equals(Object o) {
        return id == null
             ? o == null
             : id.equals(o);
    }
    public int compareTo(Version v) {
        return id.compareTo(v.getId());
    }
    public int hashCode() {
        return id.hashCode();
    }
    public String toString() {
        return id;
    }
}
