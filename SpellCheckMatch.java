public final class SpellCheckMatch {
    private final String word;
    private final int startOffset;

    public SpellCheckMatch(String word, int startOffset) {
        this.word = word;
        this.startOffset = startOffset;
    }

    public String getWord() {
        return word;
    }

    public int getStartOffset() {
        return startOffset;
    }
}
