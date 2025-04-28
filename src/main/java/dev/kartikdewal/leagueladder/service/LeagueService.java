package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.model.League;
import dev.kartikdewal.leagueladder.repository.LeagueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LeagueService {
    private static final Logger logger = LoggerFactory.getLogger(LeagueService.class);
    private final LeagueLadderClient leagueLadderClient;
    private final LeagueRepository leagueRepo;

    public LeagueService(LeagueLadderClient leagueLadderClient, LeagueRepository leagueRepository) {
        this.leagueLadderClient = leagueLadderClient;
        this.leagueRepo = leagueRepository;
    }

    public Flux<League> getLeagues() {
        try {
            return leagueRepo.findAll()
                    .collectList()
                    .flatMapMany(cachedLeagues -> {
                        if (cachedLeagues.isEmpty()) {
                            return fetchAndCacheLeagues();
                        }

                        if (isCacheValid(cachedLeagues)) {
                            logger.info("Returning cached leagues");
                            return Flux.fromIterable(cachedLeagues);
                        }

                        return fetchAndCacheLeagues();
                    })
                    .onErrorResume(e -> {
                        logger.warn("Returning empty leagues due to error: {}", e.getMessage());
                        return Flux.empty();
                    });
        } catch (Exception e) {
            logger.error("Unexpected error in getLeague: {}", e.getMessage());
            return Flux.error(e);
        }
    }

    private boolean isCacheValid(List<League> cachedLeagues) {
        try {
            String lastUpdatedString = cachedLeagues.getFirst().getLastUpdated();
            if (lastUpdatedString == null) {
                return false;
            }

            OffsetDateTime lastUpdated = OffsetDateTime.parse(lastUpdatedString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return lastUpdated.isAfter(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1));
        } catch (Exception e) {
            logger.error("Error validating cache: {}", e.getMessage());
            return false;
        }
    }

    private Flux<League> fetchAndCacheLeagues() {
        return leagueLadderClient.fetchLeagues()
                .flatMap(league -> {
                    League l = new League();
                    l.setLeagueId(league.getLeagueId());
                    l.setLeagueName(league.getLeagueName());
                    l.setLeagueSeason(league.getLeagueSeason());
                    l.setCountryId(league.getCountryId());
                    l.setCountryName(league.getCountryName());
                    l.setLeagueLogo(league.getLeagueLogo());
                    l.setCountryLogo(league.getCountryLogo());
                    l.setLastUpdated();
                    return leagueRepo.save(l)
                            .doOnNext(savedLeague -> logger.info("Saved league: {}", savedLeague.getLeagueId()))
                            .doOnError(e -> logger.error("Error saving league: {}", e.getMessage()));
                })
                .onErrorResume(e -> {
                    logger.error("Failed to process league: {}", e.getMessage());
                    return leagueRepo.findAll()
                            .timeout(Duration.ofSeconds(20))
                            .doOnSubscribe(s -> logger.warn("Falling back to cached leagues"));
                });
    }
}