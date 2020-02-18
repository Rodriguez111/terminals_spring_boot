package terminals.controller.registrations;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import terminals.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


@RestController
@RequestMapping("/registrations")
public class RegistrationsController {

    private RegistrationProcessService registrationProcessService;


    @GetMapping("/getStatistic")
    public Map<String, String> getTerminalsStatistic() {
        return registrationProcessService.getTerminalsStatistic();
    }

    @PostMapping("/validateTerminalInventoryId")
    public Map<String, String> validateTerminalInventoryId(@RequestBody Map<String, String> paramsFromClient) {
        return registrationProcessService.validateTerminalInput(paramsFromClient);
    }

    @PostMapping("/validateUserInputForGiving")
    public Map<String, String> validateUserInputForGiving(@RequestBody Map<String, String> paramsFromClient) {
        return registrationProcessService.validateUserInputForGiving(paramsFromClient);
    }

    @PostMapping("/validateUserInputForReceiving")
    public Map<String, String> validateUserInputForReceiving(@RequestBody Map<String, String> paramsFromClient) {
        return registrationProcessService.validateUserInputForReceiving(paramsFromClient);
    }

    @Autowired
    public void setRegistrationProcessService(RegistrationProcessService registrationProcessService) {
        this.registrationProcessService = registrationProcessService;
    }
}
