package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.League;
import dev.kartikdewal.leagueladder.service.LeagueService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@RestController
@EnableWebFlux
@RequestMapping("/")
public class LeaguesController {

    private final LeagueService leagueService;

    public LeaguesController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping({"/", "/leagues"})
    public Flux<League> getLeagues(
            @RequestHeader(value = "x-api-version", defaultValue = "1") String apiVersion) {
        return leagueService.getLeagues();
    }
}