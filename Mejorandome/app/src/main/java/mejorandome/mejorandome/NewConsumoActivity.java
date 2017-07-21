package mejorandome.mejorandome;

import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class NewConsumoActivity extends AppCompatActivity {

    private Spinner spinner;
    private Spinner ampmSpinner;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private String dateTime;
    private String companiaString;
    private int cantidadInt;
    private String obtencionString;
    private String tipoConsumo;

    private EditText dayText;
    private EditText monthText;
    private EditText yearText;
    private EditText hourText;
    private EditText minuteText;
    private EditText compania;
    private EditText obtecion;
    private EditText cantidad;

    private Toolbar mToolbar;

    private int idPaciente;
    private SimpleProgressDialog dialog;

    private Button saveButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_consumo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPaciente = getIntent().getIntExtra("idPaciente", 0);
        dialog = SimpleProgressDialog.build(this, "Cargando...");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.spinner);
        ampmSpinner = (Spinner) findViewById(R.id.am_pm_spinner);

        dayText = (EditText) findViewById(R.id.day);
        monthText = (EditText) findViewById(R.id.month);
        yearText = (EditText) findViewById(R.id.year);
        hourText = (EditText) findViewById(R.id.hour);
        minuteText = (EditText) findViewById(R.id.minute);
        compania = (EditText) findViewById(R.id.compania);
        obtecion = (EditText) findViewById(R.id.obtencion);
        cantidad = (EditText) findViewById(R.id.cantidad_consumo);

        saveButton = (Button) findViewById(R.id.save_consumo_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verify();
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AddItemsToSpinner();
        AddItemsToAmPmSpinner();
        GetDateTime();
    }

    public void AddItemsToSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sustancia_items, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        spinner.setAdapter(adapter);
    }

    public void AddItemsToAmPmSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.am_pm, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        ampmSpinner.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void GetDateTime() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);

        dayText.setText(String.valueOf(day));
        monthText.setText(String.valueOf(month));
        yearText.setText(String.valueOf(year));
        hourText.setText(String.valueOf(hour));
        minuteText.setText(String.valueOf(minute));
    }

    public void Verify() {
        if (VerifyDate()) {
            if (compania.getText().toString().equals("") || obtecion.getText().toString().equals("") || cantidad.getText().toString().equals("")) {
                Toast toast = Toast.makeText(NewConsumoActivity.this, "Debes completar los campos.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                dateTime = yearText.getText().toString() + ", "+monthText.getText().toString() + ", " + dayText.getText().toString() + ", " + hourText.getText().toString() + ", " + minuteText.getText().toString() + ", 00";
                companiaString = compania.getText().toString();
                cantidadInt = Integer.parseInt(cantidad.getText().toString());
                tipoConsumo = spinner.getSelectedItem().toString();
                obtencionString = obtecion.getText().toString();

                new AddConsumo().execute();
            }
        }
        else
        {
            Toast toast = Toast.makeText(NewConsumoActivity.this, "La hora o fecha ingresada es incorrecta.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public boolean VerifyDate()
    {
        if(Integer.parseInt(dayText.getText().toString())>31 || Integer.parseInt(dayText.getText().toString())<1) return false;
        if(Integer.parseInt(monthText.getText().toString())>12 || Integer.parseInt(monthText.getText().toString())<1) return false;
        if(Integer.parseInt(yearText.getText().toString())>2019 || Integer.parseInt(yearText.getText().toString())<2017) return false;
        if(Integer.parseInt(hourText.getText().toString())>11 || Integer.parseInt(hourText.getText().toString())<0) return false;
        if(Integer.parseInt(minuteText.getText().toString())>59 || Integer.parseInt(minuteText.getText().toString())<0) return false;
        return true;
    }

    private class AddConsumo extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        boolean response;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "addConsumo";
            final String SOAP_ACTION = "http://tempuri.org/IService1/addConsumo";
            String Error;
            dialog.show();
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente); // Paso parametros al WS
                request.addProperty("FechaCreacion", dateTime); // Paso parametros al WS
                request.addProperty("Cantidad", cantidadInt); // Paso parametros al WS
                request.addProperty("TipoConsumo", tipoConsumo); // Paso parametros al WS
                request.addProperty("Obtencion", obtencionString); // Paso parametros al WS
                request.addProperty("Conpania", companiaString); // Paso parametros al WS


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
            dialog.dismiss();

            if(resultado!=null)
            {
                response = Boolean.parseBoolean(resultado.toString());
            }
            else response = false;

            if(response)
            {
                Toast toast = Toast.makeText(NewConsumoActivity.this, "Se ha guardado el consumo", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(NewConsumoActivity.this, "No se ha logrado guardar el consumo.", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(result);
        }
    }

}
