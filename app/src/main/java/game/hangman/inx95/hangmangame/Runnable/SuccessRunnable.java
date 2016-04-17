package game.hangman.inx95.hangmangame.Runnable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inx95 on 16-4-16.
 */
public interface SuccessRunnable {

    void run(JSONObject responseJsonObject) throws JSONException;

}
