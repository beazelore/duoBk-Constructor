package duobk_constructor.logic;


import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.DuoSentence;

import java.util.ArrayList;
import java.util.HashMap;

public class SentenceAStar {
    private ArrayList<Sentence> sentences1;
    private ArrayList<Sentence> sentences2;
    private ArrayList<DuoSentence> result;
    private ArrayList<DuoSentence> closed;
    private ArrayList<DuoSentence> open;
    private HashMap<DuoSentence, DuoSentence> from;
    private HashMap<DuoSentence, Integer> mG;
    private DuoParagraph duoParagraph;
    public SentenceAStar(){
        result = new ArrayList<>();
        sentences1 = new ArrayList<>();
        sentences2 = new ArrayList<>();
        closed = new ArrayList<>();
        open = new ArrayList<>();
        from = new HashMap<>();
        mG = new HashMap<>();
    }

    public ArrayList<DuoSentence> getResult() {
        return result;
    }

    public void doAStar(DuoParagraph duoParagraph){
        this.duoParagraph = duoParagraph;
        for(Paragraph p : duoParagraph.getParagraphs1()){
            for(Sentence s : p.getSentences()){
                sentences1.add(s);
            }
        }
        for(Paragraph p : duoParagraph.getParagraphs2()){
            for(Sentence s : p.getSentences()){
                sentences2.add(s);
            }
        }
        boolean success = false;
        DuoSentence startSentence = new DuoSentence(duoParagraph.getParagraphs1().get(0).getSentences().get(0),duoParagraph.getParagraphs2().get(0).getSentences().get(0));
        Paragraph lastParagraph1 = duoParagraph.getParagraphs1().get(duoParagraph.getParagraphs1().size()-1);
        Paragraph lastParagraph2 = duoParagraph.getParagraphs2().get(duoParagraph.getParagraphs2().size()-1);
        Sentence lastSentence1 = lastParagraph1.getSentences().get(lastParagraph1.getSentences().size()-1);
        Sentence lastSentence2 = lastParagraph2.getSentences().get(lastParagraph2.getSentences().size()-1);
        DuoSentence endSentence = new DuoSentence(lastSentence1,lastSentence2);
        open.add(startSentence);
        mG.put(startSentence, 0);
        while (open.size() > 0)
        {
            DuoSentence currentNode = minOpenG(mG, open);
            if (currentNode.equals(endSentence))
            {
                success = true;
                // find endNode in mFrom and replace it with the actual original instance of endNode
                for (DuoSentence key : from.keySet()){
                    if (key.equals(endSentence))
                        endSentence = key;
                }
                break;
            }
            open.remove(currentNode);
            closed.add(currentNode);
            ArrayList<DuoSentence> childrenOfCurrent = createChildNodes(currentNode);
            ArrayList<DuoSentence> unclosedChildren = new ArrayList<>();
            for (DuoSentence child : childrenOfCurrent){
                if (!closed.contains(child))
                    unclosedChildren.add(child);
            }
            for (DuoSentence node : unclosedChildren){
                int tempG = mG.get(currentNode) - node.getWeight();
                if(!open.contains(node) || !mG.containsKey(node) || tempG< mG.get(node)){
                    if (!from.containsKey(node))
                        from.put(node, currentNode);
                    else from.replace(node, currentNode);
                    if (!mG.containsKey(node))
                        mG.put(node, tempG);
                    else
                        mG.replace(node, tempG);
                }
                if (!open.contains(node))
                    open.add(node);
            }
        }
        if (success)
        {
            result = new ArrayList<>();
            DuoSentence temp = from.get(endSentence);
            result.add(temp);
            result.add(endSentence);
            while (!temp.equals(startSentence))
            {
                temp = from.get(temp);
                result.add(0,temp);
            }
        }
    }
    private DuoSentence minOpenG(HashMap<DuoSentence,Integer> g, ArrayList<DuoSentence> open)
    {
        DuoSentence minKey = open.get(0);
        Integer minValue = g.get(minKey);
        for(DuoSentence p : open){
            if(g.get(p) < minValue){
                minKey = p;
                minValue = g.get(p);
            }
        }
        return minKey;
    }
    private ArrayList<DuoSentence> createChildNodes(DuoSentence duoSentence){
        int book1;
        int book2;
        ArrayList<DuoSentence> result = new ArrayList<>();
        // create (+1,+1) child
        int index1 = duoSentence.getSentences1().get(duoSentence.getSentences1().size()-1).getIndexInDuo() +1;
        int index2 = duoSentence.getSentences2().get(duoSentence.getSentences2().size()-1).getIndexInDuo() +1;
        if (index1 < sentences1.size() && index2 < sentences2.size())
        {
            Sentence sentence1 = sentences1.get(index1);
            Sentence sentence2 = sentences2.get(index2);
            DuoSentence pair = new DuoSentence(sentence1,sentence2);
            result.add(pair);
        }
        // create (+2,+1) child
        int index3 = duoSentence.getSentences1().get(duoSentence.getSentences1().size()-1).getIndexInDuo() +2;
        if (index3 < sentences1.size() && index2 < sentences2.size())
        {
            ArrayList<Sentence> sent1 = new ArrayList<>();
            sent1.add(sentences1.get(index1));
            sent1.add(sentences1.get(index3));
            Sentence sentence2 = sentences2.get(index2);
            DuoSentence pair = new DuoSentence(sent1,sentence2);
            result.add(pair);
        }
        // create (+1,+2) child
        index3 = duoSentence.getSentences2().get(duoSentence.getSentences2().size()-1).getIndexInDuo() +2;
        if (index1 < sentences1.size() && index3 < sentences2.size())
        {
            ArrayList<Sentence> sent2 = new ArrayList<>();
            sent2.add(sentences2.get(index2));
            sent2.add(sentences2.get(index3));
            Sentence sentence1 = sentences1.get(index1);
            DuoSentence pair = new DuoSentence(sentence1,sent2);
            result.add(pair);
        }
        return  result;
    }
}
