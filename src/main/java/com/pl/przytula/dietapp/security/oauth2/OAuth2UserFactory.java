package com.pl.przytula.dietapp.security.oauth2;

import com.pl.przytula.dietapp.exception.OAuth2AuthenticationProcessingException;
import com.pl.przytula.dietapp.model.AuthProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserFactory {

    //todo add other authentication by social like github or google accounts
    public static OAuth2UserInfo getOAuthUserInfo(String registrationId, Map<String,Object> attributes) throws OAuth2AuthenticationProcessingException {
        if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())){
            return new FacebookOAuth2UserInfo(attributes);
        }


        else {

            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported.");
        }

    }
}
