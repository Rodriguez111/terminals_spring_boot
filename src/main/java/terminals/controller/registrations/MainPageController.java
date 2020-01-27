package terminals.controller.registrations;

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
public class MainPageController {

    private RegService regService;

    private RegSortService regSortService;

    @RequestMapping(value = "/mainpage", method = RequestMethod.POST)
    protected void getRegs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader br = req.getReader();
        StringBuilder sb = new StringBuilder();
        String read = "";
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        String requestFromClient = sb.toString();
        String jsonStringToClient = "";

        if (requestFromClient.contains("filterSelect")) {
            jsonStringToClient = regService.findEntriesByFilter(requestFromClient);
        } else if (requestFromClient.equals("getAllRegs")) {
            jsonStringToClient = regService.findAllEntriesForTheLastDay();
        } else if (requestFromClient.contains("sortEntries")) {
            jsonStringToClient = regSortService.sortEntries(requestFromClient);
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(jsonStringToClient);
        printWriter.flush();
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
