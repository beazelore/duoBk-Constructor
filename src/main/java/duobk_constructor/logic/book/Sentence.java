package duobk_constructor.logic.book;

public class Sentence {
    private String value;
    private Paragraph paragraph;
    private int index;

    public int getIndexInDuo() {
        return indexInDuo;
    }

    public void setIndexInDuo(int indexInDuo) {
        this.indexInDuo = indexInDuo;
    }

    private int indexInDuo;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Sentence(String value, Paragraph paragraph) {
        this.value = value;
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sentence))
            return false;
        Sentence other = (Sentence)obj;
        if(!value.equals(other.value))
            return false;
        return true;
    }
}
