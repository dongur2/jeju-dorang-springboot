package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.tag.Tag;

import java.util.Optional;

public interface TagService {
    Optional<Tag> checkDuplicated(String tag);
    Tag saveNewTag(String keyword);
}
