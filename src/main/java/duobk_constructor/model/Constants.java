package duobk_constructor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "constants")
public class Constants {
    @Column(name = "translation_script")
    private String translationScript;

    @Id
    @Column(name = "id")
    private String id;

    public Constants() {
    }

    public String getTranslationScript() {
        return translationScript;
    }

    public void setTranslationScript(String translationScript) {
        this.translationScript = translationScript;
    }
}
