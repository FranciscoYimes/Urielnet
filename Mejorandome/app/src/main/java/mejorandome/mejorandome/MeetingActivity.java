package mejorandome.mejorandome;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

public class MeetingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int idPaciente;
    private LinearLayout contentLayout;
    private LinearLayout linearLayoutExample;
    private LinearLayout horizontalLayoutExample;
    private TextView hourExample;
    private TextView docExample;
    private TextView ocupationExapmle;
    private ImageView calendarExample;
    private Button pastMeetingButton;
    String meses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre","Octubre","Noviembre","Diciemrbre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contentLayout = (LinearLayout) findViewById(R.id.content_layout_meeting);
        linearLayoutExample = (LinearLayout) findViewById(R.id.meeting_layout_example);
        hourExample = (TextView) findViewById(R.id.meeting_hour_example);
        docExample = (TextView) findViewById(R.id.doc_example);
        horizontalLayoutExample = (LinearLayout) findViewById(R.id.horizontal_layout_example);
        calendarExample = (ImageView) findViewById(R.id.calendar_example);
        pastMeetingButton = (Button) findViewById(R.id.past_meeting_button);
        ocupationExapmle = (TextView) findViewById(R.id.ocupation_example);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        pastMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingActivity.this,PastMeetingActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
            }
        });

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
            final String METHOD_NAME = "TotalNextMeeting";
            final String SOAP_ACTION = "http://tempuri.org/IService1/TotalNextMeeting";
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

                        AddNewMeeting(meeting);
                    }
                }
            }
            else
            {

            }
            super.onPostExecute(result);
        }
    }

    public void AddNewMeeting(SoapObject newMeeting)
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

        TextView ocupationText = new TextView(getApplicationContext());
        ocupationText.setLayoutParams(ocupationExapmle.getLayoutParams());
        ocupationText.setTextSize(19);
        ocupationText.setGravity(ocupationExapmle.getGravity());
        ocupationText.setTextColor(ocupationExapmle.getTextColors());

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
        docTextView.setText(newMeeting.getProperty("NombreTerapeuta").toString());

        line.setLayoutParams(lineLayoutParams);
        line.setBackgroundColor(Color.parseColor("#B0B0B0"));
        line.setTop(120);

        textView.setText(GetDate(newMeeting.getProperty("ProximaCita").toString()));
        ocupationText.setText("Cita con: "+newMeeting.getProperty("Ocupacion").toString());

        linearLayout.addView(ocupationText);
        linearLayout.addView(docTextView);
        linearLayout.addView(textView);
        linearLayout.addView(line);

        horizontalLayout.addView(imageView);
        horizontalLayout.addView(linearLayout);

        contentLayout.addView(horizontalLayout);
    }

    public String GetDate(String date)
    {
        int i = 0;
        String fechaFinal;
        String month="";
        String day="";
        String hora="";
        while(!"/".equals(date.charAt(i)+""))
        {
            month = month + date.charAt(i);
            i++;
        }
        i++;

        while(!"/".equals(date.charAt(i)+""))
        {
            day = day + date.charAt(i);
            i++;
        }
        i=i+6;

        while(!":".equals(date.charAt(i)+""))
        {
            hora = hora + date.charAt(i);
            i++;
        }

        fechaFinal = day + " de ";
        fechaFinal = fechaFinal+meses[Integer.parseInt(month)-1];
        fechaFinal = fechaFinal + ", "+hora+":"+date.charAt(i+1)+date.charAt(i+2)+" "+date.charAt(i+7)+date.charAt(i+8);

        return fechaFinal;
    }
}
