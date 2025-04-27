package dev.kartikdewal.leagueladder.client;

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
    public Flux<Standings> fetchStandings(String leagueId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("action", "get_standings")
                        .queryParam("league_id", leagueId)
                        .queryParam("APIkey", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(Standings.class)
                .timeout(Duration.ofSeconds(2)) // Ensure timeout is applied
                .doOnError(e -> logger.error("Error fetching standings for leagueId {}: {}", leagueId, e.getMessage()));
    }

    private Flux<Standings> fallbackFetchStandings(String leagueId, Throwable throwable) {
        logger.warn("Fallback triggered for leagueId {}: {}", leagueId, throwable.getMessage());
        return standingsRepo.findAllByLeagueId(leagueId);
    }
}