package com.example.CovidTracker.controller;


import com.example.CovidTracker.model.Location;
import com.example.CovidTracker.virusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller

public class LocationController {
    @Autowired
    virusRepository virusRepository;

    @GetMapping("/home")
    public String home(Model model) {
        List<Location> countyData = virusRepository.findAll(Sort.by("state"));
        int totalcases = countyData.stream().mapToInt(stat -> stat.getCases()).sum();

        model.addAttribute("locations", countyData);
        model.addAttribute("totalcases", totalcases);
        return "home";
    }
}
