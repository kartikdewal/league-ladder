package dev.kartikdewal.leagueladder.repository;

import dev.kartikdewal.leagueladder.model.League;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface LeagueRepository extends R2dbcRepository<League, String> {
    Mono<League> findByLeagueId(String leagueId);
}