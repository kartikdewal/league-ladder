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
        return standingsRepo.findAllByLeagueId(leagueId)
                .collectList()
                .flatMapMany(cachedStandings -> {
                    if (cachedStandings.isEmpty()) {
                        return fetchAndCacheStandings(leagueId);
                    }

                    if (isCacheValid(cachedStandings)) {
                        return Flux.fromIterable(cachedStandings);
                    }

                    return fetchAndCacheStandings(leagueId);
                });
    }

    private boolean isCacheValid(List<Standings> cachedStandings) {
        String lastUpdatedString = cachedStandings.get(0).getLastUpdated();
        if (lastUpdatedString == null) {
            return false;
        }

        OffsetDateTime lastUpdated = OffsetDateTime.parse(lastUpdatedString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return lastUpdated.isAfter(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1));
    }

    private Flux<Standings> fetchAndCacheStandings(String leagueId) {
        String rfc3339Timestamp = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return leagueLadderClient.fetchStandings(leagueId)
                .map(standing -> {
                    standing.setLastUpdated(rfc3339Timestamp);
                    return standing;
                })
                .flatMap(standingsRepo::save);
    }
}