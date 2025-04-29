package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.service.StandingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@RestController
@EnableWebFlux
@RequestMapping("/v{apiVersion}/standings")
public class StandingsController {

    private final StandingsService standingsService;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping("/{leagueId}")
    public Flux<Standings> getStandings(
            @PathVariable(required = false) String apiVersion,
            @PathVariable String leagueId) {
        if (leagueId == null || leagueId.isBlank()) {
            throw new IllegalArgumentException("LeagueId must not be null or blank");
        }
        try {
            Integer.parseInt(leagueId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("LeagueId must be a valid number");
        }
        return standingsService.getStandings(leagueId);
    }
}