package terminals.service;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import terminals.models.Department;
import terminals.models.Registration;
import terminals.models.Terminal;
import terminals.repository.DepartmentRepository;
import terminals.repository.RegistrationRepository;
import terminals.repository.TerminalRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TerminalsServiceImpl implements TerminalsService {

    private final static String REG_ID_EXISTS = "Терминал с таким учетным номером уже существует";
    private final static String INVENTORY_ID_EXISTS = "Терминал с таким инвентарным номером уже существует";
    private final static String SERIAL_ID_EXISTS = "Терминал с таким серийным номером уже существует";
    private final static String OK = "OK";
    private final static String NO_FIELDS_FOR_UPDATE = "Нет полей для обновления";
    private final static String CAN_NOT_DEACTIVATE = "Нельзя деактивировать терминал, пока он выдан пользователю";
    private final static String CAN_NOT_CHANGE = "Нельзя сменить департамент терминала, пока он выдан пользователю";
    private final static String UNABLE_DELETE = "Невозможно удалить пользователя, если существуют зависимые записи";

    private RegistrationRepository registrationRepository;
    private TerminalRepository terminalRepository;
    private DepartmentRepository departmentRepository;

    private List<Terminal> getListOfAllTerminals() {
        List<Terminal> listOfTerminals = (List<Terminal>) terminalRepository.findAll();
        removeUsersFromTerminalsToAvoidRecursiveError(listOfTerminals);
        sortTerminalsByRegId(listOfTerminals);
        return listOfTerminals;
    }

    private void removeUsersFromTerminalsToAvoidRecursiveError(List<Terminal> terminals) {
        terminals.forEach(t -> {
            if (t.getUser() != null) {
                t.getUser().setTerminal(null);
            }
        });
    }

    private void sortTerminalsByRegId(List<Terminal> terminals) {
        terminals.sort(new Comparator<Terminal>() {
            @Override
            public int compare(Terminal o1, Terminal o2) {
                Integer first = 0;
                int second = 0;
                try {
                    first = Integer.parseInt(o1.getRegId().substring(2));
                    second = Integer.parseInt(o2.getRegId().substring(2));
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный формат учетного номера терминала");
                    e.printStackTrace();
                }
                return first.compareTo(second);
            }
        });
    }

    @Override
    public JSONObject findAllTerminals() {
        List<Terminal> terminals = getListOfAllTerminals();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfTerminals", terminals);
        return jsonObject;
    }

    @Override
    public JSONObject findActiveTerminals() {
        List<Terminal> terminals = getListOfAllTerminals();
        List<Terminal> resultList = terminals.stream().filter(Terminal::isTerminalIsActive).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfTerminals", resultList);
        return jsonObject;
    }

    @Override
    public JSONObject findTerminalById(int id) {
        Terminal terminal = terminalRepository.findById(id).get();
        List<Terminal> terminals = new ArrayList<>();
        terminals.add(terminal);
        removeUsersFromTerminalsToAvoidRecursiveError(terminals);
        return new JSONObject(terminal);
    }

    @Transactional
    @Override
    public JSONObject addTerminal(JSONObject jsonObject) {
        String resultMessage;
        JSONObject params = jsonObject.getJSONObject("addTerminal");
        String terminalRegId = params.getString("regId");
        String terminalModel = params.getString("model");
        String terminalSerialId = params.getString("serialId");
        String terminalInventoryId = params.getString("inventoryId");
        String terminalComment = params.getString("comment");
        Department department = departmentRepository.findByDepartment(params.getString("department"));
        boolean terminalIsActive = params.getBoolean("isActive");
        Terminal terminalToCheck = new Terminal(terminalRegId, terminalSerialId, terminalInventoryId);
        resultMessage = terminalExists(terminalToCheck);
        if (resultMessage.equals(OK)) {
            terminalToCheck.setDepartment(department);
            terminalToCheck.setTerminalModel(terminalModel);
            terminalToCheck.setTerminalComment(terminalComment);
            terminalToCheck.setTerminalIsActive(terminalIsActive);
            Terminal terminalToAdd = setCurrentTimeStampForCreateDate(terminalToCheck);
            terminalRepository.save(terminalToAdd);
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("terminalAddResult", resultMessage);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject updateTerminal(JSONObject jsonObject) {
        JSONObject params = jsonObject.getJSONObject("updateTerminal");
        String result = OK;
        Optional<Terminal> optionalTerminal = terminalRepository.findById(params.getInt("id"));
        if (optionalTerminal.isPresent()) {
            Terminal terminal = optionalTerminal.get();
            Terminal updatedTerminal = updateTerminalFields(terminal, params);
            if (terminal.equals(updatedTerminal)) {
                result = NO_FIELDS_FOR_UPDATE;
            } else if (terminal.isTerminalIsActive() != updatedTerminal.isTerminalIsActive() && terminal.getUser() != null) {
                result = CAN_NOT_DEACTIVATE;
            } else if (terminal.getUser() != null
                    && (
                    (terminal.getDepartment() == null && updatedTerminal.getDepartment() != null)
                            || (terminal.getDepartment() != null && updatedTerminal.getDepartment() == null)
                            || (terminal.getDepartment() != null && updatedTerminal.getDepartment() != null
                            && !terminal.getDepartment().equals(updatedTerminal.getDepartment())))) {
                result = CAN_NOT_CHANGE;
            } else {
                Terminal terminalToUpdate = setCurrentTimeStampForUpdateDate(updatedTerminal);
                terminalRepository.save(terminalToUpdate);
            }
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("terminalUpdateResult", result);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject deleteTerminal(int id) {
        String result = UNABLE_DELETE;
        if (!checkDependency(id)) {
            Optional<Terminal> optionalTerminal = terminalRepository.findById(id);
            optionalTerminal.ifPresent(terminal -> terminalRepository.delete(terminal));
            result = OK;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("terminalDeleteResult", result);
        return jsonObject;
    }

    private boolean checkDependency(int id) {
        boolean result = false;
        Registration registration = registrationRepository.findFirstByTerminalId(id);
        if (registration != null) {
            result = true;
        }
        return result;
    }

    private Terminal updateTerminalFields(Terminal terminal, JSONObject jsonObject) {
        Terminal updatedTerminal = terminal.clone();
        if (validateField(jsonObject.getString("regId"))) {
            updatedTerminal.setRegId(jsonObject.getString("regId"));
        }
        if (validateField(jsonObject.getString("model"))) {
            updatedTerminal.setTerminalModel(jsonObject.getString("model"));
        }
        if (validateField(jsonObject.getString("serialId"))) {
            updatedTerminal.setSerialId(jsonObject.getString("serialId"));
        }
        if (validateField(jsonObject.getString("inventoryId"))) {
            updatedTerminal.setInventoryId(jsonObject.getString("inventoryId"));
        }
        if (validateField(jsonObject.getString("comment"))) {
            updatedTerminal.setTerminalComment(jsonObject.getString("comment"));
        }

        Department department = departmentRepository.findByDepartment(jsonObject.getString("department"));
        updatedTerminal.setDepartment(department);

        updatedTerminal.setTerminalIsActive(jsonObject.getBoolean("isActive"));
        return updatedTerminal;
    }

    private boolean validateField(String value) {
        return value != null && !value.equals("");
    }

    @Override
    public JSONObject getCountOfAllTerminals() {
        long count = terminalRepository.count();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countOfAllTerminals", count);
        return jsonObject;
    }

    @Override
    public JSONObject getCountOfActiveTerminals() {
        int count = terminalRepository.countAllByTerminalIsActive(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countOfActiveTerminals", count);
        return jsonObject;
    }

    private String terminalExists(Terminal terminal) {
        String result = regIdExists(terminal.getRegId());
        if (result.equals(OK)) {
            result = serialIdExists(terminal.getSerialId());
        }
        if (result.equals(OK)) {
            result = inventoryIdExists(terminal.getInventoryId());
        }
        return result;
    }

    private String regIdExists(String regId) {
        String result = OK;
        Terminal terminal = terminalRepository.findByRegId(regId);
        if (terminal != null) {
            result = REG_ID_EXISTS;
        }
        return result;
    }

    private String inventoryIdExists(String inventoryId) {
        String result = OK;
        Terminal terminal = terminalRepository.findByInventoryId(inventoryId);
        if (terminal != null) {
            result = INVENTORY_ID_EXISTS;
        }
        return result;
    }

    private String serialIdExists(String serialId) {
        String result = OK;
        Terminal terminal = terminalRepository.findBySerialId(serialId);
        if (terminal != null) {
            result = SERIAL_ID_EXISTS;
        }
        return result;
    }

    private Terminal setCurrentTimeStampForCreateDate(Terminal terminal) {
        String currentTimeStamp = generateCurrentTimeStamp();
        terminal.setCreateDate(currentTimeStamp);
        return terminal;
    }

    private Terminal setCurrentTimeStampForUpdateDate(Terminal terminal) {
        String currentTimeStamp = generateCurrentTimeStamp();
        terminal.setLastUpdateDate(currentTimeStamp);
        return terminal;
    }

    private String generateCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return formatter.format(now);
    }


    @Autowired
    public void setTerminalRepository(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setRegistrationRepository(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }
}
