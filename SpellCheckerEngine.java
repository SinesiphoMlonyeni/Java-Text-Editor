import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellCheckerEngine {
    private final Set<String> dictionary;

    public SpellCheckerEngine() {
        this.dictionary = new HashSet<>();
        loadCoreDictionary();
    }

    private void loadCoreDictionary() {
        String[] coreWords = {
            "a", "ability", "about", "above", "abstract", "academic", "access", "account",
            "accurate", "action", "activity", "add", "adding", "address", "admin", "advanced",
            "after", "again", "all", "allow", "allows", "also", "always", "am", "among", "amps",
            "an", "analysis", "and", "anyone", "api", "app", "application", "applications",
            "apply", "approach", "appropriate", "architecture", "area", "areas", "argument",
            "arguments", "arithmetic", "array", "as", "ask", "assigned", "assist", "assume",
            "assumptions", "at", "atm", "author", "automated", "automatic", "automatically",
            "available", "average", "back", "balance", "based", "basic", "be", "because",
            "been", "before", "behind", "being", "below", "between", "boolean", "both",
            "branch", "break", "brief", "buffer", "buffered", "build", "built", "business",
            "button", "buttons", "by", "byte", "calculate", "calculator", "call", "can",
            "cancel", "cape", "case", "catch", "categories", "category", "center", "change",
            "changes", "char", "character", "characters", "chat", "check", "checker",
            "checking", "choice", "choose", "chosen", "citation", "class", "classes", "clear",
            "click", "client", "close", "cloud", "code", "Coding", "collection", "color",
            "column", "columns", "command", "comment", "comments", "commit", "commits",
            "common", "communication", "company", "compare", "compiler", "complete",
            "complex", "component", "components", "computation", "computations", "compute",
            "computer", "concept", "concepts", "conclusion", "condition", "confirm",
            "connect", "consider", "console", "constant", "constants", "constructor",
            "constructors", "contact", "contain", "container", "contains", "content",
            "context", "continue", "control", "controls", "convert", "converter", "copy",
            "core", "correct", "correctness", "count", "counter", "course", "courses",
            "cput", "create", "created", "creating", "creation", "crucial", "currency",
            "current", "currentamps", "cursor", "custom", "customer", "cut", "daily", "data",
            "database", "date", "dates", "debug", "debugging", "decimal", "decision",
            "declare", "default", "define", "defined", "delete", "deliverable", "deliverables",
            "demo", "demonstrate", "demonstrates", "demonstration", "department", "deposit",
            "describe", "description", "descriptions", "design", "designed", "designing",
            "desktop", "detail", "details", "detect", "detection", "develop", "developed",
            "developer", "developing", "development", "device", "devices", "diagram",
            "diagrams", "dialog", "dictionary", "different", "difficulty", "digital",
            "dimension", "directory", "discard", "discuss", "discussing", "discussion",
            "discussions", "disk", "display", "displayed", "do", "document", "documentation",
            "documents", "does", "dollar", "domain", "done", "double", "down", "download",
            "draw", "drawing", "due", "during", "dynamic", "each", "easily", "easy", "edit",
            "edited", "editing", "editor", "editors", "educators", "effective", "effectively",
            "efficiency", "efficient", "ekwe", "electrical", "electronic", "element",
            "elements", "else", "email", "EMPTY", "enable", "enabled", "encapsulation",
            "encoding", "encryption", "end", "energy", "engine", "engineer", "engineering",
            "engineers", "enhance", "enrollment", "ensure", "enter", "entering", "entire",
            "environment", "equal", "equals", "error", "errors", "essential", "established",
            "evaluate", "evaluation", "event", "events", "every", "example", "examples",
            "examiner", "exception", "exceptions", "exchange", "execute", "execution",
            "exist", "existing", "exists", "exit", "experience", "explain", "explorer",
            "export", "extends", "extension", "external", "faculty", "false", "family",
            "fast", "feature", "features", "feedback", "fetch", "field", "fields", "figure",
            "figures", "file", "files", "filter", "filters", "final", "finally", "financial",
            "find", "first", "flight", "float", "flow", "folder", "follow", "following",
            "font", "fonts", "for", "form", "formal", "format", "formats", "formatting",
            "forms", "found", "frame", "frames", "free", "frequent", "frequently", "from",
            "full", "function", "functional", "functionalities", "functionality", "functions",
            "fundamental", "further", "future", "gain", "game", "general", "generate",
            "generated", "get", "git", "github", "given", "global", "go", "going", "good",
            "grade", "grades", "grammar", "grammatical", "graphical", "graphics", "grid",
            "group", "gui", "guide", "gutter", "handle", "handler", "handling", "hands",
            "harvard", "has", "have", "header", "height", "help", "helper", "hence", "high",
            "highlight", "highlighter", "highlighting", "history", "home", "host", "how",
            "however", "html", "ide", "idea", "identify", "if", "illustrate", "illustrates",
            "illustrating", "image", "images", "immediate", "implement", "implementation",
            "implemented", "implementing", "implements", "import", "important", "improve",
            "improving", "in", "include", "included", "includes", "including", "increase",
            "index", "indicate", "indicator", "indicators", "individual", "informal",
            "information", "inheritance", "initial", "initialize", "input", "insert",
            "insertion", "instance", "int", "integer", "ichy", "integration", "interactive",
            "interface", "interfaces", "internal", "international", "internet", "into",
            "introduction", "invalid", "inventory", "io", "is", "italic", "item", "items",
            "iteration", "iterative", "its", "java", "javadoc", "jbutton", "jcombobox",
            "jdialog", "jfilechooser", "jframe", "jlabel", "jmenu", "jmenubar", "jmenuitem",
            "joptionpane", "jpanel", "jscrollpane", "jtextpane", "jtoolbar", "keep", "key",
            "keyboard", "keyword", "keywords", "kindly", "know", "knowledge", "label",
            "language", "languages", "large", "larger", "last", "layout", "learning", "left",
            "length", "less", "level", "levels", "library", "lifecycle", "like", "limit",
            "limitation", "limitations", "line", "lines", "link", "list", "listener",
            "listeners", "literal", "literals", "live", "load", "loaded", "loading", "local",
            "location", "log", "logic", "logical", "login", "logs", "long", "look", "loop",
            "loops", "low", "main", "maintain", "maintaining", "make", "makes", "making",
            "manage", "management", "manager", "manual", "manually", "many", "margin", "mark",
            "marking", "marks", "match", "matches", "matching", "math", "mathematical",
            "mathematics", "matrix", "max", "maximum", "may", "meaning", "meaningful",
            "measurement", "mechanics", "mechanism", "memory", "menu", "message", "messages",
            "messaging", "method", "methodology", "methods", "metrics", "micro", "mini",
            "minimal", "minimum", "minute", "minutes", "mode", "model", "moderator", "modern",
            "modification", "modified", "modifier", "modifiers", "modify", "modular",
            "module", "money", "monitor", "monospaced", "month", "monthly", "more", "most",
            "move", "multiple", "multiline", "must", "my", "name", "names", "natural",
            "navigate", "necessary", "need", "needed", "needs", "netbeans", "network",
            "never", "new", "next", "no", "node", "non", "none", "normal", "not", "note",
            "notepad", "notes", "notify", "november", "now", "null", "number", "numbers",
            "numeric", "object", "objectives", "objects", "obtain", "occurrence",
            "occurrences", "of", "off", "office", "often", "ok", "on", "one", "online",
            "only", "oop", "open", "opened", "opening", "operate", "operates", "operating",
            "operation", "operations", "operator", "operators", "optimal", "option",
            "optional", "options", "or", "order", "organization", "organize", "organizing",
            "other", "our", "out", "output", "over", "override", "overview", "overwrite",
            "package", "page", "pages", "paint", "pair", "pairs", "panel", "paragraph",
            "paragraphs", "parameter", "parameters", "parameterized", "part", "participation",
            "particular", "pass", "passed", "paste", "path", "pattern", "pdf", "peninsula",
            "people", "per", "perform", "performance", "performed", "permission", "personal",
            "phone", "physical", "pick", "piece", "plain", "plan", "planning", "platform",
            "play", "player", "please", "point", "points", "polymorphism", "pop", "position",
            "positive", "possible", "power", "practical", "practicality", "practice",
            "preferences", "present", "presentation", "presented", "preserve", "preset",
            "presets", "press", "previous", "primary", "print", "printout", "prioritize",
            "private", "problem", "problems", "procedure", "process", "processing", "produce",
            "product", "productivity", "professional", "program", "programmable",
            "programmer", "programmers", "programming", "programs", "progress", "project",
            "projects", "prompt", "properly", "properties", "property", "protect",
            "protected", "provide", "provided", "provides", "public", "publish", "punctuation",
            "purpose", "purposes", "put", "quality", "query", "question", "questions",
            "quick", "quickly", "quiz", "rand", "random", "range", "rate", "rates", "rating",
            "raw", "read", "readability", "readable", "reader", "readers", "reading", "ready",
            "real", "reason", "recall", "receive", "record", "records", "redo", "reduce",
            "reducing", "reference", "references", "refresh", "register", "registration",
            "regular", "related", "relative", "relevant", "reminder", "reminders", "remove",
            "rename", "render", "repaint", "repeat", "replace", "replaced", "replacement",
            "report", "reported", "reports", "repository", "represent", "representation",
            "represented", "representing", "require", "required", "requirement",
            "requirements", "requires", "reservation", "reset", "resize", "resource",
            "resources", "response", "responsibilities", "restrict", "result", "results",
            "retrieve", "return", "returns", "reuse", "review", "rich", "right", "robust",
            "role", "room", "rooms", "root", "rotate", "row", "rows", "rubric", "rule",
            "rules", "run", "running", "runtime", "rural", "safe", "safely", "safety",
            "same", "sample", "samplecount", "sans", "save", "saved", "saving", "scale",
            "scales", "scan", "scanner", "scanning", "scenario", "scenarios", "schedule",
            "scheduler", "school", "scientific", "scope", "score", "scores", "scoring",
            "screen", "script", "scroll", "scrollpane", "sdlc", "search", "searching",
            "second", "section", "sections", "security", "see", "select", "selected",
            "selection", "selector", "send", "sensitive", "sentence", "sentences",
            "separator", "sequence", "serial", "serif", "service", "services", "session",
            "set", "setting", "settings", "several", "severe", "shape", "shapes", "share",
            "sharing", "sheet", "shift", "short", "shortcut", "should", "show", "showcasing",
            "side", "simple", "since", "single", "size", "sizes", "skill", "skillful",
            "skills", "small", "software", "solution", "solutions", "solve", "solving",
            "some", "somewhat", "sort", "source", "sources", "south", "space", "spaces",
            "spacing", "specific", "specified", "specify", "speed", "spell", "spelling",
            "split", "sports", "src", "staff", "stage", "standard", "standards", "start",
            "starting", "state", "statement", "statements", "static", "statistical",
            "statistics", "status", "step", "steps", "stock", "stopwatch", "storage", "store",
            "stored", "stream", "stress", "strict", "string", "strings", "strong",
            "structural", "structure", "structured", "structures", "student", "students",
            "studio", "studioedit", "study", "style", "styled", "styleddocument", "styles",
            "sub", "subject", "subjects", "submission", "submit", "substring", "subtract",
            "succeed", "successful", "successfully", "such", "suggestion", "suggestions",
            "suit", "suitable", "sum", "summary", "super", "supply", "supplyvoltage",
            "support", "supported", "supports", "sure", "surname", "swing", "switch",
            "switches", "symbol", "symbols", "sync", "synchronization", "syntax", "system",
            "systems", "table", "tables", "tag", "tags", "take", "taken", "taking", "tally",
            "target", "task", "tasks", "teacher", "teachers", "team", "teamwork", "technical",
            "technology", "tell", "template", "term", "terms", "test", "tested", "testing",
            "tests", "text", "texts", "than", "that", "the", "their", "them", "theme", "then",
            "there", "these", "they", "this", "those", "thread", "three", "threshold",
            "through", "throughout", "throw", "throws", "tic", "ticket", "time", "timed",
            "timer", "timers", "times", "timing", "title", "to", "today", "todo", "toe",
            "toggle", "token", "tokens", "tool", "toolbar", "toolkit", "tools", "top",
            "total", "totalenergy", "track", "tracker", "tracking", "transaction",
            "transitions", "tree", "trigonometry", "true", "trustworthy", "try", "turn",
            "two", "txt", "type", "types", "typography", "ui", "uml", "unable", "unclear",
            "under", "underline", "underlying", "understand", "understandable",
            "understanding", "undo", "unit", "university", "unknown", "unsaved", "until",
            "up", "update", "updated", "updates", "updating", "upload", "upon", "uppercase",
            "us", "usage", "use", "used", "useful", "user", "users", "uses", "using",
            "utility", "utilize", "valid", "validate", "validated", "validation", "value",
            "values", "variable", "variables", "various", "verbal", "verify", "version",
            "versatile", "vertical", "very", "via", "viability", "video", "view", "viewer",
            "viewing", "virtual", "visible", "visual", "vital", "vocabulary", "void",
            "voltage", "voting", "warning", "was", "waterfall", "way", "we", "weather",
            "web", "week", "weekly", "weight", "weighted", "weighting", "well", "were",
            "west", "what", "when", "where", "whether", "which", "while", "white",
            "whitespace", "who", "whole", "wide", "width", "will", "win", "window", "windows",
            "wins", "with", "withdraw", "withdrawal", "within", "without", "word", "words",
            "work", "workflow", "working", "works", "workspace", "world", "would", "wrap",
            "write", "writer", "writers", "writing", "written", "wrong", "year", "yes",
            "yet", "you", "your", "zero", "zoom", " engineeringcalculator", "computeaveragepower"
        };
        for (String w : coreWords) {
            dictionary.add(w.toLowerCase().trim());
        }
    }

    public void addWordToCustomDictionary(String word) {
        if (word != null && !word.trim().isEmpty()) {
            dictionary.add(word.toLowerCase().trim());
        }
    }

    public List<SpellCheckMatch> scanTextForMisspellings(String text) {
        List<SpellCheckMatch> results = new ArrayList<>();
        Matcher wordMatcher = Pattern.compile("\\b[A-Za-z]{2,}\\b").matcher(text);

        while (wordMatcher.find()) {
            String token = wordMatcher.group();
            String lower = token.toLowerCase();
            boolean isCamelCase = !token.equals(token.toLowerCase())
                    && !token.equals(token.toUpperCase())
                    && !Character.isLowerCase(token.charAt(0));
            if (!dictionary.contains(lower) && !isCamelCase) {
                results.add(new SpellCheckMatch(token, wordMatcher.start()));
            }
        }
        return results;
    }

    public List<String> getSuggestions(String targetWord, int maxSuggestions) {
        String lowerTarget = targetWord.toLowerCase();
        List<String> bestMatches = new ArrayList<>();
        List<Integer> bestDistances = new ArrayList<>();

        for (String candidate : dictionary) {
            if (Math.abs(candidate.length() - lowerTarget.length()) > 2) {
                continue;
            }
            int dist = computeLevenshteinDistance(lowerTarget, candidate);
            if (dist <= 2) {
                bestMatches.add(candidate);
                bestDistances.add(dist);
            }
        }

        for (int i = 0; i < bestMatches.size() - 1; i++) {
            for (int j = i + 1; j < bestMatches.size(); j++) {
                if (bestDistances.get(j) < bestDistances.get(i)) {
                    Collections.swap(bestDistances, i, j);
                    Collections.swap(bestMatches, i, j);
                }
            }
        }

        return bestMatches.subList(0, Math.min(maxSuggestions, bestMatches.size()));
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
