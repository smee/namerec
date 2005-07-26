package namerec;
/**
 * This interface describes a source of sentences.
 * @author sdienst
 *
 */
public interface SatzDatasource {
    /**
     * Returns the next sentence.. 
     * @return "END" if there's no more sentence , else a string.
     */
    public abstract String getNextSentence(); // getBsp

    public abstract int getNumOfSentences();

}