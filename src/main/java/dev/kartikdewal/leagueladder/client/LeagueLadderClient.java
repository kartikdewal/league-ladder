package dev.kartikdewal.leagueladder.client;

import dev.kartikdewal.leagueladder.model.Standings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class LeagueLadderClient {

    private final WebClient webClient;

    @Value("${api.key}")
    private String apiKey;

    public LeagueLadderClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Standings> fetchStandings(String leagueId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("action", "get_standings")
                        .queryParam("league_id", leagueId)
                        .queryParam("APIkey", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(Standings.class);
    }
}