package com.donguri.jejudorang.global.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder // 엔티티 간 공통 사용 매핑 정보 정의 위한 사용 -> 상속받은 서브 클래는 해당 매핑 정보 상속
@MappedSuperclass // 실제 엔티티가 아니므로 엔티티끼리만 상속가능한 특징 임시 충족
@EntityListeners(AuditingEntityListener.class) // 엔티티 관련 이벤트 발생시 해당 이벤트 처리 -> 컬럼 자동 업데이트
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected BaseEntity() {}
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
