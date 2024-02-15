package com.donguri.jejudorang.domain.notification.repository;

import com.donguri.jejudorang.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<List<Notification>> findAllByOwnerId(Long userId);

}
