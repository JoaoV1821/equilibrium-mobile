package com.ufpr.equilibrium.data.network;

/**
 * Interceptor que verifica se o token foi expirado (401 Unauthorized)
 * e redireciona para a tela de login
 */
@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000\u001E\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0012\u0001\u0000\u0018\u0000B\u000F\u0008\u0007\u0012\u0004\u0010\u0002(\u0001\u00A2\u0006\u0004\u0008\u0004\u0010\u0005J\u000C\u0010\u00062\u0004\u0010\u0008(\u00038\u0002H\u0016R\u000C\u0010\u0002H\u0001X\u0082\u0004\u00A2\u0006\u0002\n\u0000\u00F2\u0001\u0010\n\u00020\u0001\n\u00020\u0003\n\u00020\u0007\n\u00020\t\u00A8\u0006\n"}, d2 = {"Lcom/ufpr/equilibrium/data/network/UnauthorizedInterceptor;", "Lokhttp3/Interceptor;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "data_debug"}, xs= "", pn = "", xi = 48)
public final class UnauthorizedInterceptor implements okhttp3.Interceptor {
    @org.jetbrains.annotations.NotNull()
    private final android.app.Application application = null;

    @javax.inject.Inject()
    public UnauthorizedInterceptor(@org.jetbrains.annotations.NotNull() android.app.Application application) {
        super();
    }

    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull() okhttp3.Interceptor.Chain chain) {
        return null;
    }
}
