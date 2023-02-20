package com.study.datajpa.entity;

import com.study.datajpa.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void test() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // db 인서트
        em.clear(); // 영속성 컨텍스트 날려버림

        List<Member> members = em.createQuery(
                "select m from Member m ", Member.class
        ).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }

    }

    @Test
    void eventBaseEntity() throws InterruptedException {
        Member mem1 = new Member("mem1");
        memberRepository.save(mem1);

        Thread.sleep(100);

        mem1.setUserName("member1");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(mem1.getId()).orElseThrow(RuntimeException::new);

        System.out.println("findMember = " + findMember);

        System.out.println("mem1 = " + mem1.getCreatedDate());
        System.out.println("mem1 = " + mem1.getLastModifiedDate());
        System.out.println("mem1 = " + mem1.getCreateBy());
        System.out.println("mem1 = " + mem1.getLastModifiedBy());

    }

}