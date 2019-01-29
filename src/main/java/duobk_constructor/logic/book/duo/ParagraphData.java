package duobk_constructor.logic.book.duo;

import duobk_constructor.logic.book.Paragraph;

import java.util.ArrayList;

public class ParagraphData {
    ArrayList<Mark> mMarkSequence;

    public int getmSentenceCount() {
        return mSentenceCount;
    }

    int mSentenceCount;
    int mDotsCount;
    int mQuestionMarksCount;
    int mNoiseMarksCount;
    int mDotAndComasCount;
    int mWordsCount;
    int mWPS; // WordsPerSentence

    public ArrayList<Mark> getmMarkSequence() {
        return mMarkSequence;
    }

    public int getmDotsCount() {
        return mDotsCount;
    }

    public int getmQuestionMarksCount() {
        return mQuestionMarksCount;
    }

    public int getmNoiseMarksCount() {
        return mNoiseMarksCount;
    }

    public int getmDotAndComasCount() {
        return mDotAndComasCount;
    }

    public int getmWordsCount() {
        return mWordsCount;
    }

    public int getmWPS() {
        return mWPS;
    }

    public ParagraphData(Paragraph paragraph)
    {
        String text = paragraph.toString();
        int index = 0;
        int DotsCount = 0;
        int WordCount = 0;
        mSentenceCount = paragraph.getSentences().size();
        mDotAndComasCount = 0;
        mQuestionMarksCount = 0;
        mNoiseMarksCount = 0;
        mMarkSequence = new ArrayList<Mark>();
        while (index < text.length())
        {
            boolean wasLettersOrDigits = false;
            // skip whitespace until next word
            while (index < text.length() && Character.isWhitespace(text.charAt(index)))
            {
                index++;
            }
            // check if current char is part of a word and skip it
            while (index < text.length() && !Character.isWhitespace(text.charAt(index)))
            {
                if(Character.isLetterOrDigit(text.charAt(index)))
                    wasLettersOrDigits = true;
                if (text.charAt(index) == '.')
                {
                    mMarkSequence.add(Mark.Dot);
                    DotsCount++;
                }
                else if (text.charAt(index) == '!')
                {
                    mMarkSequence.add(Mark.Noise);
                    mNoiseMarksCount++;
                }
                else if (text.charAt(index) == '?')
                {
                    mMarkSequence.add(Mark.Question);
                    mQuestionMarksCount++;
                }
                else if (text.charAt(index) == ';')
                {
                    mMarkSequence.add(Mark.DotAndComa);
                    mDotAndComasCount++;
                }
                index++;
            }
            if(wasLettersOrDigits)
                WordCount++;
        }
        mWordsCount = WordCount;
        mDotsCount = DotsCount;
        if (mMarkSequence.size() == 0)
            mWPS = mWordsCount;
        else
            mWPS = mWordsCount / paragraph.getSentences().size();
    }
    public ParagraphData(ArrayList<Mark> sequence, int dots, int question, int noise, int dotsAndComas, int words, int wps)
    {
        mMarkSequence = sequence;
        mDotsCount = dots;
        mQuestionMarksCount = question;
        mNoiseMarksCount = noise;
        mDotAndComasCount = dotsAndComas;
        mWordsCount = words;
        mWPS = wps;
    }
    public ParagraphData Merge(ParagraphData otherData)
    {
        ArrayList<Mark> sequence = new ArrayList<Mark>();
        sequence.addAll(mMarkSequence);
        sequence.addAll(otherData.getmMarkSequence());
        int newWPS = 0;
        if (sequence.size() == 0)
            newWPS = mWordsCount;
        else
            newWPS = (mWordsCount + otherData.getmWordsCount()) / (this.mSentenceCount + otherData.getmSentenceCount());
        ParagraphData newData = new ParagraphData(sequence,mDotsCount + otherData.getmDotsCount(), mQuestionMarksCount + otherData.getmQuestionMarksCount(),
                mNoiseMarksCount + otherData.getmNoiseMarksCount(),mDotAndComasCount+otherData.getmDotAndComasCount(), mWordsCount + otherData.getmWordsCount(), newWPS);
        return newData;
    }
}
