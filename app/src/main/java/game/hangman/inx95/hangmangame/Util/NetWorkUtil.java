package game.hangman.inx95.hangmangame.Util;

import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import game.hangman.inx95.hangmangame.Runnable.ErrorRunnable;
import game.hangman.inx95.hangmangame.Runnable.SuccessRunnable;

/**
 * Created by inx95 on 16-4-16.
 */
public class NetWorkUtil {
    private final static String ACTION_START_GAME = "startGame";
    private final static String ACTION_NEXT_WORD = "nextWord";
    private final static String ACTION_MAKE_A_GUESS = "guessWord";
    private final static String ACTION_GET_YOUR_RESULT = "getResult";
    private final static String ACTION_SUBMIT_YOUR_RESULT = "submitResult";


    public static void doRequest(String url, @Nullable String playerId, @Nullable String action, @Nullable String sessionId, @Nullable String guess, RequestQueue queue, final SuccessRunnable successRunnable, final ErrorRunnable errorRunnable) throws JSONException {

//        if (!ValidateUtil.isUrlLegal(url)) return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("playerId", playerId)
                .put("action", action)
                .put("sessionId", sessionId)
                .put("guess", guess);
        queue.add(new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            successRunnable.run(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorRunnable.run(error);
            }
        }
        ));
    }

    public static void doStartGame(String url, String playerId, RequestQueue queue, SuccessRunnable successRunnable, ErrorRunnable errorRunnable) throws JSONException {
        doRequest(url, playerId, ACTION_START_GAME, null, null, queue, successRunnable, errorRunnable);
    }

    public static void doGiveMeAWord(String url, String sessionId, RequestQueue queue, SuccessRunnable successRunnable, ErrorRunnable errorRunnable) throws JSONException {
        doRequest(url, null, ACTION_NEXT_WORD, sessionId, null, queue, successRunnable, errorRunnable);
    }

    public static void doMakeAGuess(String url, String sessionId, String guess, RequestQueue queue, SuccessRunnable successRunnable, ErrorRunnable errorRunnable) throws JSONException {
        doRequest(url, null, ACTION_MAKE_A_GUESS, sessionId, guess, queue, successRunnable, errorRunnable);
    }

    public static void doGetYourResult(String url, String sessionId, RequestQueue queue, SuccessRunnable successRunnable, ErrorRunnable errorRunnable) throws JSONException {
        doRequest(url, null, ACTION_GET_YOUR_RESULT, sessionId, null, queue, successRunnable, errorRunnable);
    }

    public static void doSubmitYourResult(String url, String sessionId, RequestQueue queue, SuccessRunnable successRunnable, ErrorRunnable errorRunnable) throws JSONException {
        doRequest(url, null, ACTION_SUBMIT_YOUR_RESULT, sessionId, null, queue, successRunnable, errorRunnable);
    }
}
