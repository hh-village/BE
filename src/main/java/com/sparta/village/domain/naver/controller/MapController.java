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

    // 클라이언트가 요청한 검색어를 매개변수로 받아 네이버 지도 API를 호출하고 결과를 반환하는 메소드
    @GetMapping("/search")
    public String search(@RequestParam String keyword) {
        return naverMapService.searchByKeyword(keyword);
    }

    @PreAuthorize("hasRole('Role_USER')")
    @GetMapping("/geocode")
    public String geoCode(@RequestParam String address) {
        System.out.println("======클릭은 되니?=======");
        return naverMapService.geoCode(address);
    }

    @PreAuthorize("hasRole('Role_USER')")
    @GetMapping("/gc")
    public String reverseGeocode(@RequestParam String coords) {
        System.out.println("======클릭은 되니?=======");
        return naverMapService.reverseGeocode(coords);
    }
}
