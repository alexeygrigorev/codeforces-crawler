package codeforcescrawl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class BowFeatureExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BowFeatureExtractor.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Database db = Factory.createDatabase();

        boolean tokenized = false;
        if (args.length > 0) {
            if ("tokenized".equals(args[0])) {
                tokenized = true;
            }
        }

        AtomicInteger cnt = new AtomicInteger();

        new File("out").mkdirs();
        PrintWriter pw = null;
        if (tokenized) {
            pw = new PrintWriter("out/dump-tokenized.jsonl");
        } else {
            pw = new PrintWriter("out/dump-original.jsonl");
        }

        PrintWriter finalPw = pw;
        boolean finalTokenized = tokenized;

        db.iterateOverAllScrapedSubmissions(submission -> {
            Map<String, Object> map = new HashMap<>();
            map.put("submission_id", submission.getSubmissionId());
            map.put("language", submission.getLanguage());

            if (finalTokenized) {
                String tokens = tokenize(submission.getSource());
                map.put("source", tokens);
            } else {
                map.put("source", submission.getSource());
            }

            String json = toJson(map);
            finalPw.println(json);

            if (cnt.incrementAndGet() % 1000 == 0) {
                LOGGER.debug("so far processed {} lines", cnt);
            }
        });

        pw.flush();
        pw.close();
    }

    private static String toJson(Map<String, Object> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String tokenize(String source) {
        try {
            return tokenizeUnsafe(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String tokenizeUnsafe(String source) throws Exception {
        List<String> result = new ArrayList<>();
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(source));

        // tokenizer.parseNumbers();
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.wordChars('%', '%');
        tokenizer.wordChars('#', '#');
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.ordinaryChar('-');
        tokenizer.ordinaryChar('.');
        tokenizer.ordinaryChars(0, ' ');
        tokenizer.eolIsSignificant(true);

        int tok = tokenizer.nextToken();

        while (tok != StreamTokenizer.TT_EOF) {
            tok = tokenizer.nextToken();

            switch (tok) {
                case StreamTokenizer.TT_NUMBER:
                    // double n = tokenizer.nval;
                    result.add("-NUMBER-");
                    break;

                case StreamTokenizer.TT_WORD:
                    String word = tokenizer.sval;
                    result.add(word);
                    break;

                case '"':
                case '\'':
                    String chars = tokenizer.sval;
                    result.add(chars);
                    break;

                case StreamTokenizer.TT_EOL:
                case StreamTokenizer.TT_EOF:
                    break;

                default:
                    char character = (char) tokenizer.ttype;
                    result.add(String.valueOf(character));
                    break;
            }
        }

        return String.join(" ", result);
    }



}
