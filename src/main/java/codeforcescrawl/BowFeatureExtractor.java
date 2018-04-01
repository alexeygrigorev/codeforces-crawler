package codeforcescrawl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

//import com.fasterxml.jackson.jr.ob.JSON;

public class BowFeatureExtractor {

    /*public static void main(String[] args) throws Exception {
        List<Submission> submissions = extract();

        File file = new File("out.json.gz");
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            try (GZIPOutputStream gzip = new GZIPOutputStream(os, false)) {
                try (PrintWriter pw = new PrintWriter(gzip)) {
                    writeSubmissions(pw, submissions);
                }
            }
        }
    }

    public static void writeSubmissions(PrintWriter pw, List<Submission> submissions) throws Exception {
        int cnt = 0;

        for (Submission submission : submissions) {
            Map<String, Object> json = new HashMap<>();
            json.put("submission_id", submission.getSubmissionId());
            json.put("language", submission.getLanguage());

            List<String> tokens = tokenize(submission.getSource());
            json.put("source", tokens);

            pw.println(JSON.std.asString(json));

            cnt++;
            if (cnt % 2500 == 0) {
                System.out.println("processing " + cnt + "th submission...");
            }
        }
    }

    private static List<String> tokenize(String source) throws Exception {
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

        return result;
    }

    private static List<Submission> extract() throws Exception {
        String query = "SELECT submission_id, source, language, status FROM submissions";

        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeforces", "root", "");

        List<Submission> res = new ArrayList<>();

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                int submissionId = rs.getInt(1);
                String source = rs.getString(2);
                String language = rs.getString(3);
                String status = rs.getString(4);
                res.add(new Submission(submissionId, language, status, null, source));
            }
        }

        return res;
    }

*/
}
