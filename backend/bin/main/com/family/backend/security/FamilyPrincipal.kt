package com.family.backend.security

import com.family.backend.domain.MemberRole

data class FamilyPrincipal(
    val memberId: Long,
    val username: String,
    val role: MemberRole
)
