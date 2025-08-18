package naitei.group5.workingspacebooking.utils;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;

public class ConverterDto {

    public static Venue toVenueEntity(CreateVenueRequestDto requestDto, User owner, VenueStyle venueStyle) {
        return Venue.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .capacity(requestDto.capacity())
                .location(requestDto.location())
                .image(requestDto.image())
                .verified(false) 
                .owner(owner)
                .venueStyle(venueStyle)
                .build();
    }

    public static VenueResponseDto toVenueResponseDto(Venue venue) {
        return new VenueResponseDto(
                venue.getId(),
                venue.getName(),
                venue.getDescription(),
                venue.getCapacity(),
                venue.getLocation(),
                venue.getImage(),
                venue.getVerified(),
                venue.getVenueStyle() != null ? venue.getVenueStyle().getName() : null,
                venue.getOwner() != null ? venue.getOwner().getName() : null
        );
    }

    public static void updateVenueFromDto(Venue venue, CreateVenueRequestDto requestDto, VenueStyle venueStyle) {
        venue.setName(requestDto.name());
        venue.setDescription(requestDto.description());
        venue.setCapacity(requestDto.capacity());
        venue.setLocation(requestDto.location());
        venue.setImage(requestDto.image());
        
        if (venueStyle != null) {
            venue.setVenueStyle(venueStyle);
        }
    }

    private ConverterDto() {
        throw new UnsupportedOperationException("Utility class không thể được khởi tạo");
    }
}
