package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.League;
import dev.kartikdewal.leagueladder.service.LeagueService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@EnableWebFlux
@RequestMapping({"/", "/v1"})
public class LeaguesController {

    private final LeagueService leagueService;

    public LeaguesController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping({"/", "/v{apiVersion}", "/leagues"})
    public Flux<League> getLeagues(
            @PathVariable(required = false) String apiVersion) {
        if (apiVersion == null || apiVersion.isBlank()) {
            apiVersion = "1";
        }
        if (!Objects.equals(apiVersion, "1")) {
            throw new IllegalArgumentException("API version " + apiVersion + " is not supported");
        }
        return leagueService.getLeagues();
    }
}