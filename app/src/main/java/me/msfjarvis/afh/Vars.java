package me.msfjarvis.afh;


public class Vars {
    private static final String didEndpoint = "https://www.androidfilehost.com/api/?action=developers&did=%s&limit=100";
    private static final String flidEndpoint = "https://www.androidfilehost.com/api/?action=folder&flid=%s";

    public String getDidEndpoint(){
        return didEndpoint;
    }

    public String getFlidEndpoint(){
        return flidEndpoint;
    }
}
