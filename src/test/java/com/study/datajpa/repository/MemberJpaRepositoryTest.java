package com.study.datajpa.repository;

import com.study.datajpa.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void test() {
        Member member = new Member("A");
        Member save = memberJpaRepository.save(member);

        Member result = memberJpaRepository.find(save.getId());

        assertThat(save.getId()).isEqualTo(result.getId());
    }

    @Test
    void crud() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건
        Member findMember1 = memberJpaRepository.findById(member1.getId()).orElseThrow(RuntimeException::new);
        Member findMember2 = memberJpaRepository.findById(member2.getId()).orElseThrow(RuntimeException::new);

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        // 카운트 검증
        long count1 = memberJpaRepository.count();
        assertThat(count1).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterTen() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 30);

        memberJpaRepository.save(aaa);
        memberJpaRepository.save(bbb);

        List<Member> aaa1 = memberJpaRepository.findByUserNameAndAgeGreaterThan("aaa", 25);

        assertThat(aaa1.get(0).getUserName()).isEqualTo("aaa");
        assertThat(aaa1.get(0).getAge()).isEqualTo(30);
    }

    @Test
    void paging() {
        memberJpaRepository.save(new Member("1", 10));
        memberJpaRepository.save(new Member("2", 10));
        memberJpaRepository.save(new Member("3", 10));
        memberJpaRepository.save(new Member("4", 10));
        memberJpaRepository.save(new Member("5", 10));
        memberJpaRepository.save(new Member("6", 10));

        List<Member> members = memberJpaRepository.findByPage(10, 0, 3);
        long totalCount = memberJpaRepository.totalCount(10);

        assertThat(members.size()).isEqualTo(3);

        assertThat(totalCount).isEqualTo(6);
    }

    @Test
    void bulkUpdate() {
        memberJpaRepository.save(new Member("1", 10));
        memberJpaRepository.save(new Member("2", 20));
        memberJpaRepository.save(new Member("3", 40));
        memberJpaRepository.save(new Member("4", 50));
        memberJpaRepository.save(new Member("4", 70));

        int i = memberJpaRepository.bulkAge(20);
        assertThat(i).isEqualTo(4);
    }
}