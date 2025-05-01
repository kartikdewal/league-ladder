package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.League;
import dev.kartikdewal.leagueladder.service.LeagueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@RestController
@EnableWebFlux
@RequestMapping({"/v1/leagues"})
public class LeaguesController {

    private final LeagueService leagueService;

    public LeaguesController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public Flux<League> getLeagues() {
        return leagueService.getLeagues();
    }
}