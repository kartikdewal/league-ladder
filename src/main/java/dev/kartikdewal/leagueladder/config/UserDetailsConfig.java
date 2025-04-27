package dev.kartikdewal.leagueladder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class UserDetailsConfig {

    @Value("${user.username}")
    private String userUsername;

    @Value("${user.password}")
    private String userPassword;

    @Value("${user.roles}")
    private String userRoles;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(userUsername)
                .password("{noop}" + userPassword)
                .roles(userRoles.split(","))
                .build();

        return new MapReactiveUserDetailsService(user);
    }
}