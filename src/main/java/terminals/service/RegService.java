package terminals.service;

public interface RegService {

    String findAllEntriesForTheLastDay();

    String findEntriesByFilter(String jsonFilterParams);

}
