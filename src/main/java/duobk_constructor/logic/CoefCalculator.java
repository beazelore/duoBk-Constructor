package duobk_constructor.logic;



import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.Mark;
import duobk_constructor.logic.book.duo.ParagraphData;

import java.util.ArrayList;

public class CoefCalculator {

    private final int LongParagraphWordsCount = 20;
    ArrayList<Mark> mMarkSequence1;
    ArrayList<Mark> mMarkSequence2;
    int mMarksCountDiff;
    int mWPSDiff;
    int mWordsDiff;
    int mWordsCount1;
    int mWordsCount2;
    int mNoise1;
    int mNoise2;
    int mQuestion1;
    int mQuestion2;
    int mDotAndComas1;
    int mDotAndComas2;
    int mDots1;
    int mDots2;
    //List<string> mLog;
    public CoefCalculator()
    {
    }
    public double calculate(DuoParagraph duoParagraph)
    {
        ParagraphData data1 = null;
        ParagraphData data2 = null;
        for(int i =0; i < duoParagraph.getParagraphs1().size(); i++){
            if(i==0){
                data1=duoParagraph.getParagraphs1().get(i).getData();
            }
            else{
                data1 = data1.Merge(duoParagraph.getParagraphs1().get(i).getData());
            }
        }
        for(int i =0; i < duoParagraph.getParagraphs2().size(); i++){
            if(i==0){
                data2=duoParagraph.getParagraphs2().get(i).getData();
            }
            else{
                data2 = data2.Merge(duoParagraph.getParagraphs2().get(i).getData());
            }
        }
        mMarkSequence1 = data1.getmMarkSequence();
        mMarkSequence2 = data2.getmMarkSequence();
        mMarksCountDiff = Math.abs(mMarkSequence1.size() - mMarkSequence2.size());
        mWPSDiff = Math.abs(data1.getmWPS ()- data2.getmWPS());
        mWordsDiff = Math.abs(data1.getmWordsCount ()- data2.getmWordsCount());
        mWordsCount1 = data1.getmWordsCount();
        mWordsCount2 = data2.getmWordsCount();
        mDots1 = data1.getmDotsCount();
        mDots2 = data2.getmDotsCount();
        mNoise1 = data1.getmNoiseMarksCount();
        mNoise2 = data2.getmNoiseMarksCount();
        mDotAndComas1 = data1.getmDotAndComasCount();
        mDotAndComas2 = data2.getmDotAndComasCount();
        mQuestion1 = data1.getmQuestionMarksCount();
        mQuestion2 = data2.getmQuestionMarksCount();
        double result = 100;
        double averageWordsCount = (mWordsCount1 + mWordsCount2) / 2;
        //handling words count
        {
            double acceptableWordsDiff = 0;
            if (averageWordsCount < 51)
                acceptableWordsDiff = averageWordsCount * 0.2;
            int wordsDiff = Math.abs(mWordsCount2 - mWordsCount1);
            if(wordsDiff > acceptableWordsDiff)
                result -= (wordsDiff - acceptableWordsDiff) * 1.4;
        }
        //handling dots count
        {
            int acceptableDotsDiff = 0;
            if (averageWordsCount > 40 && averageWordsCount < 80)
                acceptableDotsDiff = 1;
            else if (averageWordsCount >= 80 && averageWordsCount < 140)
                acceptableDotsDiff = 2;
            else if (averageWordsCount >= 140)
                acceptableDotsDiff = 3;
            int dotsDiff = Math.abs(data1.getmSentenceCount() - data2.getmSentenceCount());
            if(dotsDiff == 0 )
                result+=10;
            if (dotsDiff > acceptableDotsDiff)
                result -= (dotsDiff - acceptableDotsDiff) * 20;
        }
        //handling sequence
        {
            if (ContainsSpecificMarks(mMarkSequence1) && ContainsSpecificMarks(mMarkSequence2))
            {
                if (AreSequencesEqual(mMarkSequence1, mMarkSequence2))
                    result += 15;
                if (AreCommonSpecificMarks(mMarkSequence1, mMarkSequence2))
                    result += 5;
            }
        }
        return result;
    }
    private boolean AreCommonSpecificMarks(ArrayList<Mark> sequence1, ArrayList<Mark> sequence2)
    {
        if (sequence1.contains(Mark.Question) && sequence2.contains(Mark.Question))
            return true;
        if (sequence1.contains(Mark.Noise) && sequence2.contains(Mark.Noise))
            return true;
        return false;
    }
    private boolean ContainsSpecificMarks(ArrayList<Mark> sequence)
    {
        if (sequence.contains(Mark.Question) || sequence.contains(Mark.Noise))
            return true;
        return false;
    }
    private boolean AreSequencesEqual(ArrayList<Mark> sequence1, ArrayList<Mark> sequence2)
    {
        if (sequence1.size() != sequence2.size())
            return false;
        for(int i = 0; i < sequence2.size(); i++)
        {
            if (sequence1.get(i) != sequence2.get(i))
                return false;
        }
        return true;
    }
}
