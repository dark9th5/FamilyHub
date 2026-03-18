package com.family.backend.infrastructure.security

import com.family.backend.domain.model.MemberRole

data class FamilyPrincipal(
    val memberId: Long,
    val username: String,
    val role: MemberRole
)
