package com.donguri.jejudorang.domain.trip.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trip")
public class TripController {

    @GetMapping("/list")
    public String tripHome() {
        return "/trip/tripList";
    }
}
