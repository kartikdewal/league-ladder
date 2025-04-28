CREATE TABLE IF NOT EXISTS league (
    league_id VARCHAR(255),
    league_name VARCHAR(255),
    league_season VARCHAR(255),
    country_id VARCHAR(255),
    country_name VARCHAR(255),
    league_logo VARCHAR(255),
    country_logo VARCHAR(255),
    last_updated VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS standings (
    league_id VARCHAR(255),
    league_name VARCHAR(255),
    team_id VARCHAR(255),
    team_name VARCHAR(255),
    team_badge VARCHAR(255),
    country_name VARCHAR(255),
    overall_league_position VARCHAR(255),
    last_updated VARCHAR(255)
    );