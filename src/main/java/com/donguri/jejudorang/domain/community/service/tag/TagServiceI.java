package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import com.donguri.jejudorang.domain.community.repository.tag.TagRepository;
import jakarta.validation.ValidationException;
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
        if (keyword.length() > 20) {
            throw new ValidationException("태그 이름은 20자를 초과할 수 없습니다.");
        }

        return tagRepository.save(Tag.builder()
                        .keyword(keyword)
                        .build());
    }

}
