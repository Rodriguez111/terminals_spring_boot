package terminals.controller.registrations;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import terminals.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class RegistrationsController {

    private RegistrationProcessService registrationProcessService;


    @RequestMapping(value = "/registrations", method = RequestMethod.POST)
    protected void RegistrationsPageHandler(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader br = req.getReader();
        StringBuilder sb = new StringBuilder();
        String read = "";
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        String requestFromClient = sb.toString();
        JSONObject result = new JSONObject();
        if (requestFromClient.equals("getStatisticForRegistrationsPage")) {
            result = registrationProcessService.getTerminalsStatistic();
        } else if (requestFromClient.contains("validateTerminalInventoryId")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = registrationProcessService.validateTerminalInput(jsonFromClient);
        } else if (requestFromClient.contains("validateUserInputForGiving")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = registrationProcessService.validateUserInputForGiving(jsonFromClient);
        } else if (requestFromClient.contains("validateUserInputForReceiving")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = registrationProcessService.validateUserInputForReceiving(jsonFromClient);
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(result);
        printWriter.flush();
    }

    @Autowired
    public void setRegistrationProcessService(RegistrationProcessService registrationProcessService) {
        this.registrationProcessService = registrationProcessService;
    }
}
