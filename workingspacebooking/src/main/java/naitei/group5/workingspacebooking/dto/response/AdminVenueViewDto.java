package naitei.group5.workingspacebooking.dto.response;

// Wrapper DTO để hiển thị venue trong admin view
public record AdminVenueViewDto(
        VenueResponseDto venue,
        String ownerName,
        Boolean deleted
) {
    public static AdminVenueViewDto from(VenueResponseDto venue, String ownerName, Boolean deleted) {
        return new AdminVenueViewDto(venue, ownerName, deleted);
    }
}
