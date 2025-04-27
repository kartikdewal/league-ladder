package dev.kartikdewal.leagueladder.controller;

import dev.kartikdewal.leagueladder.model.Standing;
import dev.kartikdewal.leagueladder.service.LeagueLadderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class LeagueLadderController {

    private final LeagueLadderService leagueLadderService;

    public LeagueLadderController(LeagueLadderService leagueLadderService) {
        this.leagueLadderService = leagueLadderService;
    }

    @GetMapping("/standings")
    public Mono<List<Standing>> getStandings(@RequestParam String leagueId) {
        return leagueLadderService.getStandings(leagueId);
    }
}