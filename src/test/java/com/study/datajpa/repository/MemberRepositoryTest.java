package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void springDataRepo() {
        Member member = new Member("A");
        Member save = memberRepository.save(member);

        Member result = memberRepository.findById(save.getId()).orElseThrow(() -> new NoSuchElementException());

        assertThat(save.getId()).isEqualTo(result.getId());
        assertThat(result).isEqualTo(member);
    }


    @Test
    void crud() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건
        Member findMember1 = memberRepository.findById(member1.getId()).orElseThrow(RuntimeException::new);
        Member findMember2 = memberRepository.findById(member2.getId()).orElseThrow(RuntimeException::new);

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 카운트 검증
        long count1 = memberRepository.count();
        assertThat(count1).isEqualTo(0);
    }

    @Test
    void findByUserNameAndAgeGreaterThan() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 30);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<Member> aaa1 = memberRepository.findByUserNameAndAgeGreaterThan("aaa", 25);

        assertThat(aaa1.get(0).getUserName()).isEqualTo("aaa");
        assertThat(aaa1.get(0).getAge()).isEqualTo(30);
    }

    @Test
    void namedQuery() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 30);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<Member> aaa1 = memberRepository.findByUsername("aaa");

        assertThat(aaa1.get(0).getUserName()).isEqualTo("aaa");
        assertThat(aaa1.get(0).getAge()).isEqualTo(10);
    }

    @Test
    void annoQuery() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 30);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<Member> aaa1 = memberRepository.findUser("aaa", 10);

        assertThat(aaa1.get(0).getUserName()).isEqualTo("aaa");
        assertThat(aaa1.get(0).getAge()).isEqualTo(10);
    }

    @Test
    void stingResult() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("aaa", 30);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<String> byUsernameList = memberRepository.findByUsernameList();

        assertThat(byUsernameList.size()).isEqualTo(2);
    }

    @Test
    void dtoQueryResult() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member aaa = new Member("aaa", 10);
        aaa.changeTeam(teamA);
        memberRepository.save(aaa);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    void queryWithCollection() {
        Member aaa = new Member("aaa", 10);
        Member bbb = new Member("bbb", 30);

        memberRepository.save(aaa);
        memberRepository.save(bbb);


        List<Member> byNames = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));

        for (Member member : byNames) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void paging() {
        memberRepository.save(new Member("1", 10));
        memberRepository.save(new Member("2", 10));
        memberRepository.save(new Member("3", 10));
        memberRepository.save(new Member("4", 10));
        memberRepository.save(new Member("5", 10));
        memberRepository.save(new Member("6", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(0);              // 현재 페이지 번호 spring data 는 default 0 으로 설정한다
        assertThat(page.getTotalPages()).isEqualTo(2);          // 총 페이지 수
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        // 엔티티를 직접 노출하는 것은 위험하다고 하다
        // Page 인터페이스는 DTO 변경을 쉽게 할 수 있도록 지원해준다
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUserName(), null));

    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("1", 10));
        memberRepository.save(new Member("2", 20));
        memberRepository.save(new Member("3", 40));
        memberRepository.save(new Member("4", 50));
        memberRepository.save(new Member("5", 70));

        int i = memberRepository.bulkAgePlus(20); // 벌크연산 완료

        em.flush(); // db에 강제 업데이트
        em.clear(); // 영속성 컨텍스트 초기화

        List<Member> result = memberRepository.findListByUserName("5");

        assertThat(i).isEqualTo(4);
    }

    @Test
    void findMemberLazy() {
        // member1 -> team A
        // member2 -> team B

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 30, teamB);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

        List<Member> all = memberRepository.findEntityGraphByUserName("memberA");

        // N + 1 문제 발생
        for (Member member : all) {
            System.out.println("member.getUserName() = " + member.getUserName());
            // class com.study.datajpa.entity.Team$HibernateProxy$j3MnfyEe 하이버네이트 프록시 가짜 객체를 생성한다
            System.out.println("member.getUserName() = " + member.getTeam().getClass());
            // getTeam 까지는 쿼리가 나가지 않음 .getName() 를 하면 그제야 쿼리 조회를 실행함
            System.out.println("member.getUserName() = " + member.getTeam().getName());
        }
    }

    @Test
    // 조회용으로만 쓰기위해서 만든 객체라고 해도 hibernate는 업데이트 등의 변경사항을 대비하기위한 더티체킹용 스탭샷을 하나 더 만들어 놓는다
    // 이를 아예 사용하지 않고 완전 조회용으로 쓰는 방법이 있다
    void hint() {
        Member member = new Member("1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUserName("1");
        findMember.setUserName("222"); // readonly일 때는 업데이트가 안된다

        em.flush();
    }

    @Test
    void lock() {
        Member member = new Member("1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUserName("1");
    }
}