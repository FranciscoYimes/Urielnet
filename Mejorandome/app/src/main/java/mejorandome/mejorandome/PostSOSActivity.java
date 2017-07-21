package mejorandome.mejorandome;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class PostSOSActivity extends AppCompatActivity {

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private LinearLayout objetivoContent;
    private LinearLayout objetivoExample;
    private TextView objetivoTextExample;
    private int idPaciente;
    private SimpleProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sos);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        objetivoContent = (LinearLayout) findViewById(R.id.objetivos_content);
        objetivoExample = (LinearLayout) findViewById(R.id.objetivo_example);
        objetivoTextExample = (TextView) findViewById(R.id.objetivo_text_example);

        dialog = SimpleProgressDialog.build(this, "Cargando...");

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);

        Ion.with(image1).load("http://calgaryclosertohome.com/wp-content/uploads/Happy-Family.jpg");
        Ion.with(image2).load("http://www.fcs-midland.org/images/gui/family%20of%20four.jpg");
        Ion.with(image3).load("https://www.tripleoklaw.com/wp-content/uploads/2015/11/Family-Law-Image-800x600.jpg");
        Ion.with(image4).load("http://www.sbmegastudy.com/wp-content/uploads/2016/03/family-trip.jpg");

        new GetGoals().execute();
        new GetRedApoyo().execute();
    }

    public void AddGoals(SoapObject goal)
    {
        LinearLayout textLayout = new LinearLayout(getApplicationContext());
        textLayout.setLayoutParams(objetivoExample.getLayoutParams());
        textLayout.setBackgroundColor(Color.WHITE);
        TextView title = new TextView(getApplicationContext());
        title.setLayoutParams(objetivoTextExample.getLayoutParams());
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setText(goal.getProperty("Objetivo").toString());

        textLayout.addView(title);
        objetivoContent.addView(textLayout);
    }

    private class GetGoals extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "PatientObjectives";
            final String SOAP_ACTION = "http://tempuri.org/IService1/PatientObjectives";
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
                Object meetingList;

                for(int i = 0; i< resultado.getPropertyCount();i++)
                {
                    meetingList = resultado.getProperty(i);

                    if(meetingList instanceof SoapObject)
                    {
                        SoapObject meeting = (SoapObject) meetingList;
                        AddGoals(meeting);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    private void sendSMS(String phone, String name) {
        int permissionCheck = ContextCompat.checkSelfPermission(PostSOSActivity.this, android.Manifest.permission.SEND_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
            ActivityCompat.requestPermissions(PostSOSActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, 225);
        } else {
            SmsManager sms = SmsManager.getDefault();
            String message = "Estimado/a "+name+", nuestro paciente ha activado un mensaje de alerta, rogamos ponerse en contacto de inmediato.";
            sms.sendTextMessage(phone, null, message , null, null);
        }
    }

    private class GetRedApoyo extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "PatientNetworkSupport";
            final String SOAP_ACTION = "http://tempuri.org/IService1/PatientNetworkSupport";
            String Error;
            dialog.show();
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
                Object redList;

                for(int i = 0; i< resultado.getPropertyCount();i++)
                {
                    redList = resultado.getProperty(i);

                    if(redList instanceof SoapObject)
                    {
                        SoapObject red = (SoapObject) redList;

                        sendSMS(red.getProperty("Telefono").toString(),red.getProperty("Nombre").toString());
                    }
                }
            }
            else
            {

            }
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
