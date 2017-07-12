package mejorandome.mejorandome;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
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

public class GoalsActivity extends AppCompatActivity {

    private LinearLayout goalContent;
    private LinearLayout goalLayout;
    private LinearLayout textLayout;
    private ImageView imageViewStar;
    private Toolbar mToolbar;
    private int idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goalContent = (LinearLayout) findViewById(R.id.goal_content);
        goalLayout = (LinearLayout) findViewById(R.id.goal_layout);
        textLayout = (LinearLayout) findViewById(R.id.text_layout);
        imageViewStar = (ImageView) findViewById(R.id.goal_star);

        new GetGoals().execute();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void AddGoals(SoapObject newGoal)
    {

        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        LinearLayout newTextLayout = new LinearLayout(getApplicationContext());
        ImageView newImageView = new ImageView(getApplicationContext());
        LinearLayout line = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);

        contentLayout.setLayoutParams(goalLayout.getLayoutParams());
        newTextLayout.setLayoutParams(textLayout.getLayoutParams());
        newImageView.setLayoutParams(imageViewStar.getLayoutParams());

        TextView title = new TextView(getApplicationContext());
        TextView description = new TextView(getApplicationContext());

        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        description.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        title.setTextColor(getResources().getColor(R.color.color_green));
        description.setTextColor(getResources().getColor(R.color.colorPrimary));

        if(Boolean.parseBoolean(newGoal.getProperty("Estado").toString()))
        {
            newImageView.setBackground(getResources().getDrawable(R.drawable.grey_star_icon));
        }
        else
        {
            newImageView.setBackground(getResources().getDrawable(R.drawable.star_icon));
        }

        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTextLayout.setOrientation(LinearLayout.VERTICAL);

        line.setLayoutParams(lineLayoutParams);
        line.setBackgroundColor(Color.parseColor("#B0B0B0"));

        title.setTextSize(30);
        description.setTextSize(20);

        title.setLeft(20);
        description.setLeft(20);

        if(newGoal.getProperty("Objetivo")!=null) title.setText(newGoal.getProperty("Objetivo").toString());
        description.setText("Debes bajar 4 kilos en 2 semanas");

        newTextLayout.addView(title);
        newTextLayout.addView(description);

        contentLayout.addView(newTextLayout);
        contentLayout.addView(newImageView);

        goalContent.addView(contentLayout);
        goalContent.addView(line);

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
            else
            {

            }
            super.onPostExecute(result);
        }
    }

}
