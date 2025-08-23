package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRenterRequestDto;
import naitei.group5.workingspacebooking.dto.response.*;
import naitei.group5.workingspacebooking.specification.RenterVenueSpecs;
import naitei.group5.workingspacebooking.specification.VenueSpecs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.UpdateVenueRequestDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.exception.custom.*;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.repository.VenueStyleRepository;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.service.VenueService;
import naitei.group5.workingspacebooking.service.common.TimeSlotCalculator;
import naitei.group5.workingspacebooking.utils.ConverterDto;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueStyleRepository venueStyleRepository;
    private final UserRepository userRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final MessageSource messageSource;

    private final TimeSlotCalculator timeSlotCalculator;

    // ==== Owner use cases ====
    @Override
    public List<Venue> getVenuesByOwner(Integer ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }
        return venueRepository.findByOwnerIdAndDeletedFalse(ownerId);
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

    @Override
    public List<VenueResponseDto> filterOwnerVenues(FilterVenueRequestDto req) {
        if (req.ownerId() == null) {
            throw new OwnerIdRequiredException();
        }
        if (!userRepository.existsById(req.ownerId())) {
            throw new UserNotFoundException();
        }
        if (req.capacityMin() != null && req.capacityMax() != null
                && req.capacityMin() > req.capacityMax()) {
            throw new InvalidCapacityRangeException();
        }

        var spec = VenueSpecs.byFilter(
                req.ownerId(),
                req.name(),
                req.location(),
                req.venueStyleId(),
                req.venueStyleName(),
                req.capacityMin(),
                req.capacityMax(),
                req.verified()
        );

        List<Venue> venues = venueRepository.findAll(spec);
        return venues.stream()
                .map(ConverterDto::toVenueResponseDto)
                .toList();
    }

    // ==== Renter use cases ====
    @Override
    public List<VenueResponseDto> getVerifiedVenues() {
        return venueRepository.findByVerifiedAndDeletedFalse(true)
                .stream()
                .map(ConverterDto::toVenueResponseDto)
                .toList();
    }

    // Owner xem chi tiết venue (bao gồm busy slots & available slots)
    @Override
    public VenueDetailResponseDto getVenueDetailByOwner(Integer ownerId, Integer venueId) {
        var venue = venueRepository.findByIdAndOwnerIdWithAllDetails(venueId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue not found with ID: " + venueId + " for owner: " + ownerId));

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end   = start.plusDays(7);

        // Busy (CONFIRMED)
        List<TimeSlotResponseDto> busy = bookingDetailRepository
                .findBusySlotsByVenueAndTimeRange(venueId, start, end)
                .stream()
                .map(bd -> new TimeSlotResponseDto(bd.getStartTime(), bd.getEndTime()))
                .toList();

        var mergedBusy     = timeSlotCalculator.mergeBusy(start, end, busy);
        var availableSlots = timeSlotCalculator.calcAvailable(start, end, mergedBusy);

        return ConverterDto.toVenueDetailResponseDto(venue, availableSlots, mergedBusy);
    }

    // Renter filter venue theo thời gian, tuần, sức chứa
    @Override
    public List<VenueResponseDto> filterVenuesForRenter(FilterVenueRenterRequestDto req) {
        if (req.capacityMin() != null && req.capacityMax() != null
                && req.capacityMin() > req.capacityMax()) {
            throw new InvalidCapacityRangeException();
        }

        if (req.startTime() != null && req.endTime() != null
                && req.startTime().isAfter(req.endTime())) {
            throw new InvalidTimeRangeException();
        }

        var spec = RenterVenueSpecs.byFilter(
                req.name(),
                req.location(),
                req.venueStyleName(),
                req.venueStyleId(),
                req.capacityMin(),
                req.capacityMax(),
                req.startTime(),
                req.endTime(),
                req.weekDays()
        );

        List<Venue> venues = venueRepository.findAll(spec);

        // Trả về chỉ các price khớp time/weekDays
        return venues.stream()
                .map(v -> ConverterDto.toVenueResponseDto(
                        v,
                        req.startTime(),
                        req.endTime(),
                        req.weekDays()
                ))
                .toList();
    }

    @Override
    @Transactional
    public VenueResponseDto updateVenue(Integer ownerId, Integer venueId, UpdateVenueRequestDto request) {

        Venue venue = venueRepository.findByIdAndOwnerIdWithAllDetails(venueId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found or not owned by you"));

        VenueStyle venueStyle = venueStyleRepository.findById(request.getVenueStyleId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue style not found"));

        ConverterDto.updateVenueFromDto(venue, request, venueStyle);

        Venue updated = venueRepository.save(venue);

        return ConverterDto.toVenueResponseDto(updated);
    }

    public VenueDetailRenterResponseDto getVenueDetail(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(VenueNotFoundException::new);

        // 1. Lấy tất cả prices của venue
        // var prices = venue.getPrices().stream()
        //         .map(ConverterDto::toPriceResponseDto)
        //         .toList();

        // 2. Lấy tất cả booking detail (booked hoặc pending)
        var busySlots = bookingDetailRepository.findByVenueId(venueId)
                .stream()
                .filter(b -> b.getBooking().getStatus().name().equalsIgnoreCase("BOOKED")
                        || b.getBooking().getStatus().name().equalsIgnoreCase("PENDING"))
                .map(b -> new BusySlotDto(
                        b.getStartTime(),
                        b.getEndTime(),
                        b.getBooking().getStatus().name()
                ))
                .toList();

        // 3. Trả về DTO riêng cho renter
        return ConverterDto.toVenueDetailRenterResponseDto(venue, busySlots);
    }

   @Override
   @Transactional
    public VenueSoftDeleteResponseDto softDeleteVenue(Integer venueId, JwtUserDetails userDetails) {
        Integer ownerId = userDetails.getId();

        // 1. Kiểm tra venue tồn tại và thuộc owner
        Venue venue = venueRepository.findByIdAndOwnerIdWithAllDetails(venueId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                         getMessage("venue.softdelete.notfound", venueId, ownerId)
                ));

        // 2. Cập nhật deleted flag + timestamp
        int updated = venueRepository.softDeleteByIdAndOwnerId(venueId, ownerId);
        if (updated == 0) {
            throw new IllegalStateException("Venue does not exist or does not belong to this owner");
        }

        // 3. Trả về response
        return new VenueSoftDeleteResponseDto(
                venue.getId(),
                venue.getName(),
                true,
                getMessage("venue.softdelete.success")
        );
    }
    
    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    // ==== Admin use cases ====
    @Override
    public List<AdminVenueViewDto> adminListVenues(String name, String status, String sort) {
        // Chuẩn hóa status
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            String trimmed = status.trim().toLowerCase();
            if ("unverified".equals(trimmed) || "verified".equals(trimmed) || "deleted".equals(trimmed)) {
                normalizedStatus = trimmed;
            }
        }

        // Chuẩn hóa sort
        String normalizedSort = "name_asc";
        if (sort != null && !sort.isBlank()) {
            String trimmed = sort.trim().toLowerCase();
            if ("name_asc".equals(trimmed) || "name_desc".equals(trimmed) || 
                "capacity_asc".equals(trimmed) || "capacity_desc".equals(trimmed)) {
                normalizedSort = trimmed;
            }
        }

        // Build specification sử dụng VenueSpecs hiện có
        var spec = VenueSpecs.byAdminFilter(name, normalizedStatus);

        // Build sort object
        Sort sortObject = buildSortObject(normalizedSort);

        // Find all venues with specification and sort
        List<Venue> venues = venueRepository.findAll(spec, sortObject);

        // Map to AdminVenueViewDto sử dụng ConverterDto hiện có
        return venues.stream()
                .map(venue -> AdminVenueViewDto.from(
                    ConverterDto.toVenueResponseDto(venue),
                    venue.getOwner() != null ? venue.getOwner().getName() : null,
                    venue.getDeleted()
                ))
                .toList();
    }

    private Sort buildSortObject(String sortParam) {
        return switch (sortParam) {
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "capacity_asc" -> Sort.by(Sort.Direction.ASC, "capacity");
            case "capacity_desc" -> Sort.by(Sort.Direction.DESC, "capacity");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
    }
    
}
