package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVenueRequestDto {

    @NotBlank(message = "Tên venue là bắt buộc")
    @Size(min = 2, max = 100, message = "Tên venue phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Mô tả venue là bắt buộc")
    @Size(min = 10, max = 5000, message = "Mô tả venue phải từ 10 đến 5000 ký tự")
    private String description;

    @NotNull(message = "Sức chứa là bắt buộc")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;

    @NotBlank(message = "Vị trí là bắt buộc")
    @Size(min = 5, max = 255, message = "Vị trí phải từ 5 đến 255 ký tự")
    private String location;

    @Size(max = 10000, message = "Đường dẫn ảnh không được quá 10000 ký tự")
    private String image;

    @NotNull(message = "ID kiểu venue là bắt buộc")
    private Integer venueStyleId;
}
