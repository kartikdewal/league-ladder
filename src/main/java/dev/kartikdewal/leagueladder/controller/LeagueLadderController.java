package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.service.LeagueLadderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@RestController
@EnableWebFlux
public class LeagueLadderController {

    private final LeagueLadderService leagueLadderService;

    public LeagueLadderController(LeagueLadderService leagueLadderService) {
        this.leagueLadderService = leagueLadderService;
    }

    @GetMapping("/standings")
    public Flux<Standings> getStandings(@RequestParam String leagueId) {
        return leagueLadderService.getStandings(leagueId);
    }
}