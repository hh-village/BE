package com.sparta.village.domain.naver.controller;

import com.sparta.village.domain.naver.service.NaverMapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapControllerTest {
    @InjectMocks
    private MapController mapController;
    @Mock
    private NaverMapService naverMapService;
    @Test
    @DisplayName("geocode")
    public void testGeocode() {
        //given
        when(naverMapService.geocode("서울")).thenReturn("129,30");

        //when
        String result = mapController.geoCode("서울");

        //then
        assertEquals("129,30", result);
        verify(naverMapService).geocode("서울");
    }

    @Test
    @DisplayName("reverseGeocode")
    public void testReverseGeocode() {
        //given
        when(naverMapService.reverseGeocode("129,30")).thenReturn("서울");

        //when
        String result = mapController.reverseGeocode("129,30");

        //then
        assertEquals("서울", result);
        verify(naverMapService).reverseGeocode("129,30");
    }
}