package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MmeberRepositoryCustom 을 extends 로 받아온 다음 그대로 사용하게 되면,
 * spring data 에서 자동으로 MemberRepositoryImpl 을 사용하게 도와준다
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    // limit가 쿼리로 날아감
    List<Member> findTop3HelloBy();

    // named query를 먼저 찾고 없으면
    // spring data에서 메소드를 생성해서 제공하는 기능을 실행한다
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("userName") String userName);

    // repository에 쿼리 직접 입력하여 진행할 수 있음
    // jpql이 있다면 영속성 컨텍스트는 먼저 자동 flush를 해주고 jqpl 실행해준다
    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    // DTO로 조회하기
    @Query("select m.userName from Member m")
    List<String> findByUsernameList();

    @Query("select new com.study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // in절에 param을 통해 컬랙션을 사용할 수 있다
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 일반 jpa의 경우 throw error 처리됨
    // spring data jpa는 값이 비었을 때 size 0으로 리턴함
    List<Member> findListByUserName(String userName);

    // spring data jpa는 값이 비었을 때 null로 리턴함
    Member findMemberByUserName(String userName);

    Optional<Member> findOptionalByUserName(String userName);


    // 페이징
    // 성능을 개선을 위한 카운트 쿼리 분할이 가능함
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 총 element 갯수, 총 페이지 수 계산하지 않는다. 핸드폰 드래그 리스트 만들 때 적합
//    Slice<Member> findByAge(int age, Pageable pageable);


    // 벌크 연산의 주의점
    // 영속성 컨텍스트를 무시하고 db에 강제로 진행하는 방법이다. 영속성 컨텍스트는 이 변화를 알 방법이 존재하지 않는다
    // 벌크연산만 하고 끝나는 로직이라면 문제가 없지만, 이후에 영속성 컨택스트를 설정할 경우가 된다면 벌크연산 후 무조건 clear를 진행해줘야한다
    // em.clear() 를 진행하거나 @Modifying(clearAutomatically = true)
    // ps. 마이바티스 등 다른 db 툴을 사용하게 된다면, flush / clear를 잊지 말자
    @Modifying(clearAutomatically = true) // 벌크 업데이트
    @Query("update Member m set m.age = m.age + 10 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
        // fetch join jpql 안 쓰고 바로 바로 지원해주는 기능
    List<Member> findAll();

    // jpql + EntityGraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // jpa repo 기본 문법 + EntityGraph
//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") // Entity에 직접 설정하는 법
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    // 조회용으로만 쓰기위해서 만든 객체라고 해도 hibernate는 업데이트 등의 변경사항을 대비하기위한 더티체킹용 스탭샷을 하나 더 만들어 놓는다
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(@Param("userName") String userName);

    // DB에 락을 걸 수 있다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);
}
