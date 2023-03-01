package com.study.datajpa.dto;

import com.study.datajpa.entity.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MemberDto {
    private Long id;
    private String userName;
    private String teamName;

    // DTO는 Entity를 바라보는 것이 편하다
    public MemberDto(Member member) {
        this.id = member.getId();
        this.userName = member.getUserName();
    }
}
