package com.donguri.jejudorang.domain.community.service.tag;

import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import com.donguri.jejudorang.domain.community.repository.tag.TagRepository;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceI implements TagService{
    private final TagRepository tagRepository;


    @Override
    @Transactional
    public Optional<Tag> checkDuplicated(String tag) {
        return tagRepository.findByKeyword(tag);
    }

    @Override
    @Transactional
    public Tag saveNewTag(String keyword) {
        if (keyword.length() > 20) {
            throw new CustomException(CustomErrorCode.EXCEEDED_TAG_NAME_LENGTH);
        }

        return tagRepository.save(Tag.builder()
                        .keyword(keyword)
                        .build());
    }

}
