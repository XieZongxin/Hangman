package game.hangman.inx95.hangmangame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import game.hangman.inx95.hangmangame.Runnable.ErrorRunnable;
import game.hangman.inx95.hangmangame.Runnable.SuccessRunnable;
import game.hangman.inx95.hangmangame.Util.ViewUtil;
import game.hangman.inx95.hangmangame.Util.NetWorkUtil;
import game.hangman.inx95.hangmangame.Util.TextUtil;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private RequestQueue mRequestQueue;
    private SharedPreferences mSp;
    private String mUrl;
    private String mSessionId;
    private EditText mGuessTv;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        mUrl = mSp.getString("url", null);
        mSessionId = mSp.getString("sessionId", null);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button mStartGameBt = (Button) findViewById(R.id.start_game_bt);
        Button mGiveMeAWordBt = (Button) findViewById(R.id.give_me_a_word_bt);
        Button mMakeAGuessBt = (Button) findViewById(R.id.make_a_guess_bt);
        Button mGetYourResultBt = (Button) findViewById(R.id.get_your_result_bt);
        Button mSubmitYourResultBt = (Button) findViewById(R.id.submit_your_result_bt);
        mGuessTv = (EditText) findViewById(R.id.guess_letter_et);
        mStartGameBt.setOnClickListener(this);
        mGiveMeAWordBt.setOnClickListener(this);
        mMakeAGuessBt.setOnClickListener(this);
        mGetYourResultBt.setOnClickListener(this);
        mSubmitYourResultBt.setOnClickListener(this);
        mRequestQueue = Volley.newRequestQueue(this);
    }


    @Override
    public void onClick(View v) {
        String content = null;
        ViewUtil.showProgress(this, true, mLoginFormView, mProgressView);
        switch (v.getId()) {
            case R.id.start_game_bt:
                doStartGame();
                content = "Start A Game";
                break;
            case R.id.give_me_a_word_bt:
                doGiveMeAWord();
                content = "Give You A Word";
                break;
            case R.id.make_a_guess_bt:
                doMakeAGuess();
                content = "Make A Guess";
                break;
            case R.id.get_your_result_bt:
                doGetYourResult();
                content = "Get Your Result";
                break;
            case R.id.submit_your_result_bt:
                doSubmitYourResult();
                content = "Submit Your Result";
                break;
        }
        if (content != null) Snackbar.make(v, content, Snackbar.LENGTH_LONG).show();
    }

    private void doSubmitYourResult() {
        try {
            NetWorkUtil.doSubmitYourResult(mUrl, mSessionId, mRequestQueue, new SuccessRunnable() {
                @Override
                public void run(JSONObject responseJsonObject) throws JSONException {
                    successRunAndShow(responseJsonObject, "Start Game");
                }
            }, new ErrorRunnable() {
                @Override
                public void run(VolleyError error) {
                    errorRunAndShow(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doStartGame() {
        String playerId = mSp.getString("playerId", null);
        try {
            NetWorkUtil.doStartGame(mUrl, playerId, mRequestQueue, new SuccessRunnable() {
                @Override
                public void run(JSONObject responseJsonObject) throws JSONException {
                    mSessionId = responseJsonObject.optString("sessionId");
                    mSp.edit().putString("sessionId", mSessionId).apply();
                    successRunAndShow(responseJsonObject, "Start Game");
                }
            }, new ErrorRunnable() {
                @Override
                public void run(VolleyError error) {
                    errorRunAndShow(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doGetYourResult() {
        try {
            NetWorkUtil.doGetYourResult(mUrl, mSessionId, mRequestQueue, new SuccessRunnable() {
                @Override
                public void run(JSONObject responseJsonObject) throws JSONException {
                    successRunAndShow(responseJsonObject, "Get Your Result");
                }
            }, new ErrorRunnable() {
                @Override
                public void run(VolleyError error) {
                    errorRunAndShow(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doMakeAGuess() {
        String guess = mGuessTv.getText().toString();
        try {
            NetWorkUtil.doMakeAGuess(mUrl, mSessionId, guess, mRequestQueue, new SuccessRunnable() {
                @Override
                public void run(JSONObject responseJsonObject) throws JSONException {
                    successRunAndShow(responseJsonObject, "Make A Guess");
                }
            }, new ErrorRunnable() {
                @Override
                public void run(VolleyError error) {
                    errorRunAndShow(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void successRunAndShow(JSONObject responseJsonObject, String dialogTitle) throws JSONException {
        ViewUtil.showProgress(this, false, mLoginFormView, mProgressView);
        JSONObject data = responseJsonObject.getJSONObject("data");
        String decodeData = TextUtil.decodeDataJson(data);
        ViewUtil.showDialog(MainActivity.this, dialogTitle, decodeData);
    }

    private void errorRunAndShow(VolleyError error) {
        ViewUtil.showProgress(this, false, mLoginFormView, mProgressView);
        Log.wtf("error", error);
        ViewUtil.showDialog(MainActivity.this, "Error", error.toString());
    }

    private void exceptionShow(Exception e) {
        ViewUtil.showProgress(this, false, mLoginFormView, mProgressView);
        Log.wtf("error", e);
        ViewUtil.showDialog(MainActivity.this, "Error", e.toString());
    }

    private void doGiveMeAWord() {
        try {
            NetWorkUtil.doGiveMeAWord(mUrl, mSessionId, mRequestQueue, new SuccessRunnable() {
                @Override
                public void run(JSONObject responseJsonObject) {
                    try {
                        successRunAndShow(responseJsonObject, "Give Me A Word");
                    } catch (JSONException e) {
                        exceptionShow(e);
                    }
                }
            }, new ErrorRunnable() {
                @Override
                public void run(VolleyError error) {
                    errorRunAndShow(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

