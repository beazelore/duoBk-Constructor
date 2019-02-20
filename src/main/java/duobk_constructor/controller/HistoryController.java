package duobk_constructor.controller;

import duobk_constructor.model.HistoryItem;
import duobk_constructor.service.HistoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/history")
public class HistoryController {
    @Autowired
    HistoryItemService historyService;

    @GetMapping(value = "/task")
    public List<HistoryItem> getTaskHistory(@RequestParam(value = "id", required = true) String taskId){
        return historyService.getTaskHistory(Integer.parseInt(taskId));
    }
}
