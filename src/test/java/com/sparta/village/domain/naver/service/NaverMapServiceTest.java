package com.sparta.village.domain.naver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.sparta.village.domain.naver.service.NaverMapService.findValueByKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaverMapServiceTest {
    @InjectMocks
    private NaverMapService naverMapService;
    @Mock
    private RestTemplate restTemplate;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    @Test
    @DisplayName("Geocode Test")
    public void testGeocode() {
        //given
        String address = "서울 강남구";
        String expectedResponse = "{\"result\": \"Test Result\"}";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(urlCaptor.capture(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class))).thenReturn(responseEntity);

        // When
        String result = naverMapService.geocode(address);

        // Then
        assertEquals(expectedResponse, result);

        String actualUrl = urlCaptor.getValue();
        assertTrue(actualUrl.contains(address));

        HttpHeaders actualHeaders = httpEntityCaptor.getValue().getHeaders();
        assertTrue(actualHeaders.containsKey("X-NCP-APIGW-API-KEY-ID"));
        assertTrue(actualHeaders.containsKey("X-NCP-APIGW-API-KEY"));

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class));
    }

    @Test
    @DisplayName("Reverse Geocode Test")
    public void testReverseGeocode() {
        // Given
        String coords = "37.3595704,127.105399";
        String responseBody = "your_response_body_here"; // Replace this with your actual response body

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(urlCaptor.capture(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class))).thenReturn(responseEntity);

        // When
        String result = naverMapService.reverseGeocode(coords);

        // Then
        assertNotNull(result); // Add more specific assertions depending on the expected result

        String actualUrl = urlCaptor.getValue();
        assertTrue(actualUrl.contains(coords));

        HttpHeaders actualHeaders = httpEntityCaptor.getValue().getHeaders();
        assertTrue(actualHeaders.containsKey("X-NCP-APIGW-API-KEY-ID"));
        assertTrue(actualHeaders.containsKey("X-NCP-APIGW-API-KEY"));

        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class));
    }

    @Test
    @DisplayName("Find Value By Key Test - Match Found")
    public void testFindValueByKeyMatchFound() {
        // Given
        String jsonString = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": \"value3\"}";
        String regex = "\"key2\":\\s*\"(.*?)\"";

        // When
        String result = findValueByKey(jsonString, regex);

        // Then
        assertNotNull(result);
        assertEquals("value2", result);
    }

}