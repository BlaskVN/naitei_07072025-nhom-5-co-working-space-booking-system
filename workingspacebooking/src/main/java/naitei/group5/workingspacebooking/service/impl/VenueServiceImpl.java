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
import naitei.group5.workingspacebooking.utils.ConverterDto;

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

        VenueStyle venueStyle = venueStyleRepository.findById(requestDto.venueStyleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue style not found with ID: " + requestDto.venueStyleId()));

        Venue venue = ConverterDto.toVenueEntity(requestDto, owner, venueStyle);

        Venue savedVenue = venueRepository.save(venue);
        
        return ConverterDto.toVenueResponseDto(savedVenue);
    }
}
