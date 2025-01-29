package pl.projekt.store.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import pl.projekt.store.model.Users;
import pl.projekt.store.repository.UsersRepository;

@Configuration
public class SecurityConfig {

    private final UsersRepository usersRepository;

    public SecurityConfig(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Na potrzeby uproszczenia zrezygnowano z losowego tokena CSRF zabezpieczajacego przed atakami (każdy token tworzony jest na podstawie nazwy uzytkownika i hasla)
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOrigins(List.of("*"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(false);
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/*").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // * Publiczny dostęp do dokumentacji API
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/api/private/admin/**").hasRole("ADMIN") // Dostęp tylko dla roli ADMIN
                .requestMatchers("/api/private/users/**").hasAnyRole("USER", "ADMIN") // Dostęp dla USER i ADMIN
                .anyRequest().authenticated() //* Wymaganie logowania dla wszystkich innych zasobów
            ).httpBasic(withDefaults()); //* Podstawowe okno logowania
           /*.formLogin(form -> form //* Formularz logowania generowany przez Springa lub własny
             .permitAll() // Publiczny dostęp do logowania
        )
             .logout(logout -> logout
             .logoutSuccessUrl("/") // Powrót na stronę główną po wylogowaniu
             .permitAll()
        ); */

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
/**
    *TODO poniżej zakomentowany kod służy do utworzenia użytkowników na sztywno 
    *TODO tzn. bez pobierania z bazy poświadczeń użytkowników (aby użyć trzeba zakomentować cały blok
    *TODO UserDetailsService userDetailsService() w linijce 86 - 98)**/

    /*     @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    } */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name().toUpperCase())
                .build();
        };
    }
    
}