package mejorandome.mejorandome;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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

import mejorandome.mejorandome.SeekBarClasses.RangeSliderView;

public class MoodActivity extends AppCompatActivity {

    private Button saveButton;
    private int idPaciente;
    private Toolbar mToolbar;
    private EditText pesoText;
    private EditText problemaFisicoText;
    private TextView statusP1;
    private TextView statusP2;
    private TextView statusP3;
    private TextView statusP4;
    private TextView statusN1;
    private TextView statusN2;
    private TextView statusN3;
    private TextView statusN4;
    private RangeSliderView seekP1;
    private RangeSliderView seekP2;
    private RangeSliderView seekP3;
    private RangeSliderView seekP4;
    private RangeSliderView seekN1;
    private RangeSliderView seekN2;
    private RangeSliderView seekN3;
    private RangeSliderView seekN4;
    private RangeSliderView drugSeek;
    private Switch drugCheck;
    private LinearLayout abstinenciaLayout;
    private LinearLayout sinAbstinenciaLayout;
    private int consumo;
    private int peso;
    private int estadoP1,estadoP2,estadoP3,estadoP4;
    private int estadoN1,estadoN2,estadoN3,estadoN4;
    private String problemaFisico;
    private boolean abstinencia;
    private boolean response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        saveButton = (Button) findViewById(R.id.save_mood_button);

        statusP1 = (TextView) findViewById(R.id.estadoP1_text);
        statusP2 = (TextView) findViewById(R.id.estadoP2_text);
        statusP3 = (TextView) findViewById(R.id.estadoP3_text);
        statusP4 = (TextView) findViewById(R.id.estadoP4_text);
        statusN1 = (TextView) findViewById(R.id.estadoN1_text);
        statusN2 = (TextView) findViewById(R.id.estadoN2_text);
        statusN3 = (TextView) findViewById(R.id.estadoN3_text);
        statusN4 = (TextView) findViewById(R.id.estadoN4_text);

        seekP1 = (RangeSliderView) findViewById(R.id.estadoP1_seek);
        seekP2 = (RangeSliderView) findViewById(R.id.estadoP2_seek);
        seekP3 = (RangeSliderView) findViewById(R.id.estadoP3_seek);
        seekP4 = (RangeSliderView) findViewById(R.id.estadoP4_seek);
        seekN1 = (RangeSliderView) findViewById(R.id.estadoN1_seek);
        seekN2 = (RangeSliderView) findViewById(R.id.estadoN2_seek);
        seekN3 = (RangeSliderView) findViewById(R.id.estadoN3_seek);
        seekN4 = (RangeSliderView) findViewById(R.id.estadoN4_seek);

        drugSeek = (RangeSliderView) findViewById(R.id.consumo_seek);
        drugCheck = (Switch) findViewById(R.id.drugCheck);

        pesoText = (EditText) findViewById(R.id.peso);
        problemaFisicoText = (EditText) findViewById(R.id.problema_fisico);

        abstinenciaLayout = (LinearLayout) findViewById(R.id.consumo_abstinencia);
        sinAbstinenciaLayout = (LinearLayout) findViewById(R.id.consumo_sin_abstinencia);

        new GetMoodData().execute();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckMoodData();
            }
        });

    }

    private class GetMoodData extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "Mood";
            final String SOAP_ACTION = "http://tempuri.org/IService1/Mood";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente); // Paso parametros al WS

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapObject) sobre.getResponse();

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
                ShowMood(resultado);
            }
            else
            {

            }
            super.onPostExecute(result);
        }
    }

    public void ShowMood(SoapObject mood)
    {
        statusP1.setText(mood.getProperty("NombreEstado1").toString());
        statusP2.setText(mood.getProperty("NombreEstado2").toString());
        statusN1.setText(mood.getProperty("NombreEstado5").toString());
        statusN2.setText(mood.getProperty("NombreEstado6").toString());

        if(mood.getProperty("NombreEstado3")==null)
        {
            statusP3.setVisibility(View.GONE);
            seekP3.setVisibility(View.GONE);
        }
        else
        {
            statusP3.setText(mood.getProperty("NombreEstado3").toString());
        }

        if(mood.getProperty("NombreEstado4")==null)
        {
            statusP4.setVisibility(View.GONE);
            seekP4.setVisibility(View.GONE);
        }
        else
        {
            statusP3.setText(mood.getProperty("NombreEstado4").toString());
        }

        //------------------------------------------------------------------------------------

        if(mood.getProperty("NombreEstado7")==null)
        {
            statusN3.setVisibility(View.GONE);
            seekN3.setVisibility(View.GONE);
        }
        else
        {
            statusN3.setText(mood.getProperty("NombreEstado7").toString());
        }

        if(mood.getProperty("NombreEstado8")==null)
        {
            statusN4.setVisibility(View.GONE);
            seekN4.setVisibility(View.GONE);
        }
        else
        {
            statusN3.setText(mood.getProperty("NombreEstado7").toString());
        }

        if(true)   // Abstinencia completa
        {
            sinAbstinenciaLayout.setVisibility(View.GONE);
            abstinenciaLayout.setVisibility(View.VISIBLE);
            abstinencia = true;
        }
        else
        {
            sinAbstinenciaLayout.setVisibility(View.VISIBLE);
            abstinenciaLayout.setVisibility(View.GONE);
            abstinencia = false;
        }
    }

    public void CheckMoodData()
    {
        if(Integer.parseInt(pesoText.getText().toString())>20)
        {
            peso = Integer.parseInt(pesoText.getText().toString());

            if(abstinencia)
            {
                if(drugCheck.isChecked()) consumo = 1;
                else consumo = 0;
            }
            else
            {
                consumo = drugSeek.getIndex();
            }

            estadoP1 = seekP1.getIndex();
            estadoP2 = seekP2.getIndex();
            estadoP3 = seekP3.getIndex();
            estadoP4 = seekP4.getIndex();
            estadoN1 = seekN1.getIndex();
            estadoN2 = seekN2.getIndex();
            estadoN3 = seekN3.getIndex();
            estadoN4 = seekN4.getIndex();

            problemaFisico = problemaFisicoText.getText().toString();

            finish();
        }
        else
        {
            Toast toast = Toast.makeText(MoodActivity.this, "Debes completar tu Peso y Estado Animico", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class sendMoodData extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "sendMoodData";
            final String SOAP_ACTION = "http://tempuri.org/IService1/sendMoodData";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente",idPaciente);
                request.addProperty("estadoP1",estadoP1);
                request.addProperty("estadoP2",estadoP2);
                request.addProperty("estadoP3",estadoP3);
                request.addProperty("estadoP4",estadoP4);
                request.addProperty("estadoN1",estadoN1);
                request.addProperty("estadoN2",estadoN2);
                request.addProperty("estadoN3",estadoN3);
                request.addProperty("estadoN4",estadoN4);
                request.addProperty("peso",peso);
                request.addProperty("consumo",consumo);
                request.addProperty("problemaFisico",problemaFisico);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapPrimitive) sobre.getResponse();

                Log.i("Resultado", resultado.toString());


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
                Toast toast = Toast.makeText(MoodActivity.this, "Tu registro ha sido guardado", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(MoodActivity.this, "No se ha logrado guardar el registro diario", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
