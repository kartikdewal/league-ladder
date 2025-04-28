package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.repository.StandingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StandingsService {
    private static final Logger logger = LoggerFactory.getLogger(StandingsService.class);
    private final LeagueLadderClient leagueLadderClient;
    private final StandingsRepository standingsRepo;

    public StandingsService(LeagueLadderClient leagueLadderClient, StandingsRepository standingsRepository) {
        this.leagueLadderClient = leagueLadderClient;
        this.standingsRepo = standingsRepository;
    }

    public Flux<Standings> getStandings(String leagueId) {
        try {
            return standingsRepo.findAllByLeagueId(leagueId)
                    .collectList()
                    .flatMapMany(cachedStandings -> {
                        if (cachedStandings.isEmpty()) {
                            return fetchAndCacheStandings(leagueId);
                        }

                        if (isCacheValid(cachedStandings)) {
                            logger.info("Returning cached standings for leagueId: {}", leagueId);
                            return Flux.fromIterable(cachedStandings);
                        }

                        return fetchAndCacheStandings(leagueId);
                    })
                    .onErrorResume(e -> {
                        logger.warn("Returning empty standings due to error: {}", e.getMessage());
                        return Flux.empty();
                    });
        } catch (Exception e) {
            logger.error("Unexpected error in getStandings: {}", e.getMessage());
            return Flux.error(e);
        }
    }

    private boolean isCacheValid(List<Standings> cachedStandings) {
        try {
            String lastUpdatedString = cachedStandings.getFirst().getLastUpdated();
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

    private Flux<Standings> fetchAndCacheStandings(String leagueId) {
        try {
            return leagueLadderClient.fetchStandings(leagueId)
                    .flatMap(standing -> {
                        Standings s = new Standings();
                        s.setLeagueId(leagueId);
                        s.setLeagueName(standing.getLeagueName());
                        s.setCountryName(standing.getCountryName());
                        s.setTeamId(standing.getTeamId());
                        s.setTeamName(standing.getTeamName());
                        s.setOverallLeaguePosition(standing.getOverallLeaguePosition());
                        s.setTeamBadge(standing.getTeamBadge());
                        s.setLastUpdated(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

                        return standingsRepo.save(s)
                                .doOnNext(savedStanding -> logger.debug("Saved standings for team: {}", savedStanding.getTeamId()))
                                .doOnError(error -> logger.error("Error saving standings: {}", error.getMessage()));
                    })
                    .onErrorResume(e -> {
                        logger.warn("Returning empty standings due to error: {}", e.getMessage());
                        return Flux.empty();
                    });
        } catch (Exception e) {
            logger.error("Unexpected error in fetchAndCacheStandings: {}", e.getMessage());
            return Flux.error(e);
        }
    }
}