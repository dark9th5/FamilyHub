package com.family.backend.infrastructure.config

import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRelationship
import com.family.backend.domain.model.MemberRole
import com.family.backend.domain.model.RelationshipType
import com.family.backend.domain.model.TimelinePost
import com.family.backend.infrastructure.persistence.repository.FamilyEventRepository
import com.family.backend.infrastructure.persistence.repository.FamilyMemberRepository
import com.family.backend.infrastructure.persistence.repository.MemberRelationshipRepository
import com.family.backend.infrastructure.persistence.repository.TimelinePostRepository
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
        val admin = memberRepository.findByUsername("admin") ?: memberRepository.save(
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

        val child = memberRepository.findByUsername("linh") ?: memberRepository.save(
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

        memberRepository.findByUsername("admin123") ?: memberRepository.save(
            FamilyMember(
                username = "admin123",
                passwordHash = passwordEncoder.encode("123"),
                fullName = "Local Admin 123",
                bio = "Tai khoan local de test app",
                birthDate = LocalDate.of(1990, 1, 1),
                role = MemberRole.ADMIN,
                avatarUrl = "https://i.pravatar.cc/300?img=12"
            )
        )

        if (relationshipRepository.count() == 0L) {
            relationshipRepository.saveAll(
                listOf(
                    MemberRelationship(fromMemberId = admin.id, toMemberId = child.id, type = RelationshipType.PARENT),
                    MemberRelationship(fromMemberId = child.id, toMemberId = admin.id, type = RelationshipType.CHILD)
                )
            )
        }

        if (postRepository.count() == 0L) {
            postRepository.save(
                TimelinePost(
                    authorId = admin.id,
                    content = "Chao mung dai gia dinh den voi mang xa hoi rieng cua dong ho!",
                    imageUrl = "https://images.unsplash.com/photo-1511895426328-dc8714191300"
                )
            )
        }

        if (eventRepository.count() == 0L) {
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
}
