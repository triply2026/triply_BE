package com.example.triply.member.repository;

import com.example.triply.member.entity.PlanMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanMemberRepository extends JpaRepository<PlanMember, Long> {
}
