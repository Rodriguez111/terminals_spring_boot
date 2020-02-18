package terminals.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import terminals.models.Registration;

import java.util.*;


@Service
public class RegSortImpl implements RegSortService {

   private final Map<String, String> mapOfSortParams = new HashMap<>();
   private List<Registration> listOfRegs;

    private final Map<String, Runnable> sortOptionsAscending  = new HashMap<>();
    private final Map<String, Runnable> sortOptionsDescending  = new HashMap<>();

    private RegSortImpl() {
        initSortOptions();
    }

    private void initSortOptionsAscending() {
        sortOptionsAscending.put("sortByRegId", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> adaptRegIdOrLoginForCompare(r.getTerminal().getRegId()))));
        sortOptionsAscending.put("sortByLogin", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> adaptRegIdOrLoginForCompare(r.getUser().getUserLogin()))));
        sortOptionsAscending.put("sortByFullName", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getUser().getFullName())));
        sortOptionsAscending.put("sortByWhoGave", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getAdminGaveOut().getFullName())));
        sortOptionsAscending.put("sortByWhoReceived", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getAdminGot().getFullName())));
        sortOptionsAscending.put("sortByStartDate", () -> listOfRegs.sort(Comparator.comparing(Registration::getStartDate)));
        sortOptionsAscending.put("sortByEndDate", () -> listOfRegs.sort(Comparator.comparing(Registration::getEndDate)));
    }

    private void initSortOptionsDescending() {
        sortOptionsDescending.put("sortByRegId", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> adaptRegIdOrLoginForCompare(r.getTerminal().getRegId())).reversed()));
        sortOptionsDescending.put("sortByLogin", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> adaptRegIdOrLoginForCompare(r.getUser().getUserLogin())).reversed()));
        sortOptionsDescending.put("sortByFullName", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getUser().getFullName()).reversed()));
        sortOptionsDescending.put("sortByWhoGave", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getAdminGaveOut().getFullName()).reversed()));
        sortOptionsDescending.put("sortByWhoReceived", () -> listOfRegs.sort(Comparator.comparing((Registration r) -> r.getAdminGot().getFullName()).reversed()));
        sortOptionsDescending.put("sortByStartDate", () -> listOfRegs.sort(Comparator.comparing(Registration::getStartDate).reversed()));
        sortOptionsDescending.put("sortByEndDate", () -> listOfRegs.sort(Comparator.comparing(Registration::getEndDate).reversed()));
    }

    private void initSortOptions() {
        initSortOptionsAscending();
        initSortOptionsDescending();
    }



    private void parseJson(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if (key.equals("sortEntries") || key.equals("listOfEntries")) {
                parseJson((JSONObject)jsonObject.get(key));
            }
            if (key.equals("whatToSort") || key.equals("upOrDown")) {
                mapOfSortParams.put(key, jsonObject.getString(key));
            }
            if (key.startsWith("entry")) {
                JSONObject entry = (JSONObject)jsonObject.get(key);
                Gson gson = new Gson();
                Registration registration = gson.fromJson(entry.toString(), Registration.class);
                listOfRegs.add(registration);
            }
        }
    }

    @Override
    public List<Registration>  sortEntries(String jsonSortParams) {
        listOfRegs = new ArrayList<>();
        JSONObject jsonFromClient = new JSONObject(jsonSortParams);
        parseJson(jsonFromClient);

        String whatToSort = mapOfSortParams.get("whatToSort");
        String upOrDown = mapOfSortParams.get("upOrDown");

        if (upOrDown.equals("up")) {
            sortOptionsAscending.get(whatToSort).run();
        } else {
           sortOptionsDescending.get(whatToSort).run();
        }
        return listOfRegs;
    }


    private Integer adaptRegIdOrLoginForCompare(String regIdOrLogin) {
        int digitalPart = 0;
        try{
            digitalPart = Integer.parseInt(regIdOrLogin.substring(2));
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат учетного номера терминала или логина пользователя");
            e.printStackTrace();
        }
        return digitalPart;
    }

}
