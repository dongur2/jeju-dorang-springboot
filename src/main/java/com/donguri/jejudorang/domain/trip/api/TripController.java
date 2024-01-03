package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.trip.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/trip")
public class TripController {
    @Autowired
    TripService tripService;

    @GetMapping("/list")
    public String tripHome(Model model) {
        List<Trip> trips = tripService.findAll();
        model.addAttribute("trips", trips);
        return "/trip/tripList";
    }
}
