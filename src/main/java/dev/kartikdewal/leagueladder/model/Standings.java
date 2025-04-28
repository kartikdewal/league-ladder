package dev.kartikdewal.leagueladder.model;

import org.springframework.data.relational.core.mapping.Table;

@Table(name = "standings")
public class Standings {
    private String countryName;
    private String leagueId;
    private String leagueName;
    private String teamId;
    private String teamName;
    private String overallLeaguePosition;
    private String teamBadge;
    private String lastUpdated;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getOverallLeaguePosition() {
        return overallLeaguePosition;
    }

    public void setOverallLeaguePosition(String overallLeaguePosition) {
        this.overallLeaguePosition = overallLeaguePosition;
    }

    public String getTeamBadge() {
        return teamBadge;
    }

    public void setTeamBadge(String teamBadge) {
        this.teamBadge = teamBadge;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}