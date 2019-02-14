package duobk_constructor.logic;


import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.duo.DuoParagraph;

import java.util.ArrayList;
import java.util.HashMap;

public class AStar {
    private Book book1;
    private Book book2;
    ArrayList<DuoParagraph> result;
    ArrayList<DuoParagraph> closed;
    ArrayList<DuoParagraph> open;
    HashMap<DuoParagraph, DuoParagraph> from;
    HashMap<DuoParagraph, Integer> mG;

    public AStar(Book book1, Book book2){
        this.book1 = book1;
        this.book2 = book2;
        closed = new ArrayList<>();
        open = new ArrayList<>();
        from = new HashMap<>();
        mG = new HashMap<>();
    }
    public ArrayList<DuoParagraph> getResult() {
        return result;
    }


    public void Start(ArrayList<Integer> startIndexes1, ArrayList<Integer> startIndexes2,
                      ArrayList<Integer> endIndexes1, ArrayList<Integer> endIndexes2)
    {
        boolean success = false;
        DuoParagraph startParagraphs = createDuoParagraph(startIndexes1,startIndexes2,book1,book2);
        DuoParagraph endParagraphs = createDuoParagraph(endIndexes1,endIndexes2,book1,book2);
        open.add(startParagraphs);
        mG.put(startParagraphs, 0);
        while (open.size() > 0)
        {
            DuoParagraph currentNode = minOpenG(mG, open);
            if (currentNode.equals(endParagraphs))
            {
                success = true;
                // find endNode in mFrom and replace it with the actual original instance of endNode
                for (DuoParagraph key : from.keySet()){
                    if (key.equals(endParagraphs))
                        endParagraphs = key;
                }
                break;
            }
            open.remove(currentNode);
            closed.add(currentNode);
            ArrayList<DuoParagraph> childrenOfCurrent = createChildNodes(currentNode);
            ArrayList<DuoParagraph> unclosedChildren = new ArrayList<>();
            for (DuoParagraph child : childrenOfCurrent){
                if (!closed.contains(child))
                    unclosedChildren.add(child);
            }
            for (DuoParagraph node : unclosedChildren){
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
            DuoParagraph temp = from.get(endParagraphs);
            result.add(temp);
            result.add(endParagraphs);
            while (!temp.equals(startParagraphs))
            {
                temp = from.get(temp);
                result.add(0,temp);
            }
        }

    }

    private DuoParagraph minOpenG(HashMap<DuoParagraph,Integer> g, ArrayList<DuoParagraph> open)
    {
        DuoParagraph minKey = open.get(0);
        Integer minValue = g.get(minKey);
        for(DuoParagraph p : open){
            if(g.get(p) < minValue){
                minKey = p;
                minValue = g.get(p);
            }
        }
        return minKey;
    }
    private DuoParagraph createDuoParagraph(ArrayList<Integer> indexes1, ArrayList<Integer> indexes2, Book book1, Book book2){
        DuoParagraph result = new DuoParagraph();
        ArrayList<Paragraph> paragraphs1 = new ArrayList<>();
        ArrayList<Paragraph> paragraphs2 = new ArrayList<>();
        for(Integer index : indexes1){
            for(Paragraph p : book1.getParagraphs())
                if(index.equals(p.getIndex()))
                    paragraphs1.add(p);
        }
        for(Integer index : indexes2){
            for(Paragraph p : book2.getParagraphs())
                if(index.equals(p.getIndex()))
                    paragraphs2.add(p);
        }
        return new DuoParagraph(paragraphs1,paragraphs2);
    }
    private ArrayList<DuoParagraph> createChildNodes(DuoParagraph paragraph){
        ArrayList<DuoParagraph> result = new ArrayList<>();
        // create (+1,+1) child
        int index1 = book1.getParagraphs().indexOf(paragraph.getParagraphs1().get(paragraph.getParagraphs1().size()-1))+1;
        int index2 = book2.getParagraphs().indexOf(paragraph.getParagraphs2().get(paragraph.getParagraphs2().size()-1))+1;
        //int index1 = paragraph.getParagraphs1().get( paragraph.getParagraphs1().size() -1).getIndex() +1;
        //int index2 = paragraph.getParagraphs2().get( paragraph.getParagraphs2().size() -1).getIndex() +1;
        if (index1 < book1.getParagraphs().size() && index2 <book2.getParagraphs().size())
        {
            Paragraph paragraph1 = book1.getParagraphs().get(index1);
            Paragraph paragraph2 = book2.getParagraphs().get(index2);
            DuoParagraph pair = new DuoParagraph(paragraph1, paragraph2);
            result.add(pair);
        }
        // create (+2,+1) child
        int index3 = index1+1;
        //int index3 = book1.getParagraphs().indexOf(paragraph.getParagraphs1().get(paragraph.getParagraphs1().size()-1))+2;
        //int index3 = paragraph.getParagraphs1().get( paragraph.getParagraphs1().size() -1).getIndex() +2;
        if (index3 < book1.getParagraphs().size() && index2 < book2.getParagraphs().size())
        {
            ArrayList<Paragraph> paragraph1 = new ArrayList<>();
            paragraph1.add(book1.getParagraphs().get(index1));
            paragraph1.add(book1.getParagraphs().get(index3));
            Paragraph paragraph2 = book2.getParagraphs().get(index2);
            DuoParagraph pair = new DuoParagraph(paragraph1, paragraph2);
            result.add(pair);
        }
        // create (+1,+2) child
        index3= index2+1;
        //index3 = book2.getParagraphs().indexOf(paragraph.getParagraphs2().get(paragraph.getParagraphs2().size()-1))+2;
        //index3 = paragraph.getParagraphs2().get( paragraph.getParagraphs2().size() -1).getIndex() +2;
        if (index1 < book1.getParagraphs().size() && index3 < book2.getParagraphs().size())
        {
            ArrayList<Paragraph> paragraph2 = new ArrayList<>();
            paragraph2.add(book2.getParagraphs().get(index2));
            paragraph2.add(book2.getParagraphs().get(index3));
            Paragraph paragraph1 = book1.getParagraphs().get(index1);
            DuoParagraph pair = new DuoParagraph(paragraph1, paragraph2);
            result.add(pair);
        }
        // create (+3,+1)
        //index3 = paragraph.getParagraphs1().get( paragraph.getParagraphs1().size() -1).getIndex() +2;
        //int index4 = paragraph.getParagraphs1().get( paragraph.getParagraphs1().size() -1).getIndex() +3;
        //index3 = book1.getParagraphs().indexOf(paragraph.getParagraphs1().get(paragraph.getParagraphs1().size()-1))+2;
        index3 = index1+1;
        int index4 = index3+1;
        if (index4 < book1.getParagraphs().size() && index2 < book2.getParagraphs().size())
        {
            ArrayList<Paragraph> paragraph1 = new ArrayList<>();
            paragraph1.add(book1.getParagraphs().get(index1));
            paragraph1.add(book1.getParagraphs().get(index3));
            paragraph1.add(book1.getParagraphs().get(index4));
            Paragraph paragraph2 = book2.getParagraphs().get(index2);
            DuoParagraph pair = new DuoParagraph(paragraph1, paragraph2);
            result.add(pair);
        }
        // create (+1,+3)
        //index3 = paragraph.getParagraphs2().get( paragraph.getParagraphs2().size() -1).getIndex() +2;
        //index4 = paragraph.getParagraphs2().get( paragraph.getParagraphs2().size() -1).getIndex() +3;
        index3 = index2+1;
        index4 = index3+1;
        if (index1 < book1.getParagraphs().size() && index4 < book2.getParagraphs().size())
        {
            ArrayList<Paragraph> paragraph2 = new ArrayList<>();
            paragraph2.add(book2.getParagraphs().get(index2));
            paragraph2.add(book2.getParagraphs().get(index3));
            paragraph2.add(book2.getParagraphs().get(index4));
            Paragraph paragraph1 = book1.getParagraphs().get(index1);
            DuoParagraph pair = new DuoParagraph(paragraph1, paragraph2);
            result.add(pair);
        }
        return  result;
    }
}
