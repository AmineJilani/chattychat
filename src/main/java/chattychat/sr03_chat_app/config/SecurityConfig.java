package chattychat.sr03_chat_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   // Bean utilisé pour le hachage des mots de passe (BCrypt)
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   // Désactive la sécurité automatique de Spring Security.
   // On garde uniquement BCrypt, l'authentification reste gérée
   // manuellement via HttpSession dans nos propres controllers.
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .authorizeHttpRequests(auth -> auth
               .anyRequest().permitAll()
           )
           .csrf(AbstractHttpConfigurer::disable)
           .formLogin(AbstractHttpConfigurer::disable)
           .httpBasic(AbstractHttpConfigurer::disable);

       return http.build();
   }
}