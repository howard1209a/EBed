import org.junit.Test;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class T2 {
    @Test
    public void t5() {
        String text = "我喜欢使用IK分词器进行中文分词。";

        try (StringReader reader = new StringReader(text)) {
            IKSegmenter segmenter = new IKSegmenter(reader, true);
            Lexeme lexeme;
            while ((lexeme = segmenter.next()) != null) {
                System.out.println(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
