package naitei.group5.workingspacebooking.utils;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.PriceResponseDto;
import naitei.group5.workingspacebooking.dto.response.BookingDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.BookingResponseDto;
import naitei.group5.workingspacebooking.dto.response.PriceDto;
import naitei.group5.workingspacebooking.dto.response.TimeSlotResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.dto.request.UpdateVenueRequestDto;
import naitei.group5.workingspacebooking.entity.*;
import naitei.group5.workingspacebooking.entity.enums.WeekDay;
import naitei.group5.workingspacebooking.dto.response.VenueDetailRenterResponseDto;
import naitei.group5.workingspacebooking.dto.response.BusySlotDto;


import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public final class ConverterDto {

    // Venue
    public static Venue toVenueEntity(CreateVenueRequestDto req, User owner, VenueStyle venueStyle) {
        return Venue.builder()
                .name(req.name())
                .description(req.description())
                .capacity(req.capacity())
                .location(req.location())
                .image(req.image())
                .verified(false)
                .owner(owner)
                .venueStyle(venueStyle)
                .build();
    }

    // Owner: full info
    public static VenueResponseDto toVenueResponseDto(Venue v) {
        Integer styleId = v.getVenueStyle() != null ? v.getVenueStyle().getId() : null;
        String styleName = v.getVenueStyle() != null ? v.getVenueStyle().getName() : null;

        List<PriceResponseDto> priceResponses =
                (v.getPrices() != null)
                        ? v.getPrices().stream()
                        .map(ConverterDto::toPriceResponseDto)
                        .toList()
                        : Collections.emptyList();

        return new VenueResponseDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                v.getImage(),
                priceResponses,
                styleId,
                styleName
        );
    }

    // Renter: chỉ trả prices thỏa time/weekDays
    public static VenueResponseDto toVenueResponseDto(
            Venue v,
            LocalTime filterStart,
            LocalTime filterEnd,
            List<WeekDay> filterDays
    ) {
        Integer styleId = v.getVenueStyle() != null ? v.getVenueStyle().getId() : null;
        String styleName = v.getVenueStyle() != null ? v.getVenueStyle().getName() : null;

        // nếu filterDays null hoặc rỗng thì bỏ qua filter theo ngày
        Set<WeekDay> daySet = (filterDays == null || filterDays.isEmpty())
                ? null
                : new HashSet<>(filterDays);

        // Nếu start > end thì coi như không filter theo time
        boolean validTimeRange = (filterStart != null && filterEnd != null && !filterStart.isAfter(filterEnd));

        List<PriceResponseDto> filteredPrices = (v.getPrices() == null)
                ? Collections.emptyList()
                : v.getPrices().stream()
                .filter(p -> {
                    boolean dayOk = (daySet == null) || daySet.contains(p.getDayOfWeek());

                    boolean timeOk = true;
                    if (validTimeRange) {
                        // price phải bao ngoài khoảng yêu cầu
                        timeOk = !p.getTimeStart().isAfter(filterStart)
                                && !p.getTimeEnd().isBefore(filterEnd);
                    }
                    return dayOk && timeOk;
                })
                .map(ConverterDto::toPriceResponseDto)
                .collect(Collectors.toList());

        return new VenueResponseDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                v.getImage(),
                filteredPrices,
                styleId,
                styleName
        );
    }

    public static VenueDetailResponseDto toVenueDetailResponseDto(
        Venue v,
        List<TimeSlotResponseDto> availableSlots,
        List<TimeSlotResponseDto> busySlots
    ) {
        return new VenueDetailResponseDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getImage(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                v.getVenueStyle() != null ? v.getVenueStyle().getName() : null,
                v.getPrices() != null ? v.getPrices().stream().map(ConverterDto::toPriceDto).toList() : List.of(),
                v.getBookings() != null ? v.getBookings().stream().map(ConverterDto::toBookingDto).toList() : List.of(),
                availableSlots,
                busySlots
        );
    }

    // renter venue detail
    public static VenueDetailRenterResponseDto toVenueDetailRenterResponseDto(
            Venue v,
            List<BusySlotDto> busySlots
    ) {
        List<PriceResponseDto> prices =
                (v.getPrices() != null)
                        ? v.getPrices().stream().map(ConverterDto::toPriceResponseDto).toList()
                        : List.of();

        return new VenueDetailRenterResponseDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                v.getImage(),
                v.getVenueStyle() != null ? v.getVenueStyle().getName() : null,
                prices,
                busySlots
        );
    }

    // Price
    public static PriceDto toPriceDto(Price p) {
        return new PriceDto(
                p.getDayOfWeek().name(),
                p.getTimeStart(),
                p.getTimeEnd(),
                p.getPrice().doubleValue()
        );
    }

    //Booking
    public static BookingResponseDto toBookingDto(Booking b) {
        return new BookingResponseDto(
                b.getId(),
                b.getUser() != null ? b.getUser().getId() : null,
                b.getStatus().name(),
                b.getCreatedAt(),
                b.getBookingDetails() != null ? b.getBookingDetails().stream().map(ConverterDto::toBookingDetailDto).toList() : List.of()
        );
    }

     public static BookingDetailResponseDto toBookingDetailDto(BookingDetail bd) {
        return new BookingDetailResponseDto(
                bd.getId(),
                bd.getStartTime(),
                bd.getEndTime()
        );
    }

    public static PriceResponseDto toPriceResponseDto(Price price) {
        return new PriceResponseDto(
                price.getId(),
                price.getDayOfWeek(),
                price.getTimeStart(),
                price.getTimeEnd(),
                price.getPrice()
        );
    }

    public static void updateVenueFromDto(Venue venue, CreateVenueRequestDto req, VenueStyle venueStyle) {
        venue.setName(req.name());
        venue.setDescription(req.description());
        venue.setCapacity(req.capacity());
        venue.setLocation(req.location());
        venue.setImage(req.image());
        if (venueStyle != null) {
            venue.setVenueStyle(venueStyle);
        }
    }

    private ConverterDto() {
        throw new UnsupportedOperationException("Utility class không thể được khởi tạo");
    }

    public static void updateVenueFromDto(Venue venue, UpdateVenueRequestDto req, VenueStyle venueStyle) {
    venue.setName(req.getName());
    venue.setDescription(req.getDescription());
    venue.setCapacity(req.getCapacity());
    venue.setLocation(req.getLocation());
    venue.setImage(req.getImage());
    if (venueStyle != null) {
        venue.setVenueStyle(venueStyle);
    }
    }
}
