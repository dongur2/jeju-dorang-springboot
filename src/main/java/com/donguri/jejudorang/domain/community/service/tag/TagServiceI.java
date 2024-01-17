package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import com.donguri.jejudorang.domain.community.repository.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagServiceI implements TagService{
    @Autowired
    TagRepository tagRepository;

    @Override
    public Optional<Tag> checkDuplicated(String tag) {
        return tagRepository.findByKeyword(tag);

    }

    @Override
    public Tag saveNewTag(String keyword) {
        return tagRepository.save(Tag.builder()
                        .keyword(keyword)
                        .build());
    }

}
