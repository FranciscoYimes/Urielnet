package mejorandome.mejorandome;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class NewRedApoyoActivity extends AppCompatActivity {

    private Button addRedButton;
    private EditText name;
    private EditText number;
    private String nameString;
    private String numberString;
    private int idPaciente;
    private boolean response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_red_apoyo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addRedButton = (Button) findViewById(R.id.add_red_button);
        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        addRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString = name.getText().toString();
                numberString = number.getText().toString();

                if(nameString.equals("") || numberString.equals(""))
                {
                    Toast toast = Toast.makeText(NewRedApoyoActivity.this, "Debes ingresar los datos solicitados", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    new AddRedApoyo().execute();
                }
            }
        });
    }

    private class AddRedApoyo extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "InsertrecordNetworkSupport";
            final String SOAP_ACTION = "http://tempuri.org/IService1/InsertrecordNetworkSupport";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente); // Paso parametros al WS
                request.addProperty("nombre", nameString); // Paso parametros al WS
                request.addProperty("numeroTelefono", numberString); // Paso parametros al WS

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
            if(resultado!=null)
            {
                response = Boolean.parseBoolean(resultado.toString());
            }
            else response = false;

            if(response)
            {
                Toast toast = Toast.makeText(NewRedApoyoActivity.this, "Se ha agregado a la Red de Apoyo", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(NewRedApoyoActivity.this, "Error al registrar.", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(result);
        }
    }
}
