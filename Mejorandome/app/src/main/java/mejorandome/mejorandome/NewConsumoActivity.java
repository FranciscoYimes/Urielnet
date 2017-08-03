package mejorandome.mejorandome;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class NewConsumoActivity extends AppCompatActivity {

    private Spinner spinner;
    private String dateTime;
    private String companiaString;
    private int cantidadInt;
    private String obtencionString;
    private String tipoConsumo;

    private EditText compania;
    private EditText obtecion;
    private EditText cantidad;

    private TextView dateEditText;
    private TextView timeEditText;

    private Toolbar mToolbar;

    private int idPaciente;
    private SimpleProgressDialog dialog;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private Button saveButton;

    final Calendar c = Calendar.getInstance();
    private int mYear; // current year
    private int mMonth; // current month
    private int mDay; // current day
    private int mHour;
    private int mMinute;

    private GoogleApiClient client;

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

        mYear = c.get(Calendar.YEAR); // current year
        mMonth = c.get(Calendar.MONTH); // current month
        mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        compania = (EditText) findViewById(R.id.compania);
        obtecion = (EditText) findViewById(R.id.obtencion);
        cantidad = (EditText) findViewById(R.id.cantidad_consumo);

        dateEditText = (TextView) findViewById(R.id.date_consumo);
        timeEditText = (TextView) findViewById(R.id.time_consumo);

        saveButton = (Button) findViewById(R.id.save_consumo_button);

        dateEditText.setText(String.valueOf(mDay) + "/" + String.valueOf(mMonth) + "/" + String.valueOf(mYear));
        timeEditText.setText(String.valueOf(mHour)+":"+String.valueOf(mMinute));

        datePickerDialog = new DatePickerDialog(NewConsumoActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        dateEditText.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));

                    }
                }, mYear, mMonth, mDay
        );

        timePickerDialog = new TimePickerDialog(NewConsumoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeEditText.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
            }
        },mHour,mMinute,false);

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();
            }
        });

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void AddItemsToSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sustancia_items, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        spinner.setAdapter(adapter);
    }

    public void Verify() {
        if (compania.getText().toString().equals("") || obtecion.getText().toString().equals("") || cantidad.getText().toString().equals("")) {
            Toast toast = Toast.makeText(NewConsumoActivity.this, "Debes completar los campos.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            companiaString = compania.getText().toString();
            cantidadInt = Integer.parseInt(cantidad.getText().toString());
            tipoConsumo = spinner.getSelectedItem().toString();
            obtencionString = obtecion.getText().toString();
            new AddConsumo().execute();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NewConsumo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class AddConsumo extends AsyncTask<Void, Void, Void> {
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

        protected void onPostExecute(Void result) {
            dialog.dismiss();

            if (resultado != null) {
                response = Boolean.parseBoolean(resultado.toString());
            } else response = false;

            if (response) {
                Toast toast = Toast.makeText(NewConsumoActivity.this, "Se ha guardado el consumo", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            } else {
                Toast toast = Toast.makeText(NewConsumoActivity.this, "No se ha logrado guardar el consumo.", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(result);
        }
    }

}
