package com.ufpr.equilibrium.data.remote;

/**
 * Retrofit service interface for Pessoas API.
 */
@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0008\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0008\n\u0002\u0008\u0002\n\u0002\u0010\u000E\n\u0002\u0008\u0002\n\u0002\u0018\u0002\n\u0002\u0008\u0002\u0008f\u0012\u0001\u0000\u0018\u0000J\u0014\u0010\u00022\u0006\u0008\u0001\u0010\u0004(\u00028\u0001H\u00A7@\u00A2\u0006\u0002\u0010\u0006J$\u0010\u00072\u0006\u0008\u0003\u0010\t(\u00042\u0006\u0008\u0003\u0010\u000B(\u00042\u0006\u0008\u0003\u0010\u000C(\u00058\u0003H\u00A7@\u00A2\u0006\u0002\u0010\u000EJ\u0014\u0010\u000F2\u0006\u0008\u0001\u0010\u0004(\u00068\u0006H\u00A7@\u00A2\u0006\u0002\u0010\u0011\u00F2\u0001 \n\u00020\u0001\n\u00020\u0003\n\u00020\u0005\n\u00020\u0008\n\u0004\u0018\u00010\n\n\u0004\u0018\u00010\r\n\u00020\u0010\u00A8\u0006\u0012"}, d2 = {"Lcom/ufpr/equilibrium/data/remote/PessoasService;", "", "login", "Lcom/ufpr/equilibrium/data/remote/dto/LoginResultDto;", "request", "Lcom/ufpr/equilibrium/data/remote/dto/LoginRequestDto;", "(Lcom/ufpr/equilibrium/data/remote/dto/LoginRequestDto;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPatients", "Lcom/ufpr/equilibrium/data/remote/PatientsEnvelope;", "page", "", "pageSize", "cpf", "", "(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "postPatient", "Lcom/ufpr/equilibrium/data/remote/dto/PatientRegistrationDto;", "(Lcom/ufpr/equilibrium/data/remote/dto/PatientRegistrationDto;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_debug"}, xs= "", pn = "", xi = 48)
public abstract interface PessoasService {

    @retrofit2.http.POST(value = "auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body() @org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.remote.dto.LoginRequestDto request, @org.jetbrains.annotations.NotNull() kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.data.remote.dto.LoginResultDto> $completion);

    /**
     * Get list of patients with optional pagination and CPF filter.
     */
    @retrofit2.http.GET(value = "patient")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPatients(@retrofit2.http.Query(value = "page") @org.jetbrains.annotations.Nullable() java.lang.Integer page, @retrofit2.http.Query(value = "pageSize") @org.jetbrains.annotations.Nullable() java.lang.Integer pageSize, @retrofit2.http.Query(value = "cpf") @org.jetbrains.annotations.Nullable() java.lang.String cpf, @org.jetbrains.annotations.NotNull() kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.data.remote.PatientsEnvelope> $completion);

    /**
     * Create a new patient.
     */
    @retrofit2.http.POST(value = "patient")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object postPatient(@retrofit2.http.Body() @org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.remote.dto.PatientRegistrationDto request, @org.jetbrains.annotations.NotNull() kotlin.coroutines.Continuation<? super com.ufpr.equilibrium.data.remote.dto.PatientRegistrationDto> $completion);
}
