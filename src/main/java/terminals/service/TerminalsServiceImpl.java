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
import java.util.*;
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
        removeRecursiveDataFromTerminals(listOfTerminals);
        sortTerminalsByRegId(listOfTerminals);
        return listOfTerminals;
    }

    private void removeRecursiveDataFromTerminals(List<Terminal> terminals) {
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
    public List<Terminal> findAllTerminals() {
        return getListOfAllTerminals();
    }

    @Override
    public List<Terminal> findActiveTerminals() {
        List<Terminal> terminals = getListOfAllTerminals();
        return terminals.stream().filter(Terminal::isTerminalIsActive).collect(Collectors.toList());
    }

    @Override
    public Terminal findTerminalById(String id) {
        int terminalId = Integer.parseInt(id);
        Terminal terminal = terminalRepository.findById(terminalId).get();
        List<Terminal> terminals = new ArrayList<>();
        terminals.add(terminal);
        removeRecursiveDataFromTerminals(terminals);
        return terminal;
    }

    @Transactional
    @Override
    public Map<String, String> addTerminal(Map<String, String> terminalParams) {
        String resultMessage;
        String terminalRegId = terminalParams.get("regId");
        String terminalModel = terminalParams.get("model");
        String terminalSerialId = terminalParams.get("serialId");
        String terminalInventoryId = terminalParams.get("inventoryId");
        String terminalComment = terminalParams.get("comment");
        Department department = departmentRepository.findByDepartment(terminalParams.get("department"));
        boolean terminalIsActive = terminalParams.get("isActive").equals("true");
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
        Map<String, String> result = new HashMap<>();
        result.put("terminalAddResult", resultMessage);
        return result;
    }


    @Transactional
    @Override
    public Map<String, String> updateTerminal(String id, Map<String, String> terminalParams) {
        String resultMessage = OK;
        int terminalId = Integer.parseInt(id);
        Optional<Terminal> optionalTerminal = terminalRepository.findById(terminalId);
        if (optionalTerminal.isPresent()) {
            Terminal terminal = optionalTerminal.get();
            Terminal updatedTerminal = updateTerminalFields(terminal, terminalParams);
            if (terminal.equals(updatedTerminal)) {
                resultMessage = NO_FIELDS_FOR_UPDATE;
            }
            if (resultMessage.equals(OK)) {
                resultMessage = terminalExistsForUpdate(updatedTerminal);
            }
            if (resultMessage.equals(OK)) {
                if (terminal.isTerminalIsActive() != updatedTerminal.isTerminalIsActive() && terminal.getUser() != null) {
                    resultMessage = CAN_NOT_DEACTIVATE;
                } else if (terminal.getUser() != null
                        && (
                        (terminal.getDepartment() == null && updatedTerminal.getDepartment() != null)
                                || (terminal.getDepartment() != null && updatedTerminal.getDepartment() == null)
                                || (terminal.getDepartment() != null && updatedTerminal.getDepartment() != null
                                && !terminal.getDepartment().equals(updatedTerminal.getDepartment())))) {
                    resultMessage = CAN_NOT_CHANGE;
                } else {
                    Terminal terminalToUpdate = setCurrentTimeStampForUpdateDate(updatedTerminal);
                    terminalRepository.save(terminalToUpdate);
                }
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("terminalUpdateResult", resultMessage);
        return result;
    }

    @Transactional
    @Override
    public Map<String, String> deleteTerminal(String id) {
        String resultMessage = UNABLE_DELETE;
        int terminalId = Integer.parseInt(id);
        if (!checkDependency(terminalId)) {
            Optional<Terminal> optionalTerminal = terminalRepository.findById(terminalId);
            optionalTerminal.ifPresent(terminal -> terminalRepository.delete(terminal));
            resultMessage = OK;
        }
        Map<String, String> result = new HashMap<>();
        result.put("terminalDeleteResult", resultMessage);
        return result;
    }

    private boolean checkDependency(int id) {
        boolean result = false;
        Registration registration = registrationRepository.findFirstByTerminalId(id);
        if (registration != null) {
            result = true;
        }
        return result;
    }

    private Terminal updateTerminalFields(Terminal terminal, Map<String, String> terminalParams) {
        Terminal updatedTerminal = terminal.clone();
        if (validateField(terminalParams.get("regId"))) {
            updatedTerminal.setRegId(terminalParams.get("regId"));
        }
        if (validateField(terminalParams.get("model"))) {
            updatedTerminal.setTerminalModel(terminalParams.get("model"));
        }
        if (validateField(terminalParams.get("serialId"))) {
            updatedTerminal.setSerialId(terminalParams.get("serialId"));
        }
        if (validateField(terminalParams.get("inventoryId"))) {
            updatedTerminal.setInventoryId(terminalParams.get("inventoryId"));
        }
        if (validateField(terminalParams.get("comment"))) {
            updatedTerminal.setTerminalComment(terminalParams.get("comment"));
        }

        Department department = departmentRepository.findByDepartment(terminalParams.get("department"));
        updatedTerminal.setDepartment(department);

        updatedTerminal.setTerminalIsActive(terminalParams.get("isActive").equals("true"));
        return updatedTerminal;
    }

    private boolean validateField(String value) {
        return value != null && !value.equals("");
    }

    @Override
    public long getCountOfAllTerminals() {
        return terminalRepository.count();
    }

    @Override
    public int getCountOfActiveTerminals() {
        return terminalRepository.countAllByTerminalIsActive(true);
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

    private String terminalExistsForUpdate(Terminal terminal) {
        int id = terminal.getId();
        String result = regIdExistsForUpdate(id, terminal.getRegId());
        if (result.equals(OK)) {
            result = serialIdExistsForUpdate(id, terminal.getSerialId());
        }
        if (result.equals(OK)) {
            result = inventoryIdExistsForUpdate(id, terminal.getInventoryId());
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

    private String regIdExistsForUpdate(int id, String regId) {
        String result = OK;
        Terminal terminal = terminalRepository.findByRegIdForUpdate(regId, id);
        if (terminal != null) {
            result = REG_ID_EXISTS;
        }
        return result;
    }

    private String inventoryIdExistsForUpdate(int id, String inventoryId) {
        String result = OK;
        Terminal terminal = terminalRepository.findByInventoryIdForUpdate(inventoryId, id);
        if (terminal != null) {
            result = INVENTORY_ID_EXISTS;
        }
        return result;
    }

    private String serialIdExistsForUpdate(int id, String serialId) {
        String result = OK;
        Terminal terminal = terminalRepository.findBySerialIdForUpdate(serialId, id);
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
