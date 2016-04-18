package game.hangman.inx95.hangmangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import game.hangman.inx95.hangmangame.Util.ViewUtil;

/**
 * Created by inx95 on 16-4-17.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mPlayerIdTv;
    private EditText mUrl;
    private TextView mShowTv;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUrl = (EditText) findViewById(R.id.url);
        mPlayerIdTv = (EditText) findViewById(R.id.player_id_tv);
        mShowTv = (TextView) findViewById(R.id.show_tv);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        ViewUtil.showProgress(this, true, mLoginFormView, mProgressView);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = mUrl.getText().toString();
        String playerId = mPlayerIdTv.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("playerId", playerId);
            jsonObject.put("action", "startGame");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        queue.add(new JsonObjectRequest(
                Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ViewUtil.showProgress(LoginActivity.this, false, mLoginFormView, mProgressView);
                        try {
                            mShowTv.setText(response.getString("message"));
                            decodeSuccessJson(response);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mShowTv.setText(e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorInfo = "error";
                        if (error instanceof TimeoutError) {
                            errorInfo = "time out";
                        } else if (error instanceof AuthFailureError) {
                            errorInfo = "playerId is not available";

                        }
                        mShowTv.setText(errorInfo);
                        ViewUtil.showDialog(LoginActivity.this, "异常", errorInfo);
                        ViewUtil.showProgress(LoginActivity.this, false, mLoginFormView, mProgressView);
                    }
                }
        ));
    }

    private void decodeSuccessJson(JSONObject response) throws JSONException {
        String sessionId = response.getString("sessionId");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("playerId", mPlayerIdTv.getText().toString())
                .putString("url", mUrl.getText().toString())
                .putString("sessionId", sessionId);
        editor.apply();
    }
}

