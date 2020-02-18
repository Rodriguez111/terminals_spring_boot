package terminals.service;

import terminals.models.Registration;

import java.util.List;
import java.util.Map;

public interface RegService {

    List<Registration> findAllEntriesForTheLastDay();

    List<Registration> findEntriesByFilter(Map<String, String> filterParams);

}
