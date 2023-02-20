package com.study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // jpa 기본 스팩에 기본 생성자 하나는 있어야한다
@ToString(of = {"id", "userName", "age"})
@NamedQuery(
        // 엡 로딩 시점에 오류가 있으면 오류를 띄워준다
        name = "Member.findByUsername",
        query = "select m from Member m where userName = :userName"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    // java 객체기 때문에 멤버만 바꿔주는 것으로는 완료되지 않는다
    // Team 객체에도 변경을 주어야한다
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
