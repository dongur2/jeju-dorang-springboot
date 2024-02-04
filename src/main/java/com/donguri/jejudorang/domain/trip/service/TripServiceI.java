package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
import com.donguri.jejudorang.global.config.JwtProvider;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class TripServiceI implements TripService{

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final TripRepository tripRepository;

    public TripServiceI(JwtProvider jwtProvider, TripRepository tripRepository) {
        this.jwtProvider = jwtProvider;
        this.tripRepository = tripRepository;
    }


    @Override
    public Page<TripListResponseDto> getAllTripsOnPage(Pageable pageable) {
        Page<Trip> tripEntityList =  tripRepository.findAll(pageable);
        return tripEntityList.map(TripListResponseDto::new);
    }

    @Override
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
    public void saveApiTrips(Mono<TripApiDataDto> response) {
        // Dto -> Entity
        List<Trip> data = response
                .map(TripApiDataDto::toEntity)
                .block();

        // Entity -> DB
        tripRepository.saveAll(data);
    }
}
