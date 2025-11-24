package com.ufpr.equilibrium.data.network;

/**
 * Adds Authorization header if a token is available from the provider.
 */
@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000\u001E\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0012\u0001\u0000\u0018\u0000B\u000F\u0008\u0007\u0012\u0004\u0010\u0002(\u0001\u00A2\u0006\u0004\u0008\u0004\u0010\u0005J\u000C\u0010\u00062\u0004\u0010\u0008(\u00038\u0002H\u0016R\u000C\u0010\u0002H\u0001X\u0082\u0004\u00A2\u0006\u0002\n\u0000\u00F2\u0001\u0010\n\u00020\u0001\n\u00020\u0003\n\u00020\u0007\n\u00020\t\u00A8\u0006\n"}, d2 = {"Lcom/ufpr/equilibrium/data/network/AuthInterceptor;", "Lokhttp3/Interceptor;", "tokenProvider", "Lcom/ufpr/equilibrium/domain/auth/TokenProvider;", "<init>", "(Lcom/ufpr/equilibrium/domain/auth/TokenProvider;)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "data_debug"}, xs= "", pn = "", xi = 48)
public final class AuthInterceptor implements okhttp3.Interceptor {
    @org.jetbrains.annotations.NotNull()
    private final com.ufpr.equilibrium.domain.auth.TokenProvider tokenProvider = null;

    @javax.inject.Inject()
    public AuthInterceptor(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.domain.auth.TokenProvider tokenProvider) {
        super();
    }

    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull() okhttp3.Interceptor.Chain chain) {
        return null;
    }
}
