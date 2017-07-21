package mejorandome.mejorandome;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class PastMeetingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int idPaciente;
    private LinearLayout contentLayout;
    private LinearLayout linearLayoutExample;
    private LinearLayout horizontalLayoutExample;
    private TextView hourExample;
    private TextView docExample;
    private TextView asistenciaExample;
    private ImageView calendarExample;
    private SimpleProgressDialog dialog;
    String meses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre","Octubre","Noviembre","Diciemrbre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = SimpleProgressDialog.build(this, "Cargando...");

        contentLayout = (LinearLayout) findViewById(R.id.content_layout_meeting);
        linearLayoutExample = (LinearLayout) findViewById(R.id.meeting_layout_example);
        hourExample = (TextView) findViewById(R.id.meeting_hour_example);
        docExample = (TextView) findViewById(R.id.doc_example);
        asistenciaExample = (TextView) findViewById(R.id.asistencia_example);
        horizontalLayoutExample = (LinearLayout) findViewById(R.id.horizontal_layout_example);
        calendarExample = (ImageView) findViewById(R.id.calendar_example);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new TotalNextMeeting().execute();
    }

    private class TotalNextMeeting extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "TotalPastMeeting";
            final String SOAP_ACTION = "http://tempuri.org/IService1/TotalPastMeeting";
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
                Object meetingList;

                for(int i = 0; i< resultado.getPropertyCount();i++)
                {
                    meetingList = resultado.getProperty(i);

                    if(meetingList instanceof SoapObject)
                    {
                        SoapObject meeting = (SoapObject) meetingList;
                        SoapObject meetingData = (SoapObject) meeting.getProperty(0);
                        SoapObject docData = (SoapObject) meeting.getProperty(1);

                        AddNewMeeting(meetingData,docData);
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

    public void AddNewMeeting(SoapObject newMeeting,SoapObject docInfo)
    {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        LinearLayout horizontalLayout = new LinearLayout(getApplicationContext());
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout line = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(calendarExample.getLayoutParams());
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);
        lineLayoutParams.topMargin = 15;
        imageLayoutParams.gravity = Gravity.CENTER_VERTICAL;

        horizontalLayout.setLayoutParams(horizontalLayoutExample.getLayoutParams());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        imageView.setLayoutParams(imageLayoutParams);
        imageView.setBackground(getResources().getDrawable(R.drawable.calendar_color_icon));


        linearLayout.setLayoutParams(linearLayoutExample.getLayoutParams());
        linearLayout.setTop(linearLayoutExample.getTop());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(hourExample.getLayoutParams());
        textView.setTextSize(22);
        textView.setGravity(hourExample.getGravity());
        textView.setTextColor(hourExample.getTextColors());

        TextView docTextView = new TextView(getApplicationContext());
        docTextView.setLayoutParams(docExample.getLayoutParams());
        docTextView.setTextSize(20);
        docTextView.setTop(docExample.getTop());
        docTextView.setGravity(docExample.getGravity());
        docTextView.setTextColor(docExample.getTextColors());
        docTextView.setText(docInfo.getProperty("NombreTerapeuta").toString());

        TextView observacionTextView = new TextView(getApplicationContext());
        observacionTextView.setLayoutParams(asistenciaExample.getLayoutParams());
        observacionTextView.setTextSize(17);
        observacionTextView.setGravity(asistenciaExample.getGravity());
        observacionTextView.setTextColor(asistenciaExample.getTextColors());

        if(newMeeting.getProperty("Observacion")!=null && newMeeting.getProperty("Observacion")!="anyType{}")
            observacionTextView.setText("Observacion: "+newMeeting.getProperty("Observacion").toString());
        else
            observacionTextView.setText("Observacion: Sin Observacion");

        TextView asistenciaTextView = new TextView(getApplicationContext());
        asistenciaTextView.setLayoutParams(asistenciaExample.getLayoutParams());
        asistenciaTextView.setTextSize(17);
        asistenciaTextView.setGravity(asistenciaExample.getGravity());
        asistenciaTextView.setTextColor(asistenciaExample.getTextColors());
        asistenciaTextView.setText("Asistencia: "+GetAsistencia(newMeeting.getProperty("Asistencia")));

        line.setLayoutParams(lineLayoutParams);
        line.setBackgroundColor(Color.parseColor("#B0B0B0"));
        line.setTop(120);

        textView.setText(GetDate(newMeeting.getProperty("FechaCita").toString()));

        linearLayout.addView(docTextView);
        linearLayout.addView(textView);
        linearLayout.addView(observacionTextView);
        linearLayout.addView(asistenciaTextView);
        linearLayout.addView(line);

        horizontalLayout.addView(imageView);
        horizontalLayout.addView(linearLayout);

        contentLayout.addView(horizontalLayout);
    }

    public String GetDate(String date)
    {
        int i = 5;
        String fechaFinal;
        String month="";
        String day="";
        String hora="";
        while(!"-".equals(date.charAt(i)+""))
        {
            month = month + date.charAt(i);
            i++;
        }

        i++;

        while(!"T".equals(date.charAt(i)+""))
        {
            day = day + date.charAt(i);
            i++;
        }
        i=i+1;

        while(!":".equals(date.charAt(i)+""))
        {
            hora = hora + date.charAt(i);
            i++;
        }

        fechaFinal = day + " de ";
        fechaFinal = fechaFinal+meses[Integer.parseInt(month)-1];
        fechaFinal = fechaFinal + ", "+hora+":"+date.charAt(i+1)+date.charAt(i+2)+" hrs";

        return fechaFinal;
    }

    public String GetAsistencia(Object asistencia)
    {
        if(asistencia==null)
            return "No";
        else
            if(Boolean.parseBoolean(asistencia.toString())) return "Si";
            else
                return "No";
    }

}
