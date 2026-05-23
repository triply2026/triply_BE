package com.example.triply.member.entity;

import com.example.triply.member.entity.enums.PlanMemberRole;
import com.example.triply.tripPlan.entity.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plan_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "plan_id"})
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanMemberRole role;
}
