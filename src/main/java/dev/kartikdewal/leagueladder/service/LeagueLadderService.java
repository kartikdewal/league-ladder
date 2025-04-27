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
public class LeagueLadderService {
    private static final Logger logger = LoggerFactory.getLogger(LeagueLadderService.class);
    private final LeagueLadderClient leagueLadderClient;
    private final StandingsRepository standingsRepo;

    public LeagueLadderService(LeagueLadderClient leagueLadderClient, StandingsRepository standingsRepository) {
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
            String rfc3339Timestamp = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return leagueLadderClient.fetchStandings(leagueId)
                    .map(standing -> {
                        standing.setLastUpdated(rfc3339Timestamp);
                        return standing;
                    })
                    .flatMap(standingsRepo::save)
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