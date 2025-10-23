# Plano de Migração Incremental

## Diagnóstico (principais problemas)
- Atividades orquestram rede e lógica (God Activities), sem camadas.
- Retrofit global sem DI; SessionManager acessado estaticamente.
- Falta de testes; acoplamento de UI com rede.
- Sem adaptatividade estruturada e design system central.

## Roadmap por PRs
1. Base modular + DI (este PR)
   - Criar módulos core-common, domain, data; Hilt e Coroutines.
   - Auth (login) migrado para MVVM como exemplo vertical.
2. Navegação e sessão
   - Encapsular roteamento via Navigation Component.
   - ViewModels para `MainActivity`, Homes, e persistência de sessão via UseCase.
3. Pacientes (Listagem + Cadastro)
   - Repos/UseCases e telas migradas para MVVM.
4. Testes e qualidade
   - Characterization tests, snapshots UI, cobertura.
5. Design System e adaptatividade
   - Material 3 tokens, dimens, tipografia. Layouts sw600dp.

## Riscos e mitigação
- Regressão funcional: usar testes de caracterização e PRs pequenos.
- Integração backend: usar MockWebServer/feature flags.
- Tempo de build: modularização e cache Gradle.


