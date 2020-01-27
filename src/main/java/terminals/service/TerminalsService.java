package terminals.service;

import org.json.JSONObject;
import terminals.models.Terminal;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TerminalsService {

    JSONObject findAllTerminals();

    JSONObject findActiveTerminals();

    JSONObject getCountOfAllTerminals();

    JSONObject getCountOfActiveTerminals();

    JSONObject findTerminalById(int id);

    JSONObject addTerminal(JSONObject jsonObject);

    JSONObject updateTerminal(JSONObject jsonObject);

    JSONObject deleteTerminal(int id);
}
