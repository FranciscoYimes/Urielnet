package mejorandome.mejorandome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class DashboardFragment extends Fragment {

    private View rootView;
    private PieChartView pieChart;
    private PieChartData data;
    private TextView negativeMood;
    private TextView positiveMood;
    private TextView dateText;
    private Button meetingButton;
    private Button moodButton;
    private Button goalsButton;
    private Button sosButton;
    private MenuItem moodItem;
    private Menu menu;
    private int positiveEmotions;
    private int negativeEmotions;
    private int idPaciente;
    private View rootView2;
    private Intent intent;

    String meses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre","Octubre","Noviembre","Diciemrbre"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rootView2 = inflater.inflate(R.layout.activity_main, container, false);

        pieChart = (PieChartView) rootView.findViewById(R.id.pie_chart);
        negativeMood = (TextView) rootView.findViewById(R.id.negative_mood_text);
        positiveMood = (TextView) rootView.findViewById(R.id.positive_mood_text);
        dateText = (TextView) rootView.findViewById(R.id.dateText);
        moodButton = (Button) rootView.findViewById(R.id.mood_button);
        meetingButton = (Button) rootView.findViewById(R.id.citas);
        goalsButton = (Button) rootView.findViewById(R.id.goalsButton);
        sosButton = (Button) rootView.findViewById(R.id.sos_button);

        NavigationView navigationView = (NavigationView) rootView2.findViewById(R.id.nav_view);
        if(navigationView!=null)  menu = navigationView.getMenu();



        moodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menu!=null)
                {
                    menu.findItem(R.id.dashboard).setChecked(false);
                    menu.findItem(R.id.mood).setChecked(true);
                }
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,new MoodFragment()).commit();
            }
        });

        meetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,new MeetingFragment()).commit();
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,new GoalsFragment()).commit();
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSosMessage();
                intent = new Intent(getActivity(),PostSOSActivity.class);
                startActivity(intent);

            }
        });

        pieChart.setInteractive(true);

        idPaciente = getActivity().getIntent().getIntExtra("id",0);

        new getNextMeeting().execute();


        new GetEmotionsResume().execute();

        return rootView;
    }

    private void generateData() {

        List<SliceValue> values = new ArrayList<>();

        SliceValue sliceValueBuenas = new SliceValue((float) positiveEmotions, ContextCompat.getColor(rootView.getContext(),R.color.colorPrimary));
        values.add(sliceValueBuenas);

        SliceValue sliceValueMalas = new SliceValue((float) negativeEmotions, ContextCompat.getColor(rootView.getContext(),R.color.color_red));
        values.add(sliceValueMalas);

        positiveMood.setText("Positivas: "+(positiveEmotions*100)/(positiveEmotions+negativeEmotions)+"%");
        negativeMood.setText("Negativas: "+(negativeEmotions*100)/(positiveEmotions+negativeEmotions)+"%");

        data = new PieChartData(values);

        pieChart.setPieChartData(data);
    }

    private class getNextMeeting extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "nextMeeting";
            final String SOAP_ACTION = "http://tempuri.org/IService1/nextMeeting";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente); // Paso parametros al WS

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
                dateText.setText(GetDate(resultado.toString()));
            }
            else
            {
                dateText.setText("No hay citas aún");
            }

            super.onPostExecute(result);
        }
    }

    private class GetEmotionsResume extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "GetEmotionsResume";
            final String SOAP_ACTION = "http://tempuri.org/IService1/GetEmotionsResume";
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
                positiveEmotions = Integer.parseInt(resultado.getProperty(0).toString());
                negativeEmotions = Integer.parseInt(resultado.getProperty(1).toString());
            }
            else
            {
                positiveEmotions = 1;
                negativeEmotions = 1;
            }

            generateData();

            super.onPostExecute(result);
        }
    }

    public String GetDate(String date)
    {
        int i = 0;
        String fechaFinal="";
        String month="";
        String day="";
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
        fechaFinal = day + " de ";
        fechaFinal = fechaFinal+meses[Integer.parseInt(month)-1];
        fechaFinal = fechaFinal + ", "+date.charAt(i)+date.charAt(i+1)+date.charAt(i+2)+date.charAt(i+3)+date.charAt(i+4)+" "+date.charAt(i+9)+date.charAt(i+10);

        return fechaFinal;
    }

    public void GetSosMessage()
    {
        //sendSMS("+56974785845","Francisco");
        //sendSMS("+56967864621","Gabriel");
        //sendSMS("+56961590408","Pilar");
    }

    private void sendSMS(String phone, String name) {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.SEND_SMS}, 225);
        } else {
            SmsManager sms = SmsManager.getDefault();
            String message = "Estimado/a "+name+", nuestro paciente ha activado un mensaje de alerta, rogamos ponerse en contacto de inmediato.";
            sms.sendTextMessage(phone, null, message , null, null);
        }
    }
}
