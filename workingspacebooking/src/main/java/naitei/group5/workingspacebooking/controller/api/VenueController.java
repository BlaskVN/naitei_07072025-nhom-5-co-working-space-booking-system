package naitei.group5.workingspacebooking.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.service.VenueService;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping("/")
    public ResponseEntity<VenueResponseDto> createVenueRequest(
            @Valid @RequestBody CreateVenueRequestDto requestDto) {
        
        VenueResponseDto responseDto = venueService.createVenueRequest(requestDto.ownerId(), requestDto);
        
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
