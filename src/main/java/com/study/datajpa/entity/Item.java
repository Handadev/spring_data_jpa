package com.study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item implements Persistable<String> {

    // jpa에서 지원하는 generateValue를 사용하지 않고
    // number 형이 아닌 다른 id를 임의로 사용한다고 했을 때,
    // .save(객체)를 사용하게 되었을 때, save는 해당 객체 persist를 판단할 때 id가 null인지를 확인한다
    // 하지만 pk에 값이 세팅이 되어있기 때문에 merge를 호출하게 된다
    // insert만 하면되는데 select -> insert를 사용하게 되는 비효율적인 상황이 발생한다
    // 그래서 Persistable<?> 을 사용하면 된다
    // 저장할 때 생성일자를 추가하는 편이 편한다
    // 생성일자는 jpa에 의해 관리되는 값으로
    // isNew 에서 날짜가 null 이면 새로운 객체로 판단하게 하는 방법을 사용하면 편하게 쓸 수 있다
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
