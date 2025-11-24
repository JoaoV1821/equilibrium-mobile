package com.ufpr.equilibrium.data.remote;

@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0002\u0008f\u0012\u0001\u0000\u0018\u0000J\u0014\u0010\u00022\u0006\u0008\u0001\u0010\u0004(\u00028\u0001H\u00A7@\u00A2\u0006\u0002\u0010\u0006\u00F2\u0001\u000C\n\u00020\u0001\n\u00020\u0003\n\u00020\u0005\u00A8\u0006\u0007"}, d2 = {"Lcom/ufpr/equilibrium/data/remote/PessoasService;", "", "login", "Lcom/ufpr/equilibrium/data/remote/dto/LoginResultDto;", "request", "Lcom/ufpr/equilibrium/data/remote/dto/LoginRequestDto;", "(Lcom/ufpr/equilibrium/data/remote/dto/LoginRequestDto;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_debug"}, xs= "", pn = "", xi = 48)
public abstract interface PessoasService {

    @retrofit2.http.POST(value = "auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body() @org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.remote.dto.LoginRequestDto request, @org.jetbrains.annotations.NotNull() kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.data.remote.dto.LoginResultDto> $completion);
}
