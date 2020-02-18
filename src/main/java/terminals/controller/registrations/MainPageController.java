package terminals.controller.registrations;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import terminals.models.Registration;
import terminals.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mainpage_controller")
public class MainPageController {

    private RegService regService;

    private RegSortService regSortService;

    @GetMapping
    @RequestMapping("/all")
    List<Registration> getAllRegs() {
       return regService.findAllEntriesForTheLastDay();
    }

    @PostMapping
    @RequestMapping("/by_filter")
    List<Registration> getAllRegs(@RequestBody Map<String, String> params) {
        return regService.findEntriesByFilter(params);
    }

    @PostMapping
    @RequestMapping("/sort")
    List<Registration> sortRegs(@RequestBody String jsonObject) {
        return regSortService.sortEntries(jsonObject);
    }




    @Autowired
    public void setRegService(RegService regService) {
        this.regService = regService;
    }

    @Autowired
    public void setRegSortService(RegSortService regSortService) {
        this.regSortService = regSortService;
    }
}
