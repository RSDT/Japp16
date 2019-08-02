package nl.rsdt.japp.application.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.auth.Authentication
import nl.rsdt.japp.jotial.data.structures.area348.UserInfo
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class LoginActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val passwordLost = findViewById<TextView>(R.id.password_forgotten)
        passwordLost.setOnClickListener {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val browser = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.forget_password_url, year)))
            startActivity(browser)
        }

        val button = findViewById<Button>(R.id.login)
        button.setOnClickListener {
            if (findViewById<EditText>(R.id.username).text.toString().isNotEmpty() && findViewById<EditText>(R.id.password).text.toString().isNotEmpty()) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val focus = this@LoginActivity.currentFocus
                if (focus != null) {
                    imm?.hideSoftInputFromWindow(focus.windowToken, 0)
                }

                val authentication = Authentication.Builder()
                        .setUsername((findViewById<View>(R.id.username) as EditText).text.toString())
                        .setPassword((findViewById<View>(R.id.password) as EditText).text.toString())
                        .setCallback (object : Authentication.OnAuthenticationCompletedCallback{
                            override fun onAuthenticationCompleted(result: Authentication.AuthenticationResult) {
                                if (result.isSucceeded) {
                                    UserInfo.collect()
                                        val intent = Intent(this@LoginActivity, SplashActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                } else {
                                    Snackbar.make(findViewById(R.id.login_layout), result.message, Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.BLUE)
                                            .show()
                                }
                            }

                        })
                        .create()
                authentication.executeAsync()
            } else {
                Snackbar.make(findViewById(R.id.login_layout), getString(R.string.fill_all_fields), Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.BLUE)
                        .show()
            }
        }

    }

    companion object {

        var TAG = "LoginActivity"
    }
}
