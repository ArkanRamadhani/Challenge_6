package com.ch6.challenge_6.auth;

import com.ch6.challenge_6.token.Token;
import com.ch6.challenge_6.token.TokenRepository;
import com.ch6.challenge_6.token.TokenType;
import com.ch6.challenge_6.user.User;
import com.ch6.challenge_6.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private String jwtToken;
    private Token token;

    @BeforeEach
    public void setUp() {
        user = new User();
        jwtToken = "jwtToken";
        token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    @Test
    public void testSaveUserTokenWhenUserAndJwtTokenAreValidThenSaveToken() {
        authenticationService.saveUserToken(user, jwtToken);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    public void testRevokeAllUserTokensWhenUserHasValidTokensThenRevokeTokens() {
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Arrays.asList(token));
        authenticationService.revokeAllUserTokens(user);
        verify(tokenRepository, times(1)).saveAll(Arrays.asList(token));
    }

    @Test
    public void testRevokeAllUserTokensWhenUserDoesNotHaveValidTokensThenDoNotRevokeTokens() {
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Collections.emptyList());
        authenticationService.revokeAllUserTokens(user);
        verify(tokenRepository, times(0)).saveAll(any());
    }
}