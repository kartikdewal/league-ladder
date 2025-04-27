package dev.kartikdewal.leagueladder.repository;

import dev.kartikdewal.leagueladder.model.Standings;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface StandingsRepository extends R2dbcRepository<Standings, String> {
    Flux<Standings> findAllByLeagueId(String leagueId);
}