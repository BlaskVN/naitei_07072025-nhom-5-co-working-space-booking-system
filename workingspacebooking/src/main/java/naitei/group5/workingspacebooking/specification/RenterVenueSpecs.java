package naitei.group5.workingspacebooking.specification;

import jakarta.persistence.criteria.*;
import naitei.group5.workingspacebooking.entity.Price;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import naitei.group5.workingspacebooking.entity.enums.WeekDay;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class RenterVenueSpecs {
    private RenterVenueSpecs() {}

    public static Specification<Venue> byFilter(
            String name,
            String location,
            String venueStyleName,
            Integer venueStyleId,
            Integer capacityMin,
            Integer capacityMax,
            LocalTime startTime,
            LocalTime endTime,
            List<WeekDay> weekDays
    ) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            // chỉ venue đã verified
            ps.add(cb.isTrue(root.get("verified")));

            // name
            if (name != null && !name.isBlank()) {
                String like = "%" + name.trim().toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("name")), like));
            }

            // location
            if (location != null && !location.isBlank()) {
                String like = "%" + location.trim().toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("location")), like));
            }

            // style theo id hoặc name
            Join<Venue, VenueStyle> styleJoin = null;
            if (venueStyleId != null || (venueStyleName != null && !venueStyleName.isBlank())) {
                styleJoin = root.join("venueStyle", JoinType.LEFT);
                if (venueStyleId != null) {
                    ps.add(cb.equal(styleJoin.get("id"), venueStyleId));
                }
                if (venueStyleName != null && !venueStyleName.isBlank()) {
                    String like = "%" + venueStyleName.trim().toLowerCase() + "%";
                    ps.add(cb.like(cb.lower(styleJoin.get("name")), like));
                }

                if (query != null) {
                    query.distinct(true);
                }
            }

            // capacity
            if (capacityMin != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("capacity"), capacityMin));
            }
            if (capacityMax != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("capacity"), capacityMax));
            }

            // lọc theo Price (time và/hoặc weekDays)
            boolean needPriceJoin = (startTime != null && endTime != null)
                    || (weekDays != null && !weekDays.isEmpty());

            if (needPriceJoin) {
                Join<Venue, Price> priceJoin = root.join("prices", JoinType.INNER);

                if (startTime != null && endTime != null) {
                    Predicate timeWraps = cb.and(
                            cb.lessThanOrEqualTo(priceJoin.get("timeStart"), startTime),
                            cb.greaterThanOrEqualTo(priceJoin.get("timeEnd"), endTime)
                    );
                    ps.add(timeWraps);
                }

                if (weekDays != null && !weekDays.isEmpty()) {
                    CriteriaBuilder.In<WeekDay> inDays = cb.in(priceJoin.get("dayOfWeek"));
                    for (WeekDay d : weekDays) inDays.value(d);
                    ps.add(inDays);
                }

                if (query != null) {
                    query.distinct(true);
                }   
            }

            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
