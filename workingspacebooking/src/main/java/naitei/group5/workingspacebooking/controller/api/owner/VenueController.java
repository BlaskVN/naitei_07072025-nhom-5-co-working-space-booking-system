package naitei.group5.workingspacebooking.controller.api.owner;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.UpdateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueSoftDeleteResponseDto;
import naitei.group5.workingspacebooking.service.VenueService;
import naitei.group5.workingspacebooking.utils.ConverterDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated; 
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;


@RestController
@RequestMapping("/api/owner/venues")
@RequiredArgsConstructor
@Validated
public class VenueController {

    private final VenueService venueService;

    // GET /api/owner/venues?ownerId=123  (list venue của owner)
    @GetMapping(params = "ownerId")
    public List<VenueResponseDto> listMyVenues(@RequestParam Integer ownerId) {
        return venueService.getVenuesByOwner(ownerId)
                .stream().map(ConverterDto::toVenueResponseDto).toList();
    }

    // POST /api/owner/venues  (owner tạo venue)
    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenueRequest(
            @Valid @RequestBody CreateVenueRequestDto requestDto) {

        VenueResponseDto responseDto =
                venueService.createVenueRequest(requestDto.ownerId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // POST /api/owner/venues/filter  (lọc theo tiêu chí)
    @PostMapping("/filter")
    public List<VenueResponseDto> filterVenues(@RequestBody FilterVenueRequestDto req) {
        return venueService.filterOwnerVenues(req);
    }

    // GET /api/owner/venues/{venueId}/detail?ownerId=123 (Xem chi tiết 1 venue + slot trống)
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueDetailResponseDto> getMyVenueDetail(
            @PathVariable Integer venueId,
            @RequestParam @Positive Integer ownerId
    ) {
        var dto = venueService.getVenueDetailByOwner(ownerId, venueId);
        return ResponseEntity.ok(dto);
    }

    // DELETE /api/owner/venues/{venueId}?ownerId=123 (soft delete venue)
    @DeleteMapping("/{venueId}")
    public ResponseEntity<VenueSoftDeleteResponseDto> softDeleteVenue(
            @PathVariable Integer venueId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        return ResponseEntity.ok(venueService.softDeleteVenue(venueId, userDetails));
    }
    // PUT /api/owner/venues/{venueId} (owner update venue)
    @PutMapping("/{venueId}")
    public ResponseEntity<VenueResponseDto> updateVenue(
            @PathVariable Integer venueId,
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Valid @RequestBody UpdateVenueRequestDto requestDto
    ) {
        Integer ownerId = userDetails.getId(); // lấy ownerId từ JWT
        VenueResponseDto responseDto = venueService.updateVenue(ownerId, venueId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
