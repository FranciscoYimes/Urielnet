package mejorandome.mejorandome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;

import mejorandome.mejorandome.Adapters.Utils;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView forgotPassword;
    private int respuesta;
    private String userNameText;
    private String passText;
    private Utils utils;
    private EditText userName;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);

        utils = new Utils();

        userName = (EditText) findViewById(R.id.login_username);
        pass = (EditText) findViewById(R.id.login_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userName.getText().toString().equals("") || pass.getText().toString().equals(""))
                {
                    ShowWrongData();
                }
                else
                {
                    userNameText = userName.getText().toString();
                    passText = pass.getText().toString();
                    new sendLogginInfo().execute();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class sendLogginInfo extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "login";
            final String SOAP_ACTION = "http://tempuri.org/IService1/login";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("username", userNameText); // Paso parametros al WS
                request.addProperty("password", passText); // Paso parametros al WS
                request.addProperty("macAddress",utils.getMACAddress("wlan0"));

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapPrimitive) sobre.getResponse();

                Log.i("Resultado", resultado.toString());

                respuesta = Integer.parseInt(resultado.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Error = e.toString();
                respuesta = -2;

            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                Error = soapFault.toString();
                respuesta = -2;

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Error = e.toString();
                respuesta = -2;

            } catch (IOException e) {
                e.printStackTrace();
                Error = e.toString();
                respuesta = -2;
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(respuesta > 0)
            {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                i.putExtra("id",respuesta);
                startActivity(i);
                finish();
            }
            else
            {
                ShowWrongData();
            }
            super.onPostExecute(result);
        }
    }

    public void ShowWrongData()
    {

    }
}
