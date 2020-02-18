package terminals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import terminals.models.Terminal;
import terminals.models.User;
import terminals.service.TerminalsService;
import terminals.service.UsersService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/terminals_controller")
public class TerminalsController {

    private TerminalsService terminalsService;

    @GetMapping
    @RequestMapping("/all")
    public List<Terminal> listOfAllTerminals() {
        return terminalsService.findAllTerminals();
    }

    @GetMapping("/terminal/{id}")
    public Terminal getTerminal(@PathVariable String id) {
        return terminalsService.findTerminalById(id);
    }

    @GetMapping
    @RequestMapping("/active")
    public List<Terminal> listOfActiveTerminals() {
        return terminalsService.findActiveTerminals();
    }

    @GetMapping
    @RequestMapping("/countall")
    public long getCountOfAllTerminals() {
        return terminalsService.getCountOfAllTerminals();
    }

    @GetMapping
    @RequestMapping("/countactive")
    public int getCountOfActiveTerminals() {
        return terminalsService.getCountOfActiveTerminals();
    }

    @PostMapping
    public Map<String, String> create(@RequestBody Map<String, String> newTerminal){
        return terminalsService.addTerminal(newTerminal);
    }

    @PutMapping("{id}")//departToRename передается в заголовке запроса
    public Map<String, String> update(@PathVariable String id, @RequestBody Map<String, String> terminalToUpdate){
        return terminalsService.updateTerminal(id, terminalToUpdate);
    }

    @DeleteMapping("{terminalId}") //id передается в заголовке запроса
    public Map<String, String>  delete(@PathVariable String terminalId) {
        return terminalsService.deleteTerminal(terminalId);
    }

    @Autowired
    public void setTerminalsService(TerminalsService terminalsService) {
        this.terminalsService = terminalsService;
    }
}
