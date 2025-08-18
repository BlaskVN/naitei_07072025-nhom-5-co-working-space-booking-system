package naitei.group5.workingspacebooking.dto.response;

public record VenueResponseDto(
        Integer id,
        String name,
        String description,
        Integer capacity,
        String location,
        String image,
        Boolean verified,
        String venueStyleName,
        String ownerName
) {
}
