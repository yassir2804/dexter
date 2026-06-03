package com.dexter.app.domain.error

/**
 * Sealed class representing domain-level errors.
 *
 * Each subclass maps to a specific failure category so that upper layers
 * (ViewModels, UI) can react accordingly without depending on low-level
 * exception types.
 */
sealed class DomainError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class AuthError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class UserError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class MatchError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class SkillError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class ReviewError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class NetworkError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class MappingError(
        message: String,
        cause: Throwable? = null
    ) : DomainError(message, cause)

    class UnknownError(
        message: String = "An unknown error occurred",
        cause: Throwable? = null
    ) : DomainError(message, cause)
}
