package mejorandome.mejorandome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingsActivity extends AppCompatActivity {

    private Button logoutButton;
    private Button changepassOption;
    private EditText password1;
    private EditText password2;
    private TextView changePassMessage;
    private Button changePassButton;
    private LinearLayout changePassLayout;
    private String pass1;
    private String pass2;
    private Utils utils;
    private int idPaciente;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        utils = new Utils();

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        logoutButton = (Button) findViewById(R.id.logout_button);
        changepassOption = (Button) findViewById(R.id.change_pass_option);
        changePassLayout = (LinearLayout) findViewById(R.id.change_pass_layout);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        changePassButton = (Button) findViewById(R.id.change_pass_button);
        changePassMessage = (TextView) findViewById(R.id.change_pass_message);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new logout().execute();

            }
        });

        changepassOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changePassLayout.getVisibility() == View.VISIBLE)
                {
                    changePassLayout.setVisibility(View.GONE);
                }
                else
                {
                    changePassLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendNewPassword();
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class changePassword extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "changePassword";
            final String SOAP_ACTION = "http://tempuri.org/IService1/changePassword";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente",idPaciente);
                request.addProperty("pass",pass1);
                request.addProperty("newPass",pass2);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapPrimitive) sobre.getResponse();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Error = e.toString();

            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                Error = soapFault.toString();

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Error = e.toString();

            } catch (IOException e) {
                e.printStackTrace();
                Error = e.toString();

            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            int changePassResult;

            if(resultado!=null)
            {
                changePassResult = Integer.parseInt(resultado.toString());

                if(changePassResult==1)                // cambio exitoso
                {
                    Toast toast = Toast.makeText(SettingsActivity.this, "La contraseña ha sido cambiada", Toast.LENGTH_SHORT);
                    toast.show();
                    changePassLayout.setVisibility(View.GONE);
                }
                if(changePassResult==-1)
                {
                    changePassMessage.setVisibility(View.VISIBLE);
                    changePassMessage.setText("Contraseña actual no corresponde");
                }
                if(changePassResult==0)
                {
                    changePassMessage.setVisibility(View.VISIBLE);
                    changePassMessage.setText("No se ha logrado cambiar la contraseña");
                }
            }
            else
            {
                changePassMessage.setVisibility(View.VISIBLE);
                changePassMessage.setText("Error al cambiar la contraseña");
            }
        }
    }

    public void SendNewPassword()
    {
        pass1 = password1.getText().toString();
        pass2 = password2.getText().toString();

        if(password1.getText().toString().equals("") || password2.getText().toString().equals(""))
        {
            changePassMessage.setVisibility(View.VISIBLE);
            changePassMessage.setText("Debe completar los campos");
        }
        else
        {
            if(password1.getText().toString().equals(password2.getText().toString()))
            {
                changePassMessage.setVisibility(View.VISIBLE);
                changePassMessage.setText("Las contraseñas son iguales");
            }
            else new changePassword().execute();
        }
    }

    private class logout extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "logout";
            final String SOAP_ACTION = "http://tempuri.org/IService1/logout";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("macAddress",utils.getMACAddress("wlan0"));

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapPrimitive) sobre.getResponse();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Error = e.toString();

            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                Error = soapFault.toString();

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Error = e.toString();

            } catch (IOException e) {
                e.printStackTrace();
                Error = e.toString();

            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

}
