package terminals.service;

import terminals.models.Registration;

import java.util.List;

public interface RegSortService {

    List<Registration> sortEntries(String jsonSortParams);
}
