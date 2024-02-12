package org.mmc.implementations;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@AllArgsConstructor
public class UserAccessTokenCredential implements TokenCredential {

    private final String accessToken;
    private final OffsetDateTime expiresOn;

    @Override
    public Mono<AccessToken> getToken(TokenRequestContext request) {
        return Mono.just(new AccessToken(accessToken, expiresOn));
    }
}
