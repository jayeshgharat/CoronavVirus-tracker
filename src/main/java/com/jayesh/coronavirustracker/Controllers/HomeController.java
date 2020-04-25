package com.jayesh.coronavirustracker.Controllers;

import com.jayesh.coronavirustracker.models.LocationStats;
import com.jayesh.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        //model.addAttribute("testName","TEST");
        //model.addAttribute("locationStats", coronaVirusDataService.getAllStats());
        //@49
       List<LocationStats> allStats = coronaVirusDataService.getAllStats();
       int totalReportedCases = allStats.stream().mapToInt(stat-> stat.getLatestTotalCases()).sum();
       int totalNewCases = allStats.stream().mapToInt(stat-> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        String s = "home";
        return s;
    }
}
