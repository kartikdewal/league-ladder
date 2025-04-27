package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.model.Standings;
import dev.kartikdewal.leagueladder.repository.StandingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;

class LeagueLadderServiceTest {

    @Mock
    private LeagueLadderClient leagueLadderClient;

    @Mock
    private StandingsRepository standingsRepository;

    @InjectMocks
    private LeagueLadderService leagueLadderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStandings_WhenCacheIsValid() {
        String leagueId = "123";
        Standings cachedStanding = new Standings();
        cachedStanding.setLeagueId(leagueId);
        cachedStanding.setLastUpdated(OffsetDateTime.now().toString());

        when(standingsRepository.findAllByLeagueId(leagueId))
                .thenReturn(Flux.fromIterable(Collections.singletonList(cachedStanding)));

        StepVerifier.create(leagueLadderService.getStandings(leagueId))
                .expectNext(cachedStanding)
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verifyNoInteractions(leagueLadderClient);
    }

    @Test
    void testGetStandings_WhenCacheIsInvalid() {
        String leagueId = "123";
        Standings cachedStanding = new Standings();
        cachedStanding.setLeagueId(leagueId);
        cachedStanding.setLastUpdated(OffsetDateTime.now().minusHours(2).toString());

        Standings fetchedStanding = new Standings();
        fetchedStanding.setLeagueId(leagueId);

        when(standingsRepository.findAllByLeagueId(leagueId))
                .thenReturn(Flux.fromIterable(Collections.singletonList(cachedStanding)));
        when(leagueLadderClient.fetchStandings(leagueId))
                .thenReturn(Flux.just(fetchedStanding));
        when(standingsRepository.save(fetchedStanding))
                .thenReturn(Mono.just(fetchedStanding));

        StepVerifier.create(leagueLadderService.getStandings(leagueId))
                .expectNext(fetchedStanding)
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verify(leagueLadderClient, times(1)).fetchStandings(leagueId);
        verify(standingsRepository, times(1)).save(fetchedStanding);
    }

    @Test
    void testGetStandings_WhenCacheIsEmpty() {
        String leagueId = "123";
        Standings fetchedStanding = new Standings();
        fetchedStanding.setLeagueId(leagueId);

        when(standingsRepository.findAllByLeagueId(leagueId))
                .thenReturn(Flux.empty());
        when(leagueLadderClient.fetchStandings(leagueId))
                .thenReturn(Flux.just(fetchedStanding));
        when(standingsRepository.save(fetchedStanding))
                .thenReturn(Mono.just(fetchedStanding));

        StepVerifier.create(leagueLadderService.getStandings(leagueId))
                .expectNext(fetchedStanding)
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verify(leagueLadderClient, times(1)).fetchStandings(leagueId);
        verify(standingsRepository, times(1)).save(fetchedStanding);
    }
}