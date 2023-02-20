package com.study.datajpa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MemberDto {
    private Long id;
    private String userName;
    private String teamName;
}
