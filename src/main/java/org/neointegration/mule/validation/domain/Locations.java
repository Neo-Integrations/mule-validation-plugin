package org.neointegration.mule.validation.domain;

public class Locations {
    private String inlcudeFileNamePattern;
    private String excludeFileNamePattern;
    private String path;


    public String getInlcudeFileNamePattern() {
        return inlcudeFileNamePattern;
    }

    public void setInlcudeFileNamePattern(String inlcudeFileNamePattern) {
        this.inlcudeFileNamePattern = inlcudeFileNamePattern;
    }

    public String getExcludeFileNamePattern() {
        return excludeFileNamePattern;
    }

    public void setExcludeFileNamePattern(String excludeFileNamePattern) {
        this.excludeFileNamePattern = excludeFileNamePattern;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Locations{" +
                "inlcudeFileNamePattern='" + inlcudeFileNamePattern + '\'' +
                ", excludeFileNamePattern='" + excludeFileNamePattern + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
