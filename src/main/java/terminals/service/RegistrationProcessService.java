package terminals.service;

import org.json.JSONObject;

public interface RegistrationProcessService {

    JSONObject getTerminalsStatistic();

    JSONObject validateTerminalInput(JSONObject jsonFromClient);

    JSONObject validateUserInputForGiving(JSONObject jsonFromClient);

    JSONObject validateUserInputForReceiving(JSONObject jsonFromClient);
}
