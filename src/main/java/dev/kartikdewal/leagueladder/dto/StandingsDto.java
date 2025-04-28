package dev.kartikdewal.leagueladder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsDto {
    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("league_id")
    private String leagueId;

    @JsonProperty("league_name")
    private String leagueName;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("overall_league_position")
    private String overallLeaguePosition;

    @JsonProperty("team_badge")
    private String teamBadge;

    public String getCountryName() {
        return countryName;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getOverallLeaguePosition() {
        return overallLeaguePosition;
    }

    public String getTeamBadge() {
        return teamBadge;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setOverallLeaguePosition(String overallLeaguePosition) {
        this.overallLeaguePosition = overallLeaguePosition;
    }

    public void setTeamBadge(String teamBadge) {
        this.teamBadge = teamBadge;
    }
}
