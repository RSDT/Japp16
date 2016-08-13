package nl.rsdt.japp.application.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.auth.Authentication;
import nl.rsdt.japp.jotial.data.structures.area348.UserInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button button = (Button)findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!((EditText)findViewById(R.id.username)).getText().toString().isEmpty() && !((EditText)findViewById(R.id.password)).getText().toString().isEmpty()) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    View focus = LoginActivity.this.getCurrentFocus();
                    if(focus != null) {
                        imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                    }

                    Authentication authentication = new Authentication.Builder()
                            .setUsername(((EditText)findViewById(R.id.username)).getText().toString())
                            .setPassword(((EditText)findViewById(R.id.password)).getText().toString())
                            .setCallback(new Authentication.OnAuthenticationCompletedCallback() {
                                @Override
                                public void onAuthenticationCompleted(Authentication.AuthenticationResult result) {
                                    if(result.isSucceeded())
                                    {
                                        UserInfo.collect();
                                        if(JappPreferences.isFirstRun())
                                        {
                                            Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                    else
                                    {
                                        Snackbar.make(findViewById(R.id.login_layout), result.getMessage(), Snackbar.LENGTH_LONG)
                                                .setActionTextColor(Color.BLUE)
                                                .show();
                                    }
                                }
                            })
                            .create();
                    authentication.executeAsync();
                } else {
                    Snackbar.make(findViewById(R.id.login_layout), "Vul alsjeblieft alle velden in!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.BLUE)
                            .show();
                }

            }
        });

    }
}
