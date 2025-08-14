package naitei.group5.workingspacebooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueResponseDto {

    private Integer id;
    private String name;
    private String description;
    private Integer capacity;
    private String location;
    private String image;
    private Boolean verified;
    private String venueStyleName;
    private String ownerName;
}
