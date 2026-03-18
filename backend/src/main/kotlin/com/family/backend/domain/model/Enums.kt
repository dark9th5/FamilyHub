package com.family.backend.domain.model

enum class MemberRole {
    ADMIN,
    MEMBER
}

enum class RelationshipType {
    PARENT,
    CHILD,
    SIBLING,
    SPOUSE
}

enum class RsvpStatus {
    GOING,
    MAYBE,
    DECLINED
}
