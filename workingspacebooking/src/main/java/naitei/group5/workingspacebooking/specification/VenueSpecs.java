package naitei.group5.workingspacebooking.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class VenueSpecs {
    private VenueSpecs() {}

    public static Specification<Venue> byFilter(
            Integer ownerId,
            String name,
            String location,
            Integer venueStyleId,
            String venueStyleName,
            Integer capacityMin,
            Integer capacityMax,
            Boolean verified
    ) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (ownerId != null) {
                ps.add(cb.equal(root.get("owner").get("id"), ownerId));
            }
            if (name != null && !name.isBlank()) {
                String like = "%" + name.trim().toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("name")), like));
            }
            if (location != null && !location.isBlank()) {
                String like = "%" + location.trim().toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("location")), like));
            }
            if (venueStyleId != null) {
                ps.add(cb.equal(root.get("venueStyle").get("id"), venueStyleId));
            }
            if (venueStyleName != null && !venueStyleName.isBlank()) {
                Join<Venue, VenueStyle> style = root.join("venueStyle", JoinType.LEFT);
                String like = "%" + venueStyleName.trim().toLowerCase() + "%";
                ps.add(cb.like(cb.lower(style.get("name")), like));
                
                if (query != null) {
                    query.distinct(true);
                }
            }
            if (capacityMin != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("capacity"), capacityMin));
            }
            if (capacityMax != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("capacity"), capacityMax));
            }
            if (verified != null) {
                ps.add(cb.equal(root.get("verified"), verified));
            }

            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
