package terminals.service;

import org.json.JSONObject;

import java.util.Map;

public interface RegistrationProcessService {

    Map<String, String> getTerminalsStatistic();

    Map<String, String> validateTerminalInput(Map<String, String> paramsFromClient);

    Map<String, String> validateUserInputForGiving(Map<String, String> paramsFromClient);

    Map<String, String> validateUserInputForReceiving(Map<String, String> paramsFromClient);
}
