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
    public String findAllEntriesForTheLastDay() {
        LocalDateTime now = LocalDateTime.now().minusDays(DAYS_AMOUNT_TO_SHOW_STATISTIC_IN_MAIN_PAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String yesterday = formatter.format(now);
        List<Registration> list = registrationRepository.findAllByStartDateAfter(yesterday);
        list.forEach(this::setEmptyValuesForNullableFields);
        list.sort(new Comparator<Registration>() {
            @Override
            public int compare(Registration o1, Registration o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
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
    public String findEntriesByFilter(String params) {
        Map<String, String> mapOfFilters = convertParamsFromStringToMap(params);
        Specification<Registration> specification = RegSpecifications.orderById();
        List<Registration> filteredRegs = new ArrayList<>();
        if (mapOfFilters.containsKey("regIdFilter")) {
            specification = specification.and(RegSpecifications.selectByRegId(mapOfFilters.get("regIdFilter")));
        }
        if (mapOfFilters.containsKey("loginFilter")) {
            specification = specification.and(RegSpecifications.selectByUserLogin(mapOfFilters.get("loginFilter")));
        }

        if (mapOfFilters.containsKey("fullNameFilter")) {
            specification = specification.and(RegSpecifications.selectByUserFullName(mapOfFilters.get("fullNameFilter")));
        }
        if (mapOfFilters.containsKey("whoGaveFilter")) {
            specification = specification.and(RegSpecifications.selectByWhoGaveFullName(mapOfFilters.get("whoGaveFilter")));
        }
        if (mapOfFilters.containsKey("whoReceivedFilter")) {
            specification = specification.and(RegSpecifications.selectByWhoReceivedFullName(mapOfFilters.get("whoReceivedFilter")));
        }
        if (mapOfFilters.containsKey("startDateFilterFrom") || mapOfFilters.containsKey("startDateFilterTo")) {

            specification = specification.and(RegSpecifications.selectByStartDate(mapOfFilters.get("startDateFilterFrom"), mapOfFilters.get("startDateFilterTo")));
        }
        if (mapOfFilters.containsKey("endDateFilterFrom") || mapOfFilters.containsKey("endDateFilterTo")) {

            specification = specification.and(RegSpecifications.selectByEndDate(mapOfFilters.get("endDateFilterFrom"), mapOfFilters.get("endDateFilterTo")));
        }

        filteredRegs = registrationRepository.findAll(specification);
        filteredRegs.forEach(this::setEmptyValuesForNullableFields);

        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(filteredRegs);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
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
