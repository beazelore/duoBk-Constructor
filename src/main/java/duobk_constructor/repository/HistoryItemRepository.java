package duobk_constructor.repository;

import duobk_constructor.model.HistoryItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryItemRepository extends CrudRepository<HistoryItem, Integer> {
    public List<HistoryItem> findByTaskId(Integer taskId);
}
