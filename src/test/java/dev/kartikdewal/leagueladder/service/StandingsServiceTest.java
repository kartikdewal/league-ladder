package dev.kartikdewal.leagueladder.service;

import dev.kartikdewal.leagueladder.client.LeagueLadderClient;
import dev.kartikdewal.leagueladder.dto.StandingsDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StandingsServiceTest {

    @Mock
    private LeagueLadderClient leagueLadderClient;

    @Mock
    private StandingsRepository standingsRepository;

    @InjectMocks
    private StandingsService standingsService;

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

        StepVerifier.create(standingsService.getStandings(leagueId))
                .expectNext(cachedStanding)
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verifyNoInteractions(leagueLadderClient);
    }

    @Test
    void testGetStandings_WhenCacheIsInvalid() {
        String leagueId = "149";
        Standings cachedStanding = new Standings();
        cachedStanding.setLeagueId(leagueId);
        cachedStanding.setLastUpdated(OffsetDateTime.now().minusHours(2).toString());

        StandingsDto fetchedStanding = new StandingsDto();
        fetchedStanding.setLeagueId(leagueId);
        fetchedStanding.setLeagueName("Premier League");
        fetchedStanding.setCountryName("England");
        fetchedStanding.setTeamId("456");
        fetchedStanding.setTeamName("Team A");
        fetchedStanding.setOverallLeaguePosition("1");
        fetchedStanding.setTeamBadge("badge_url");

        when(standingsRepository.findAllByLeagueId(leagueId))
                .thenReturn(Flux.fromIterable(Collections.singletonList(cachedStanding)));
        when(leagueLadderClient.fetchStandings(leagueId))
                .thenReturn(Flux.just(fetchedStanding));
        when(standingsRepository.save(any(Standings.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(standingsService.getStandings(leagueId))
                .assertNext(savedStanding -> {
                    assertEquals(leagueId, savedStanding.getLeagueId());
                    assertEquals("Premier League", savedStanding.getLeagueName());
                    assertEquals("England", savedStanding.getCountryName());
                    assertEquals("456", savedStanding.getTeamId());
                    assertEquals("Team A", savedStanding.getTeamName());
                    assertEquals("1", savedStanding.getOverallLeaguePosition());
                    assertEquals("badge_url", savedStanding.getTeamBadge());
                })
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verify(leagueLadderClient, times(1)).fetchStandings(leagueId);
        verify(standingsRepository, times(1)).save(any(Standings.class));
    }

    @Test
    void testGetStandings_WhenCacheIsEmpty() {
        String leagueId = "123";
        StandingsDto apiResponseDto = new StandingsDto();
        apiResponseDto.setLeagueId(leagueId);

        Standings standings = new Standings();
        standings.setLeagueId(apiResponseDto.getLeagueId());

        when(standingsRepository.findAllByLeagueId(leagueId))
                .thenReturn(Flux.empty());
        when(leagueLadderClient.fetchStandings(leagueId))
                .thenReturn(Flux.just(apiResponseDto));
        when(standingsRepository.save(any(Standings.class)))
                .thenReturn(Mono.just(standings));

        StepVerifier.create(standingsService.getStandings(leagueId))
                .expectNext(standings)
                .verifyComplete();

        verify(standingsRepository, times(1)).findAllByLeagueId(leagueId);
        verify(leagueLadderClient, times(1)).fetchStandings(leagueId);
        verify(standingsRepository, times(1)).save(any(Standings.class));
    }
}