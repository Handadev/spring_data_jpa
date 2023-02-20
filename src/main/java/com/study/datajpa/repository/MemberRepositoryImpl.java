package com.study.datajpa.repository;

import com.study.datajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * spring data 에 맞게 들어가기 위해서는 중요한 명명 규칙이 존재한다
 * 인터페이스의 이름은 어떻게 되는 상관이 없지만, 구현체의 이름은
 * 사용하게될 repository 명 + Impl 이다
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
    }
}
