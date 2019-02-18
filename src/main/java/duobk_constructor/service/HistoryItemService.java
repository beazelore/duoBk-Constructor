package duobk_constructor.service;

import duobk_constructor.model.HistoryItem;
import duobk_constructor.repository.HistoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryItemService {
    @Autowired
    HistoryItemRepository repository;
    public void delete(HistoryItem item){
        repository.delete(item);
    }
    public List<HistoryItem> getTaskHistory(Integer taskId){
        return repository.findByTaskId(taskId);
    }
    public HistoryItem save(HistoryItem item){
        return repository.save(item);
    }
}
