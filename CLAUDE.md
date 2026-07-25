#Our Java/Spring Boot Standards
Use constructor injection; do not use @Autowired on fields.
All REST endpoints must have @Valid on the request body.
Do not use raw SQL; use JPA or named queries instead.
Services must not call repositories from other domains.

