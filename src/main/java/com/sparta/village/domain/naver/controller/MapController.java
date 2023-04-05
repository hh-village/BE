package com.sparta.village.domain.naver.controller;

import com.sparta.village.domain.naver.service.NaverMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maps")
@RequiredArgsConstructor
public class MapController {
    private final NaverMapService naverMapService;

    @GetMapping("/geocode")
    public String geoCode(@RequestParam String address) {
        return naverMapService.geoCode(address);
    }

    @PreAuthorize("hasRole('Role_USER')")
    @GetMapping("/gc")
    public String reverseGeocode(@RequestParam String coords) {
        return naverMapService.reverseGeocode(coords);
    }
}
