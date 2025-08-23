package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.AdminVenueViewDto;
import naitei.group5.workingspacebooking.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminVenueController extends BaseAdminController {

    private final VenueService venueService;

    @GetMapping("/venues")
    public String listVenues(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Model model) {
        
        List<AdminVenueViewDto> venues = venueService.adminListVenues(name, status, sort);
        
        model.addAttribute("venues", venues);
        model.addAttribute("currentName", name);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSort", sort);
        
        return "admin/venues/list";
    }
}
