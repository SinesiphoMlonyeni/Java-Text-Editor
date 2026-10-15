public final class DocumentMetrics {
    private final int wordCount;
    private final int charCountWithSpaces;
    private final int charCountWithoutSpaces;
    private final int lineCount;
    private final int paragraphCount;

    public DocumentMetrics(int wordCount, int charCountWithSpaces,
            int charCountWithoutSpaces, int lineCount, int paragraphCount) {
        this.wordCount = wordCount;
        this.charCountWithSpaces = charCountWithSpaces;
        this.charCountWithoutSpaces = charCountWithoutSpaces;
        this.lineCount = lineCount;
        this.paragraphCount = paragraphCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharCountWithSpaces() {
        return charCountWithSpaces;
    }

    public int getCharCountWithoutSpaces() {
        return charCountWithoutSpaces;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getParagraphCount() {
        return paragraphCount;
    }
}
