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

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class GoalsActivity extends AppCompatActivity {

    private LinearLayout goalContent;
    private LinearLayout goalLayout;
    private LinearLayout textLayout;
    private ImageView imageViewStar;
    private TextView titleExample;
    private TextView descExample;
    private Toolbar mToolbar;
    private int idPaciente;
    private SimpleProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        dialog = SimpleProgressDialog.build(this, "Cargando...");

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goalContent = (LinearLayout) findViewById(R.id.goal_content);
        goalLayout = (LinearLayout) findViewById(R.id.goal_layout);
        textLayout = (LinearLayout) findViewById(R.id.text_layout);
        imageViewStar = (ImageView) findViewById(R.id.goal_star);
        titleExample = (TextView) findViewById(R.id.title_goal);
        descExample = (TextView) findViewById(R.id.description_goal);

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
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);

        contentLayout.setLayoutParams(goalLayout.getLayoutParams());
        newTextLayout.setLayoutParams(textLayout.getLayoutParams());
        newImageView.setLayoutParams(imageViewStar.getLayoutParams());

        contentLayout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(getApplicationContext());
        TextView description = new TextView(getApplicationContext());

        title.setLayoutParams(titleExample.getLayoutParams());
        description.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        title.setTextColor(Color.BLACK);
        description.setTextColor(Color.parseColor("#757b80"));

        if(!Boolean.parseBoolean(newGoal.getProperty("Estado").toString()))
        {
            newImageView.setBackground(getResources().getDrawable(R.drawable.goal_icon_1));
        }
        else
        {
            newImageView.setBackground(getResources().getDrawable(R.drawable.goal_icon_0));
        }

        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTextLayout.setOrientation(LinearLayout.VERTICAL);

        title.setTextSize(20);
        description.setTextSize(15);

        //title.setLeft(20);
        description.setLeft(20);

        if(newGoal.getProperty("Objetivo")!=null) title.setText(newGoal.getProperty("Objetivo").toString());
        if(newGoal.getProperty("Descripcion")!=null) description.setText(newGoal.getProperty("Descripcion").toString());

        newTextLayout.addView(title);
        newTextLayout.addView(description);

        contentLayout.addView(newImageView);
        contentLayout.addView(newTextLayout);

        goalContent.addView(contentLayout);

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
                        AddGoals(meeting);
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
