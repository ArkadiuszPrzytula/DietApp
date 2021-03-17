package com.pl.przytula.dietapp.security.oauth2;

import com.pl.przytula.dietapp.config.AppAuthProperties;
import com.pl.przytula.dietapp.exception.BadRequestException;
import com.pl.przytula.dietapp.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.pl.przytula.dietapp.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private TokenJWTProvider tokenJWTProvider;
    private AppAuthProperties appAuthProperties;
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenJWTProvider, AppAuthProperties appAuthProperties, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenJWTProvider = tokenJWTProvider;
        this.appAuthProperties = appAuthProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookieByName(request, REDIRECT_URI_PARAM_COOKIE_NAME) // REDIRECT_URI_PARAM_COOKIE_NAME is constant parameter from HttpCookieOAuth2AuthorizationRequestRepository
                .map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) { // check if retrieve from cookies uri exist and is match with known uri
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        String token = tokenJWTProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl).queryParam("token", token).build().toUriString();


    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appAuthProperties.getOAuth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(p -> {
                    URI authURI = URI.create(p);
                    return authURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authURI.getPort() == clientRedirectUri.getPort();
                });
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
