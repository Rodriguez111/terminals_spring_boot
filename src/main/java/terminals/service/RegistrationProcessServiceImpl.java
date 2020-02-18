package terminals.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import terminals.models.Registration;
import terminals.models.Terminal;
import terminals.models.User;
import terminals.repository.RegistrationRepository;
import terminals.repository.TerminalRepository;
import terminals.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationProcessServiceImpl implements RegistrationProcessService {

    private TerminalRepository terminalRepository;
    private UserRepository userRepository;
    private RegistrationRepository registrationRepository;

    private final static String OK = "OK";
    private final static String DEPARTMENTS_NOT_MATCH = "Департаменты терминала и пользователя не совпадают";


    @Override
    public Map<String, String> getTerminalsStatistic() {
        Map<String, String> result = new HashMap<>();
        int totalAmountOfTerminals = (int) terminalRepository.count();
        int amountOfInactiveTerminals = terminalRepository.countAllByTerminalIsActive(false);
        int amountOfGivenTerminals = terminalRepository.countAllByUserIsNotNull();
        int activeTerminalsRemain = totalAmountOfTerminals - amountOfGivenTerminals - amountOfInactiveTerminals;
        result.put("totalAmountOfTerminals", String.valueOf(totalAmountOfTerminals));
        result.put("amountOfInactiveTerminals", String.valueOf(amountOfInactiveTerminals));
        result.put("amountOfGivenTerminals",  String.valueOf(amountOfGivenTerminals));
        result.put("activeTerminalsRemain",  String.valueOf(activeTerminalsRemain));
        return result;
    }

    @Override
    public Map<String, String> validateTerminalInput(Map<String, String> paramsFromClient) {
        String terminalInvId = paramsFromClient.get("validateTerminalInventoryId");

        Map<String, String> result = new HashMap<>();
        Terminal terminal = terminalRepository.findByInventoryId(terminalInvId);

        if (terminal == null) {
            result.put("terminalNotExists", "Терминал с таким номером не существует");
        } else {
            String regId = terminal.getRegId();
            boolean isActive = terminal.isTerminalIsActive();
            if (!isActive) {
                result.put("terminalNotActive", "Терминал " + regId + " деактивирован");
            } else {
                User user = terminal.getUser();
                if (user == null) { //Если этот терминал не выдан
                    result.put("terminalIsReady", "OK"); //тогда терминал идет на выдачу
                    result.put("terminalRegId", regId);
                } else { //иначе идет на прием
                    result = createJsonObjectWithUserInfo(user);
                    result.put("terminalRegId", regId);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, String> validateUserInputForGiving(Map<String, String> paramsFromClient) {
        String login = paramsFromClient.get("userInputLogin");
        String terminalRegId = paramsFromClient.get("terminalRegId");
        String adminGaveLogin = paramsFromClient.get("adminGaveLogin");
        User adminGave = userRepository.findByUserLogin(adminGaveLogin);
        Map<String, String> result = new HashMap<>();
        User whoEnterUser = userRepository.findByUserLogin(login);
        if (whoEnterUser == null) {
            result.put("userNotExists", "Пользователь с таким логином не существует");
        } else {
            boolean isActive = whoEnterUser.isActive();
            if (!isActive) { //если пользователь неактивен
                result.put("userNotActive", "Пользователь " + login + " деактивирован");
            } else {
                Terminal terminal = whoEnterUser.getTerminal();
                if (terminal != null && !terminal.getRegId().equals(terminalRegId)) { //если у этого пользователя уже есть другой терминал

                    result.put("userAlreadyHaveTerminal", "За пользователем уже зарегистрирован терминал " + terminal.getRegId());
                } else {
                    Terminal takenTerminal = terminalRepository.findByRegId(terminalRegId);
                    String departmentResult = checkDepartmentAffiliation(takenTerminal, whoEnterUser);
                    if (departmentResult.equals(OK)) { //если департаменты совпадают

                        Registration entry = new Registration(takenTerminal, whoEnterUser, adminGave);
                        Registration entryWithStartDate = setCurrentTimeStampForStartDate(entry);
                        registrationRepository.save(entryWithStartDate);
                        takenTerminal.setUser(whoEnterUser);
                        whoEnterUser.setTerminal(takenTerminal);
                        terminalRepository.save(takenTerminal);
                        userRepository.save(whoEnterUser);
                        result = createJsonObjectWithUserInfo(whoEnterUser);
                        result.put("terminalGivingSuccess", "OK");
                    } else {
                        result.put("departmentsNotMatch", departmentResult);
                    }
                }
            }
        }
        return result;
    }

    private String checkDepartmentAffiliation(Terminal terminal, User user) {
        String result = OK;
        if (terminal.getDepartment() != null && user.getDepartment() == null) {
            result = DEPARTMENTS_NOT_MATCH;
        } else if (terminal.getDepartment() == null && user.getDepartment() != null || terminal.getDepartment() == null && user.getDepartment() == null) {
            result = OK;
        } else if (!terminal.getDepartment().equals(user.getDepartment())) {
            result = DEPARTMENTS_NOT_MATCH;
        }
        return result;
    }


    public Map<String, String> validateUserInputForReceiving(Map<String, String> paramsFromClient) {
        String login = paramsFromClient.get("userInputLogin");
        String terminalRegId = paramsFromClient.get("terminalRegId");
        String adminReceivedLogin = paramsFromClient.get("adminReceivedLogin");
        User adminGot = userRepository.findByUserLogin(adminReceivedLogin);
        Map<String, String> result = new HashMap<>();
        User whoEnterUser = userRepository.findByUserLogin(login);
        if (whoEnterUser == null) {
            result.put("userNotExists", "Пользователь с таким логином не существует");
        } else {
            if (whoEnterUser.getTerminal() == null) { //пользователь существует, но у него нет терминала
                result.put("doNotHaveTerminal", "Этот пользователь не брал терминал");
            } else if (!whoEnterUser.getTerminal().getRegId().equals(terminalRegId)) { //если у этого пользователя уже есть другой терминал
                result.put("userNotMatch", "За этим пользователем зарегистрирован другой терминал: " + whoEnterUser.getTerminal().getRegId());
            } else {
                Terminal terminal = whoEnterUser.getTerminal();
                Registration registration = registrationRepository.findByUserIdAndTerminalIdAndEndDateIsNull(whoEnterUser.getId(), whoEnterUser.getTerminal().getId());
                if (registration != null) {
                    Registration registrationForUpdate = setCurrentTimeStampForEndDate(registration);
                    registrationForUpdate.setAdminGot(adminGot);
                    registrationRepository.save(registrationForUpdate);
                    removeTerminalFromUserAndSave(whoEnterUser);
                    removeUserFromTerminalAndSave(terminal);
                    result = createJsonObjectWithUserInfo(whoEnterUser);
                    result.put("terminalReceivingSuccess", "OK");
                } else {
                    result.put("fatalErrorRecordNotFound", "Fatal error: запись не найдена");
                }
            }
        }
        return result;
    }

    private Registration setCurrentTimeStampForStartDate(Registration registration) {
        String currentTimeStamp = generateCurrentTimeStamp();
        registration.setStartDate(currentTimeStamp);
        return registration;
    }

    private Registration setCurrentTimeStampForEndDate(Registration registration) {
        String currentTimeStamp = generateCurrentTimeStamp();
        registration.setEndDate(currentTimeStamp);
        return registration;
    }

    private String generateCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return formatter.format(now);
    }

    private void removeTerminalFromUserAndSave(User user) {
        user.setTerminal(null);
        userRepository.save(user);
    }

    private void removeUserFromTerminalAndSave(Terminal terminal) {
        terminal.setUser(null);
        terminalRepository.save(terminal);
    }

    private Map<String, String> createJsonObjectWithUserInfo(User user) {
        Map<String, String> result = getTerminalsStatistic();
        result.put("login", user.getUserLogin());
        result.put("name", user.getUserName());
        result.put("surname", user.getUserSurname());
        return result;
    }

    @Autowired
    public void setTerminalRepository(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRegistrationRepository(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }
}
