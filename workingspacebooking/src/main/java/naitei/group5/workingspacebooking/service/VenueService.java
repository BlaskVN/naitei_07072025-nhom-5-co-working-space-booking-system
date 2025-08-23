package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRenterRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.UpdateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.AdminVenueViewDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailRenterResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueSoftDeleteResponseDto;
import naitei.group5.workingspacebooking.entity.Venue;

import java.util.List;

public interface VenueService {

    // ==== Owner use cases ====
    VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto);

    List<Venue> getVenuesByOwner(Integer ownerId);

    List<VenueResponseDto> filterOwnerVenues(FilterVenueRequestDto req);
    
    VenueResponseDto updateVenue(Integer ownerId, Integer venueId, UpdateVenueRequestDto request);

    //Xem chi tiết venue của owner
    VenueDetailResponseDto getVenueDetailByOwner(Integer ownerId, Integer venueId);

    // ==== Renter use cases ====
    List<VenueResponseDto> getVerifiedVenues();

    List<VenueResponseDto> filterVenuesForRenter(FilterVenueRenterRequestDto req);
    //Xóa mềm venue của owner
    VenueSoftDeleteResponseDto softDeleteVenue(Integer venueId, JwtUserDetails userDetails);

    VenueDetailRenterResponseDto getVenueDetail(Integer venueId);

    // ==== Admin use cases ====
    List<AdminVenueViewDto> adminListVenues(String name, String status, String sort);

}
