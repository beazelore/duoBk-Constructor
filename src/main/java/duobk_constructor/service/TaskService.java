package duobk_constructor.service;

import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.model.Task;
import duobk_constructor.repository.TaskRepository;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<Task> getUserTasks(Integer userId){
        return taskRepository.findByUserId(userId);
    }
    public Iterable<Task> getAll(){
        return taskRepository.findAll();
    }
    public List<Task> getAllFree(){
        List<Task> result = taskRepository.findByUserId(null);
        return result;
    }
    public Task create(String name, Integer entryId1, Integer entryId2, Integer bookId, String status){
        Task task = new Task();
        task.setName(name);
        task.setEntry1_id(entryId1);
        task.setEntry2_id(entryId2);
        task.setBookId(bookId);
        task.setStatus(status);
        return taskRepository.save(task);
    }
    public Task save(Task task){
        return taskRepository.save(task);
    }
    public Task getTaskById(Integer id){
        return taskRepository.findById(id).get();
    }
    public Task setUserId(Integer taskId, Integer userId){
        Task task = getTaskById(taskId);
        task.setUserId(userId);
        return taskRepository.save(task);
    }
    public ResponseEntity<?> formBooksResponse(Book book1, Book book2){
        StringBuilder builder = new StringBuilder();
        for (Chapter chapter : book1.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        builder.append("!separator!");
        for (Chapter chapter : book2.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        return new ResponseEntity<String>(builder.toString(), HttpStatus.OK);
    }
    /*
    * after Dijkstra algorithm, we convert it's result to string that will be saved in
    * "unprocessed" column of Task table in db.
    * */
    public String formUnprocessedAfterPreProcess(ArrayList<DuoParagraph> duoParagraphs){
        StringBuilder stringBuilder = new StringBuilder();
        for(DuoParagraph paragraph : duoParagraphs){
            stringBuilder.append("<dp indexes1=\"").append(getParagraphsIndexesCSV(paragraph.getParagraphs1())).append("\" ")
                    .append("indexes2=\"").append(getParagraphsIndexesCSV(paragraph.getParagraphs2())).append("\" ")
                    .append("chapter=\"").append(paragraph.getParagraphs1().get(0).getChapter().getIndex()).append("\">")
                    .append(getParagraphsUnprocessedString(paragraph.getParagraphs1(),true))
                    .append(getParagraphsUnprocessedString(paragraph.getParagraphs2(),false))
                    .append("</dp>");
        }
        return stringBuilder.toString();
    }
    private String getParagraphsIndexesCSV(ArrayList<Paragraph> paragraphs){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0; i < paragraphs.size(); i ++){
            if(i == 0)
                stringBuilder.append(paragraphs.get(i).getIndex());
            else
                stringBuilder.append(',').append(paragraphs.get(i).getIndex());
        }
        return stringBuilder.toString();
    }
    /*
     * <p1 id="">....</><p1 id="">....</> <p2 id="">....</p2>
     *  that's how paraphs are represent in "unprocessed" column of Task table in database
     */
    private String getParagraphsUnprocessedString(ArrayList<Paragraph> paragraphs, boolean fromBook1){
        StringBuilder stringBuilder = new StringBuilder();
        for(Paragraph paragraph : paragraphs){
            if(fromBook1)
                stringBuilder.append("<p1 ");
            else stringBuilder.append("<p2 ");
            stringBuilder.append("index=\"").append(paragraph.getIndex()).append("\">")
                    .append(paragraph.toString());
            if(fromBook1)
                stringBuilder.append("</p1>");
            else  stringBuilder.append("</p2>");
        }
        return stringBuilder.toString();
    }
}
