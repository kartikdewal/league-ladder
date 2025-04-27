package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.repository.StandingsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeagueLadderService {

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
        // Assuming all standings have the same timestamp, check the first entry
        LocalDateTime lastUpdated = cachedStandings.getFirst().getLastUpdated();
        return lastUpdated != null && lastUpdated.isAfter(LocalDateTime.now().minusHours(1));
    }

    private Flux<Standings> fetchAndCacheStandings(String leagueId) {
        return leagueLadderClient.fetchStandings(leagueId)
                .map(standing -> {
                    standing.setLastUpdated(LocalDateTime.now());
                    return standing;
                })
                .flatMap(standingsRepo::save);
    }
}