package game.hangman.inx95.hangmangame.Util;

import org.json.JSONObject;

/**
 * Created by inx95 on 16-4-17.
 */
public class TextUtil {
    public static String decodeDataJson(JSONObject dataJson) {
        if (dataJson == null) return null;
        StringBuilder builder = new StringBuilder();
        String[] keys = {"playerId","numberOfWordsToGuess","numberOfGuessAllowedForEachWord", "totalWordCount", "correctWordCount", "word", "totalWrongGuessCount", "score", "datetime"};
        for (String key : keys) {
            Object value = dataJson.opt(key);
            if (value != null) builder.append(key).append(": ").append(value).append("\n");
        }
        return builder.toString();
    }
}
