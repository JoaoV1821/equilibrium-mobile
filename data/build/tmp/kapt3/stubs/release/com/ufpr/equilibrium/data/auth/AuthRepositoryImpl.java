package com.ufpr.equilibrium.data.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J$\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/ufpr/equilibrium/data/auth/AuthRepositoryImpl;", "Lcom/ufpr/equilibrium/domain/auth/AuthRepository;", "service", "Lcom/ufpr/equilibrium/data/remote/PessoasService;", "(Lcom/ufpr/equilibrium/data/remote/PessoasService;)V", "decodeJwtPayload", "Lorg/json/JSONObject;", "token", "", "login", "Lcom/ufpr/equilibrium/core/common/Result;", "Lcom/ufpr/equilibrium/domain/model/UserSession;", "username", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_release"})
public final class AuthRepositoryImpl implements com.ufpr.equilibrium.domain.auth.AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.ufpr.equilibrium.data.remote.PessoasService service = null;
    
    @javax.inject.Inject()
    public AuthRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.ufpr.equilibrium.data.remote.PessoasService service) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.core.common.Result<com.ufpr.equilibrium.domain.model.UserSession>> $completion) {
        return null;
    }
    
    private final org.json.JSONObject decodeJwtPayload(java.lang.String token) {
        return null;
    }
}