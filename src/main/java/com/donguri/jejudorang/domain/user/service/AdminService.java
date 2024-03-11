package com.donguri.jejudorang.domain.user.service;

public interface AdminService {

    void checkIsAdmin(String token);

    void deleteUnusedImages(String token);
}
