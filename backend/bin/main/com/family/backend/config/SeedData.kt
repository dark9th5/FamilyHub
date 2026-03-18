package com.family.backend.config

import com.family.backend.domain.FamilyEvent
import com.family.backend.domain.FamilyMember
import com.family.backend.domain.MemberRole
import com.family.backend.domain.RelationshipType
import com.family.backend.domain.TimelinePost
import com.family.backend.repository.FamilyEventRepository
import com.family.backend.repository.FamilyMemberRepository
import com.family.backend.repository.MemberRelationshipRepository
import com.family.backend.repository.TimelinePostRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.time.LocalDateTime

@Configuration
class SeedData {
    @Bean
    fun seed(
        memberRepository: FamilyMemberRepository,
        relationshipRepository: MemberRelationshipRepository,
        postRepository: TimelinePostRepository,
        eventRepository: FamilyEventRepository,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        if (memberRepository.count() > 0) return@CommandLineRunner

        val admin = memberRepository.save(
            FamilyMember(
                username = "admin",
                passwordHash = passwordEncoder.encode("admin123"),
                fullName = "Tran Minh Quan",
                bio = "Truong toc va dieu phoi su kien",
                birthDate = LocalDate.of(1980, 5, 16),
                role = MemberRole.ADMIN,
                avatarUrl = "https://i.pravatar.cc/300?img=11"
            )
        )
        val child = memberRepository.save(
            FamilyMember(
                username = "linh",
                passwordHash = passwordEncoder.encode("linh123"),
                fullName = "Tran Gia Linh",
                bio = "Thanh vien tre, yeu thich luu giu ky niem",
                birthDate = LocalDate.of(2002, 8, 2),
                role = MemberRole.MEMBER,
                avatarUrl = "https://i.pravatar.cc/300?img=47"
            )
        )

        relationshipRepository.saveAll(
            listOf(
                com.family.backend.domain.MemberRelationship(fromMemberId = admin.id, toMemberId = child.id, type = RelationshipType.PARENT),
                com.family.backend.domain.MemberRelationship(fromMemberId = child.id, toMemberId = admin.id, type = RelationshipType.CHILD)
            )
        )

        postRepository.save(
            TimelinePost(
                authorId = admin.id,
                content = "Chao mung dai gia dinh den voi mang xa hoi rieng cua dong ho!",
                imageUrl = "https://images.unsplash.com/photo-1511895426328-dc8714191300"
            )
        )

        eventRepository.save(
            FamilyEvent(
                title = "Gio to ho Tran",
                description = "Hop mat toan bo thanh vien de cung gio to va ket noi the he tre.",
                eventTime = LocalDateTime.now().plusDays(10),
                location = "Nha tho ho Tran, Nam Dinh",
                createdBy = admin.id
            )
        )
    }
}
