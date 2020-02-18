package terminals.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import terminals.selectfilters.RegSpecifications;
import terminals.models.Registration;
import terminals.models.User;
import terminals.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RegServiceImpl implements RegService {

    private RegistrationRepository registrationRepository;

    private final static int DAYS_AMOUNT_TO_SHOW_STATISTIC_IN_MAIN_PAGE = 1;

    @Override
    public List<Registration> findAllEntriesForTheLastDay() {
        LocalDateTime now = LocalDateTime.now().minusDays(DAYS_AMOUNT_TO_SHOW_STATISTIC_IN_MAIN_PAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String yesterday = formatter.format(now);
        List<Registration> list = registrationRepository.findAllByStartDateAfter(yesterday);
        removeRecursiveDataFromRegistrations(list);
        list.forEach(this::setEmptyValuesForNullableFields);
        list.sort(new Comparator<Registration>() {
            @Override
            public int compare(Registration o1, Registration o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return list;
    }

    private void removeRecursiveDataFromRegistrations(List<Registration> registrations) {
        registrations.forEach(r -> {
            if (r.getTerminal() != null) {
                r.getTerminal().setUser(null);
            }
            if (r.getUser() != null) {
                r.getUser().setTerminal(null);
            }
            if (r.getAdminGot() != null) {
                r.getAdminGot().setTerminal(null);
            }
            if (r.getAdminGaveOut() != null) {
                r.getAdminGaveOut().setTerminal(null);
            }
        });
    }

    private void setEmptyValuesForNullableFields(Registration registration) {
        User user = new User();
        user.setFullName("");
        if (registration.getAdminGot() == null) {
            registration.setAdminGot(user);
        }
        if (registration.getEndDate() == null) {
            registration.setEndDate("");
        }
    }

    @Override
    public List<Registration> findEntriesByFilter(Map<String, String> filterParams) {
        Specification<Registration> specification = RegSpecifications.orderById();

        if (filterParams.containsKey("regIdFilter")) {
            specification = specification.and(RegSpecifications.selectByRegId(filterParams.get("regIdFilter")));
        }
        if (filterParams.containsKey("loginFilter")) {
            specification = specification.and(RegSpecifications.selectByUserLogin(filterParams.get("loginFilter")));
        }

        if (filterParams.containsKey("fullNameFilter")) {
            specification = specification.and(RegSpecifications.selectByUserFullName(filterParams.get("fullNameFilter")));
        }
        if (filterParams.containsKey("whoGaveFilter")) {
            specification = specification.and(RegSpecifications.selectByWhoGaveFullName(filterParams.get("whoGaveFilter")));
        }
        if (filterParams.containsKey("whoReceivedFilter")) {
            specification = specification.and(RegSpecifications.selectByWhoReceivedFullName(filterParams.get("whoReceivedFilter")));
        }
        if (filterParams.containsKey("startDateFilterFrom") || filterParams.containsKey("startDateFilterTo")) {
            filterParams.computeIfPresent("startDateFilterFrom", (key, val) -> val.replaceAll("-", "."));
            filterParams.computeIfPresent("startDateFilterTo", (key, val) -> val.replaceAll("-", "."));
            specification = specification.and(RegSpecifications.selectByStartDate(filterParams.get("startDateFilterFrom"), filterParams.get("startDateFilterTo")));
        }
        if (filterParams.containsKey("endDateFilterFrom") || filterParams.containsKey("endDateFilterTo")) {
            filterParams.computeIfPresent("endDateFilterFrom", (key, val) -> val.replaceAll("-", "."));
            filterParams.computeIfPresent("endDateFilterTo", (key, val) -> val.replaceAll("-", "."));
            specification = specification.and(RegSpecifications.selectByEndDate(filterParams.get("endDateFilterFrom"), filterParams.get("endDateFilterTo")));
        }

        List<Registration> filteredRegs = registrationRepository.findAll(specification);
        removeRecursiveDataFromRegistrations(filteredRegs);
        filteredRegs.forEach(this::setEmptyValuesForNullableFields);

        return filteredRegs;
    }

    private Map<String, String> convertParamsFromStringToMap(String jsonFromClient) {
        JSONObject json = new JSONObject(jsonFromClient);
        Map<String, String> resultMap = new HashMap<>();
        Iterator<String> keys = json.keys();
        if (keys.hasNext()) {
            JSONObject nestedJson = (JSONObject) json.get(keys.next());
            Iterator<String> keysOfNestedJson = nestedJson.keys();
            while (keysOfNestedJson.hasNext()) {
                String key = keysOfNestedJson.next();
                resultMap.put(key, nestedJson.getString(key));
            }
        }
        return resultMap;
    }

    @Autowired
    public void setRegistrationRepository(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }
}
