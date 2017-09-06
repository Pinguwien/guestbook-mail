package de.codecentric.demo.guestbook;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * registers the Keycloakauthenticationprovider in spring context
     * @param authbuilder SecurityBuilder to build authentications and add details like authproviders etc.
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authbuilder) throws Exception {
        KeycloakAuthenticationProvider keyCloakAuthProvider = keycloakAuthenticationProvider();
        keyCloakAuthProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());

        authbuilder.authenticationProvider(keyCloakAuthProvider);
    }

    /**
     * Sets keycloaks config resolver to use springs application.properties instead of keycloak.json (which is standard)
     * @return
     */
    @Bean
    public KeycloakConfigResolver KeyCloakConfigResolver(){
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * define the session auth strategy.
     * @return concrete implementation of session authentication strategy
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    /**
     * define the actual constraints of the app.
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        super.configure(http);
        http.cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                .and()
                .authorizeRequests()
                .antMatchers("/mail*").hasRole("mail")
                .anyRequest().permitAll();
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
            KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
            KeycloakPreAuthActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
