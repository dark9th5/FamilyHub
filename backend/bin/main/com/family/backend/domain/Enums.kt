package com.family.backend.domain

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
