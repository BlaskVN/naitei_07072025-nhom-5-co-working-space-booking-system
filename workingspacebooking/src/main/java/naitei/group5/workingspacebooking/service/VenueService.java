package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;

public interface VenueService {
    VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto);
}
