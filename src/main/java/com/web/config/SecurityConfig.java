package com.web.config;


import com.web.oauth2.CustomOAuth2Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.web.domain.enums.SocialType.FACEBOOK;
import static com.web.domain.enums.SocialType.GOOGLE;
import static com.web.domain.enums.SocialType.KAKAO;

@Configuration
@EnableWebSecurity
//@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
                .authorizeRequests()
                    // 요청 패턴을 리스트 형식으로 설정
                    .antMatchers("/", "/oauth2/**", "/login/**", "/css/**","/images/**","/js/**","/console/**").permitAll()
                    .antMatchers("/facebook").hasAuthority(FACEBOOK.getRoleType())
                    .antMatchers("/google").hasAuthority(GOOGLE.getRoleType())
                    .antMatchers("/kakao").hasAuthority(KAKAO.getRoleType())
                    .anyRequest().authenticated()
                //oauth2 추가
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl("/loginSuccess")
                    .failureUrl("/loginFailure")
                .and()
                    /*
                    *  headers() : 응답에 해당하는 header를 설정, 설정하지 않으면 디폴트 값으로 설정
                    *  frameOptions().disable() : XframeOptionsHeaderWriter의 최적화 설정을 허용 안함
                    * */
                    .headers().frameOptions().disable()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                    .formLogin()
                    .successForwardUrl("/board/list")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                .and()
                    .addFilterBefore(filter, CsrfFilter.class)
                    //.addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
                    .csrf().disable();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties,
                                                                     @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요 없는 값이지만 null이면 실행이 안되므로 임시값을 넣음
                .jwkSetUri("test") //필요 없는 값이지만 null이면 실행이 안되므로 임시값을 넣음
                .build());

        return new InMemoryClientRegistrationRepository(registrations);
    }


    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client){
        if("google".equals(client)){
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }


        if("facebook".equals(client)){
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .userInfoUri("https://graph.facebook.com/me?fileds=id,name,email,link")
                    .scope("email")
                    .build();
        }
        return null;
    }


    /**
     * oauth2 적용 이하 코드 필요 없음.
     **/

//    @Bean
//    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(filter);
//        registration.setOrder(-100);
//
//        return registration;
//    }

    //각 소셜 미디어 타입을 받아 필터 설정
//    private Filter oauth2Filter() {
//        CompositeFilter filter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//        filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
//        filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
//        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
//        filter.setFilters(filters);
//        return filter;
//    }


    //소셜 미디어 필터를 리스트 형식으로 한꺼번에 반환
//    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType){
//        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
//        filter.setRestTemplate(template);
//        filter.setTokenServices(new UserTokenServices(client, socialType));
//        filter.setAuthenticationFailureHandler(((request, response, exception) -> response.sendRedirect("/" + socialType.getValue() + "/complete")));
//        filter.setAuthenticationFailureHandler(((request, response, exception) -> response.sendRedirect("/error")));
//
//        return filter;
//    }


    /**
    * 각 소셜 미디어 리소스 정보를 빈으로 등록
    **/
//    @Bean
//    @ConfigurationProperties("facebook")
//    public ClientResources facebook(){
//        return new ClientResources();
//    }
//
//    @Bean
//    @ConfigurationProperties("google")
//    public ClientResources google(){
//        return new ClientResources();
//    }
//
//    @Bean
//    @ConfigurationProperties("kakao")
//    public ClientResources kakao(){
//        return new ClientResources();
//    }
}
