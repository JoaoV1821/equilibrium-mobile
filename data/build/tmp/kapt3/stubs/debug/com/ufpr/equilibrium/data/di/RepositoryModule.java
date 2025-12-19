package com.ufpr.equilibrium.data.di;

@kotlin.Metadata(k = 1, mv = {2, 0, 0}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0008'\u0012\u0001\u0000\u0018\u0000B\u0007\u00A2\u0006\u0004\u0008\u0002\u0010\u0003J\u000C\u0010\u00042\u0004\u0010\u0006(\u00028\u0001H'J\u000C\u0010\u00082\u0004\u0010\u0006(\u00048\u0003H'J\u000C\u0010\u000B2\u0004\u0010\u0006(\u00068\u0005H'\u00F2\u0001\u001C\n\u00020\u0001\n\u00020\u0005\n\u00020\u0007\n\u00020\t\n\u00020\n\n\u00020\u000C\n\u00020\r\u00A8\u0006\u000E"}, d2 = {"Lcom/ufpr/equilibrium/data/di/RepositoryModule;", "", "<init>", "()V", "bindAuthRepository", "Lcom/ufpr/equilibrium/domain/auth/AuthRepository;", "impl", "Lcom/ufpr/equilibrium/data/auth/AuthRepositoryImpl;", "bindPatientRepository", "Lcom/ufpr/equilibrium/domain/repository/PatientRepository;", "Lcom/ufpr/equilibrium/data/repository/PatientRepositoryImpl;", "bindQuestionnaireRepository", "Lcom/ufpr/equilibrium/domain/repository/QuestionnaireRepository;", "Lcom/ufpr/equilibrium/data/repository/QuestionnaireRepositoryImpl;", "data_debug"}, xs= "", pn = "", xi = 48)
@dagger.Module()
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {

    public RepositoryModule() {
        super();
    }

    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ufpr.equilibrium.domain.auth.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.auth.AuthRepositoryImpl impl);

    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ufpr.equilibrium.domain.repository.PatientRepository bindPatientRepository(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.repository.PatientRepositoryImpl impl);

    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.ufpr.equilibrium.domain.repository.QuestionnaireRepository bindQuestionnaireRepository(@org.jetbrains.annotations.NotNull() com.ufpr.equilibrium.data.repository.QuestionnaireRepositoryImpl impl);
}
