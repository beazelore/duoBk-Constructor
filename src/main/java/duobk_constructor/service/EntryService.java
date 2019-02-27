package duobk_constructor.service;

import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Entry;
import duobk_constructor.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryService {
    @Autowired
    EntryRepository entryRepository;
    public Entry create(String value, String author, String title, String lang){
        Entry entry = new Entry();
        entry.setAuthor(author);
        entry.setLanguage(lang);
        entry.setTitle(title);
        entry.setValue(value);
        return entryRepository.save(entry);
    }
    public Entry getEntryById(Integer id){
        return entryRepository.findById(id).get();
    }
    public void delete(Entry entry){
        entryRepository.delete(entry);
    }
}
