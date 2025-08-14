package naitei.group5.workingspacebooking.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.repository.VenueStyleRepository;
import naitei.group5.workingspacebooking.service.VenueService;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueStyleRepository venueStyleRepository;
    private final UserRepository userRepository;

    public VenueServiceImpl(VenueRepository venueRepository, 
                           VenueStyleRepository venueStyleRepository,
                           UserRepository userRepository) {
        this.venueRepository = venueRepository;
        this.venueStyleRepository = venueStyleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));

        if (!UserRole.owner.equals(owner.getRole())) {
            throw new IllegalArgumentException("User does not have owner permission");
        }

        VenueStyle venueStyle = venueStyleRepository.findById(requestDto.getVenueStyleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue style not found with ID: " + requestDto.getVenueStyleId()));

        Venue venue = Venue.builder()
                .owner(owner)
                .venueStyle(venueStyle)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .capacity(requestDto.getCapacity())
                .location(requestDto.getLocation())
                .image(requestDto.getImage())
                .verified(false)
                .build();

        Venue savedVenue = venueRepository.save(venue);
        
        return convertToResponseDto(savedVenue);
    }

    private VenueResponseDto convertToResponseDto(Venue venue) {
        return VenueResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .capacity(venue.getCapacity())
                .location(venue.getLocation())
                .image(venue.getImage())
                .verified(venue.getVerified())
                .venueStyleName(venue.getVenueStyle().getName())
                .ownerName(venue.getOwner().getName())
                .build();
    }
}
