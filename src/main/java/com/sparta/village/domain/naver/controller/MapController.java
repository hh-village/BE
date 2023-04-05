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
    public String geocode(@RequestParam String address) {
        return naverMapService.geocode(address);
    }
}
