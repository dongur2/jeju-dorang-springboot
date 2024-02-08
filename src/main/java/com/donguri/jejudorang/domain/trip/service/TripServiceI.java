package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TripServiceI implements TripService{

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final TripRepository tripRepository;

    public TripServiceI(JwtProvider jwtProvider, TripRepository tripRepository) {
        this.jwtProvider = jwtProvider;
        this.tripRepository = tripRepository;
    }


    /*
    * 전체 여행 데이터 조회
    *
    * > Pageable pageable: 10개 목록
    *
    * */
    @Override
    @Transactional
    public Map<String, Object> getAllTrips(Pageable pageable) {
        Map<String, Object> result = new HashMap<>();

        try {
            Page<TripListResponseDto> data = tripRepository.findAll(pageable)
                    .map(trip -> TripListResponseDto.builder().trip(trip).build());

            int page = data.getTotalPages();

            result.put("data", data);
            result.put("page", page);

            return result;

        } catch (Exception e) {
            log.error("여행 데이터 리스트 조회에 실패했습니다.");
            throw e;
        }
    }

    /*
    * 검색어를 포함한 여행 데이터 조회
    *
    * > String word: 검색어
    * > Pageable pageable: 10개 목록
    *
    * */
    @Override
    @Transactional
    public Map<String, Object> getSearchedTripsContainingTagKeyword(String word, Pageable pageable) {
        Map<String, Object> result = new HashMap<>();

        try {
            Page<TripListResponseDto> data = tripRepository.findByTagsContaining(word, pageable)
                    .map(trip -> TripListResponseDto.builder().trip(trip).build());

            result.put("data", data);
            result.put("page", data.getTotalPages());

            return result;

        } catch (Exception e) {
            log.error("검색어를 포함하는 데이터가 없습니다.");
            throw e;
        }
    }

    @Override
    @Transactional
    public TripDetailResponseDto getTripDetail(String token, Long tripId) {
        try {
            Trip tripEntity = tripRepository.findById(tripId)
                    .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글이 없습니다."));

            // Access Token 쿠키가 있고
            if (token != null) {

                // 토큰이 유효할 경우
                StringBuilder idFromJwt = new StringBuilder();
                try {
                    idFromJwt.append(jwtProvider.getUserNameFromJwtToken(token));
                    return new TripDetailResponseDto(tripEntity, idFromJwt.toString());

                // 토큰이 유효하지 않을 경우
                } catch (Exception e) {
                    log.error("유효한 토큰이 아닙니다. 비회원은 북마크 여부를 확인할 수 없습니다.");
                    return new TripDetailResponseDto(tripEntity);
                }

            // Access Token 쿠키가 없음
            } else {
                return new TripDetailResponseDto(tripEntity);
            }

        } catch (Exception e) {
            log.error("여행 상세글 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void saveApiTrips(Mono<TripApiDataDto> response) {
        // Dto -> Entity
        List<Trip> data = response
                .map(TripApiDataDto::toEntity)
                .block();

        // Entity -> DB
        tripRepository.saveAll(data);
    }
}
