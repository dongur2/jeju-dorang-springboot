package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/trip")
public class TripController {
    @Autowired
    TripService tripService;

    @GetMapping("/list/{nowPage}")
    public String tripHome(@PathVariable("nowPage") Integer nowPage, Model model) {

        Pageable pageable = PageRequest.of(nowPage, 10);
        Page<TripListResponseDto> trips = tripService.findAll(pageable);

//        model.addAttribute("nowPage", nowPage);
        model.addAttribute("trips", trips);
        return "/trip/tripList";
    }
}
