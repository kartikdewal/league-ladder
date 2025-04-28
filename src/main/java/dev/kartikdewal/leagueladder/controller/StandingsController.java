package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.service.StandingsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@RestController
@EnableWebFlux
@RequestMapping("/standings")
public class StandingsController {

    private final StandingsService standingsService;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping("/{leagueId}")
    public Flux<Standings> getStandings(
            @RequestHeader(value = "x-api-version", defaultValue = "1") String apiVersion,
            @PathVariable String leagueId) {
        return standingsService.getStandings(leagueId);
    }
}