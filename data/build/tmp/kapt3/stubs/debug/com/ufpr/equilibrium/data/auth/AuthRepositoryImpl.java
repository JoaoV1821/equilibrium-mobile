package com.ufpr.equilibrium.data.auth;

@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000E\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0002\u0008\u0002\u0012\u0001\u0000\u0018\u0000B\u000F\u0008\u0007\u0012\u0004\u0010\u0002(\u0001\u00A2\u0006\u0004\u0008\u0004\u0010\u0005J\u0018\u0010\u00062\u0004\u0010\t(\u00042\u0004\u0010\u000B(\u00048\u0003H\u0096@\u00A2\u0006\u0002\u0010\u000CJ\u000C\u0010\r2\u0004\u0010\u000F(\u00048\u0005H\u0002R\u000C\u0010\u0002H\u0001X\u0082\u0004\u00A2\u0006\u0002\n\u0000\u00F2\u0001\u001E\n\u00020\u0001\n\u00020\u0003\n\u00020\u0008\n\u0006\u0012\u0002\u0018\u00020\u0007\n\u00020\n\n\u0004\u0018\u00010\u000E\u00A8\u0006\u0010"}, d2 = {"Lcom/ufpr/equilibrium/data/auth/AuthRepositoryImpl;", "Lcom/ufpr/equilibrium/domain/auth/AuthRepository;", "service", "Lcom/ufpr/equilibrium/data/remote/PessoasService;", "<init>", "(Lcom/ufpr/equilibrium/data/remote/PessoasService;)V", "login", "Lcom/ufpr/equilibrium/core/common/Result;", "Lcom/ufpr/equilibrium/domain/model/UserSession;", "cpf", "", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "decodeJwtPayload", "Lorg/json/JSONObject;", "token", "data_debug"}, xs= "", pn = "", xi = 48)
public final class AuthRepositoryImpl implements com.ufpr.equilibrium.domain.auth.AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.ufpr.equilibrium.data.remote.PessoasService service = null;

    @javax.inject.Inject()
    public AuthRepositoryImpl(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.remote.PessoasService service) {
        super();
    }

    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object login(@org.jetbrains.annotations.NotNull() java.lang.String cpf, @org.jetbrains.annotations.NotNull() java.lang.String password, @org.jetbrains.annotations.NotNull() kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.core.common.Result<com.ufpr.equilibrium.domain.model.UserSession>> $completion) {
        return null;
    }

    private final org.json.JSONObject decodeJwtPayload(java.lang.String token) {
        return null;
    }
}
