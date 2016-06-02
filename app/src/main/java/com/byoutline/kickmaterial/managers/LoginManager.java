package com.byoutline.kickmaterial.managers;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.kickmaterial.api.KickMaterialService;
import com.byoutline.kickmaterial.dagger.GlobalScope;
import com.byoutline.kickmaterial.events.AccessTokenFetchedEvent;
import com.byoutline.kickmaterial.events.AccessTokenFetchingFailedEvent;
import com.byoutline.kickmaterial.model.AccessToken;
import com.byoutline.kickmaterial.model.EmailAndPass;
import com.byoutline.ottocachedfield.OttoCachedFieldWithArg;
import com.byoutline.ottocachedfield.OttoCachedFieldWithArgBuilder;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit2.Call;

import static com.byoutline.observablecachedfield.RetrofitHelper.apiValueProv;

/**
 * Created by Sebastian Kacprzak <sebastian.kacprzak at byoutline.com> on 31.03.15.
 */
@GlobalScope
public class LoginManager {
    private CachedFieldWithArg<AccessToken, EmailAndPass> accessToken;
    private final AccessTokenProvider accessTokenProvider;

    @Inject
    public LoginManager(KickMaterialService service, AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
        accessToken = new OttoCachedFieldWithArgBuilder<AccessToken, EmailAndPass>()
                // INote: 5/23/16  use func program is a little hard to understand
                .withValueProvider(apiValueProv(service::postGetAccessToken))
                .withSuccessEvent(new AccessTokenFetchedEvent())
                .withResponseErrorEvent(new AccessTokenFetchingFailedEvent())
                .withCustomSessionIdProvider(() -> "") // should be valid between sessions
                .build();

        accessToken = new OttoCachedFieldWithArgBuilder<AccessToken, EmailAndPass>()
                .withValueProvider(apiValueProv(new ProviderWithArg<Call<AccessToken>, EmailAndPass>() {
                    @Override
                    public Call<AccessToken> get(EmailAndPass arg) {
                        return service.postGetAccessToken(arg);
                    }
                })).withSuccessEvent(new AccessTokenFetchedEvent())
                .withResponseErrorEvent(new AccessTokenFetchingFailedEvent())
                .withCustomSessionIdProvider(new Provider<String>() {
                    @Override
                    public String get() {
                        return "";
                    }
                }).build();
    }

    public void logIn(EmailAndPass emailAndPass) {
        accessToken.postValue(emailAndPass);
    }

    public void logOff() {
        accessTokenProvider.set("");
        accessToken.drop();
    }

    @Subscribe
    public void onAccessTokenFetched(AccessTokenFetchedEvent event) {
        accessTokenProvider.set(event.getResponse().accessToken);
    }
}
