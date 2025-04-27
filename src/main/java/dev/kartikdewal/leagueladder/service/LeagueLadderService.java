package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.model.Standing;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LeagueLadderService {

    private final LeagueLadderClient leagueLadderClient;

    public LeagueLadderService(LeagueLadderClient leagueLadderClient) {
        this.leagueLadderClient = leagueLadderClient;
    }

    public Mono<List<Standing>> getStandings(String leagueId) {
        return leagueLadderClient.fetchStandings(leagueId);
    }
}