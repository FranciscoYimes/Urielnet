package mejorandome.mejorandome;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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
import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class DashboardFragment extends Fragment {

    private View rootView;
    private PieChartView pieChart;
    private PieChartData data;
    private TextView negativeMood;
    private TextView positiveMood;
    private TextView dateText;
    private TextView logros;
    private TextView inasistencias;
    private Button meetingButton;
    private Button moodButton;
    private Button goalsButton;
    private Button sosButton;
    private Button inasistenciasButton;
    private int positiveEmotions;
    private int negativeEmotions;
    private int idPaciente;
    private boolean privacidadAlta;
    private Intent intent;
    private SimpleProgressDialog dialog;

    String meses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre","Octubre","Noviembre","Diciemrbre"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pieChart = (PieChartView) rootView.findViewById(R.id.pie_chart);
        negativeMood = (TextView) rootView.findViewById(R.id.negative_mood_text);
        positiveMood = (TextView) rootView.findViewById(R.id.positive_mood_text);
        logros = (TextView) rootView.findViewById(R.id.logros);
        inasistencias = (TextView) rootView.findViewById(R.id.inasistencias);
        dateText = (TextView) rootView.findViewById(R.id.dateText);
        moodButton = (Button) rootView.findViewById(R.id.mood_button);
        meetingButton = (Button) rootView.findViewById(R.id.citas);
        goalsButton = (Button) rootView.findViewById(R.id.goalsButton);
        sosButton = (Button) rootView.findViewById(R.id.sos_button);
        inasistenciasButton = (Button) rootView.findViewById(R.id.inasistencias_button);

        idPaciente = getActivity().getIntent().getIntExtra("id",0);

        pieChart.setInteractive(true);

        new getNextMeeting().execute();

        new GetEmotionsResume().execute();

        new getUserData().execute();

        new getMoodStatus().execute();

        moodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MoodActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
            }
        });

        meetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MeetingActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GoalsActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertMessage().show();
            }
        });

        inasistenciasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PastMeetingActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
            }
        });

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

    private class getMoodStatus extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "getMoodStatus";
            final String SOAP_ACTION = "http://tempuri.org/IService1/getMoodStatus";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente);

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
                if(Boolean.parseBoolean(resultado.toString()))
                {
                    moodButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    moodButton.setVisibility(View.GONE);
                }
            }
            else
            {
                moodButton.setVisibility(View.GONE);
            }

            super.onPostExecute(result);
        }
    }

    private class getUserData extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        SoapPrimitive logrosRes;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "ObjectPaciente";
            final String SOAP_ACTION = "http://tempuri.org/IService1/ObjectPaciente";
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


                resultado = (SoapObject) resultado.getProperty(0);
                logrosRes = (SoapPrimitive) resultado.getProperty(1);
                resultado = (SoapObject) resultado.getProperty(0);

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
                logros.setText(logrosRes.toString());
                 if(resultado.getProperty("InasistenciasSeguidas")!=null) inasistencias.setText(resultado.getProperty("InasistenciasSeguidas").toString());
            }
            else
            {
                logros.setText("Error");
                inasistencias.setText("Error");
            }

            super.onPostExecute(result);
        }
    }
    public AlertDialog AlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("SOS")
                .setMessage("Se enviará un mensaje de alerta a tu Red de Apoyo")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(getActivity(),PostSOSActivity.class);
                                intent.putExtra("idPaciente",idPaciente);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }
}
