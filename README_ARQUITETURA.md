# Arquitetura – Equilibrium (Android)

Este documento resume as decisões de arquitetura e a estrutura modular adotada.

## Camadas e Módulos

- app: UI (Activities/Fragments agora migrando para MVVM), Navegação e inicialização do Hilt.
- domain: Casos de uso e modelos de domínio. Não conhece detalhes de framework.
- data: Repositórios e fontes de dados (Retrofit/OkHttp). Implementa interfaces do domain.
- core-common: utilitários transversais (Result, mapeamento de erros etc.).

Fluxo: UI → ViewModel → UseCase → Repository → DataSource(s)

```
UI(View)/ViewModel
        │
        ▼
   UseCase (domain)
        │
        ▼
Repository (data)
        │
        ▼
Remote/Local DataSources (data)
```

## DI

Hilt é utilizado com `@HiltAndroidApp` no `Application`, módulos em `data` para Retrofit/OkHttp e bindings de repositórios, e módulos em `app` para factories/casos de uso.

## Assíncrono

Coroutines/Flow para lógica reativa. UI usa `StateFlow` para estados `Loading/Success/Error`.

## Login – Exemplo Vertical

Migração do login para MVVM: `LoginActivity` observa `LoginViewModel`, que chama `LoginUseCase` (domain) e `AuthRepository` (data) via Hilt.

## Próximos Passos

- Migrar gradualmente cada feature.
- Adicionar testes de caracterização e snapshots de UI.
- Expandir Design System (Material 3) e adaptatividade.


