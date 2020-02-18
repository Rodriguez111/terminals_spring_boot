package terminals.service;

import org.json.JSONObject;
import terminals.models.Terminal;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface TerminalsService {

    List<Terminal> findAllTerminals();

    List<Terminal> findActiveTerminals();

    long getCountOfAllTerminals();

    int getCountOfActiveTerminals();

    Terminal findTerminalById(String id);

    Map<String, String> addTerminal(Map<String, String> terminalParams);

    Map<String, String> updateTerminal(String id, Map<String, String> terminalParams);

    Map<String, String> deleteTerminal(String id);
}
