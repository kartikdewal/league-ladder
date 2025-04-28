package dev.kartikdewal.leagueladder.client;

import dev.kartikdewal.leagueladder.dto.LeagueDto;
import dev.kartikdewal.leagueladder.dto.StandingsDto;
import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.repository.StandingsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
public class LeagueLadderClient {
    private static final Logger logger = LoggerFactory.getLogger(LeagueLadderClient.class);
    private final WebClient webClient;
    private final StandingsRepository standingsRepo;

    @Value("${api.key}")
    private String apiKey;

    public LeagueLadderClient(WebClient webClient, StandingsRepository standingsRepo) {
        this.webClient = webClient;
        this.standingsRepo = standingsRepo;
    }

    @CircuitBreaker(name = "leagueLadderClient", fallbackMethod = "fallbackFetchStandings")
    @TimeLimiter(name = "leagueLadderClient")
    public Flux<StandingsDto> fetchStandings(String leagueId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("action", "get_standings")
                        .queryParam("league_id", leagueId)
                        .queryParam("APIkey", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(StandingsDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnComplete(() -> logger.info("Successfully fetched standings from the API for leagueId: {}", leagueId))
                .doOnError(e -> logger.error("Error fetching standings for leagueId {}: {}", leagueId, e.getMessage()));
    }

    private Flux<Standings> fallbackFetchStandings(String leagueId, Throwable throwable) {
        logger.warn("Fallback triggered for leagueId {}: {}", leagueId, throwable.getMessage());
        return standingsRepo.findAllByLeagueId(leagueId)
                .doOnError(e -> logger.error("Error fetching cached standings for leagueId {}: {}", leagueId, e.getMessage()))
                .onErrorResume(e -> {
                    logger.warn("Returning empty standings due to error: {}", e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<LeagueDto> fetchLeagues() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("action", "get_leagues")
                        .queryParam("APIkey", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(LeagueDto.class)
                .doOnComplete(() -> logger.info("Successfully fetched leagues from the API"))
                .doOnError(e -> logger.error("Error fetching leagues: {}", e.getMessage()));
    }
}