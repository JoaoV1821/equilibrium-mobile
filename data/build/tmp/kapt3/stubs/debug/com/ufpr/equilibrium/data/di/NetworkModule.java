package com.ufpr.equilibrium.data.di;

@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0002\u0008\u0002\u0008\u00C7\u0002\u0012\u0001\u0000\u0018\u0000B\t\u0008\u0002\u00A2\u0006\u0004\u0008\u0002\u0010\u0003J\u000C\u0010\u00042\u0004\u0010\u0006(\u00028\u0001H\u0007J\u000C\u0010\u00082\u0004\u0010\n(\u00048\u0003H\u0007J\u0006\u0010\u000C8\u0005H\u0007J\u0012\u0010\u000E2\u0004\u0010\u0010(\u00012\u0004\u0010\u0011(\u00038\u0006H\u0007J\u0012\u0010\u00122\u0004\u0010\u0014(\u00062\u0004\u0010\u0015(\u00058\u0007H\u0007J\u000C\u0010\u00162\u0004\u0010\u0018(\u00078\u0008H\u0007\u00F2\u0001$\n\u00020\u0001\n\u00020\u0005\n\u00020\u0007\n\u00020\t\n\u00020\u000B\n\u00020\r\n\u00020\u000F\n\u00020\u0013\n\u00020\u0017\u00A8\u0006\u0019"}, d2 = {"Lcom/ufpr/equilibrium/data/di/NetworkModule;", "", "<init>", "()V", "provideAuthInterceptor", "Lcom/ufpr/equilibrium/data/network/AuthInterceptor;", "tokenProvider", "Lcom/ufpr/equilibrium/domain/auth/TokenProvider;", "provideUnauthorizedInterceptor", "Lcom/ufpr/equilibrium/data/network/UnauthorizedInterceptor;", "application", "Landroid/app/Application;", "provideGson", "Lcom/google/gson/Gson;", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "authInterceptor", "unauthorizedInterceptor", "provideRetrofit", "Lretrofit2/Retrofit;", "client", "gson", "providePessoasService", "Lcom/ufpr/equilibrium/data/remote/PessoasService;", "retrofit", "data_debug"}, xs= "", pn = "", xi = 48)
@dagger.Module()
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class NetworkModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.ufpr.equilibrium.data.di.NetworkModule INSTANCE = null;

    private NetworkModule() {
        super();
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.ufpr.equilibrium.data.network.AuthInterceptor provideAuthInterceptor(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.domain.auth.TokenProvider tokenProvider) {
        return null;
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.ufpr.equilibrium.data.network.UnauthorizedInterceptor provideUnauthorizedInterceptor(@org.jetbrains.annotations.NotNull() android.app.Application application) {
        return null;
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.gson.Gson provideGson() {
        return null;
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.network.AuthInterceptor authInterceptor, @org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.network.UnauthorizedInterceptor unauthorizedInterceptor) {
        return null;
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull() okhttp3.OkHttpClient client, @org.jetbrains.annotations.NotNull() com.google.gson.Gson gson) {
        return null;
    }

    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.ufpr.equilibrium.data.remote.PessoasService providePessoasService(@org.jetbrains.annotations.NotNull() retrofit2.Retrofit retrofit) {
        return null;
    }
}
