package mejorandome.mejorandome;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import mejorandome.mejorandome.Adapters.SimpleProgressDialog;

public class RedApoyoActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private LinearLayout contentLayout;
    private TextView nameExample;
    private TextView numberExample;
    private ImageView imageExample;
    private LinearLayout horizontalLayoutExample;
    private LinearLayout redLayoutExample;
    private Toolbar mToolbar;
    private int idPaciente;
    private int idRedApoyo;
    private boolean response;
    private SimpleProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_apoyo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPaciente = getIntent().getIntExtra("idPaciente",0);
        dialog = SimpleProgressDialog.build(this, "Cargando...");

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contentLayout = (LinearLayout) findViewById(R.id.red_content);
        nameExample = (TextView) findViewById(R.id.nombre_red);
        numberExample = (TextView) findViewById(R.id.numero_red);
        imageExample = (ImageView) findViewById(R.id.delete_image);
        horizontalLayoutExample = (LinearLayout) findViewById(R.id.horizontal_layout_example);
        redLayoutExample = (LinearLayout) findViewById(R.id.red_layout_example);

        new GetRedApoyo().execute();

        fab = (FloatingActionButton) findViewById(R.id.add_red);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RedApoyoActivity.this, NewRedApoyoActivity.class);
                intent.putExtra("idPaciente",idPaciente);
                startActivity(intent);
                finish();
            }
        });
    }

    public void AddRedApoyo(final SoapObject redApoyo)
    {
        LinearLayout horizontalLayout = new LinearLayout(getApplicationContext());
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout line = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(imageExample.getLayoutParams());
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);
        lineLayoutParams.topMargin = 15;
        imageLayoutParams.gravity = Gravity.CENTER_VERTICAL;

        horizontalLayout.setLayoutParams(horizontalLayoutExample.getLayoutParams());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        imageView.setLayoutParams(imageLayoutParams);
        imageView.setBackground(getResources().getDrawable(R.drawable.delete_icon));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idRedApoyo = Integer.parseInt(redApoyo.getProperty("IdRedApoyo").toString());
                AlertMessage().show();
            }
        });

        linearLayout.setLayoutParams(redLayoutExample.getLayoutParams());
        linearLayout.setTop(redLayoutExample.getTop());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(numberExample.getLayoutParams());
        textView.setTextSize(22);
        textView.setGravity(numberExample.getGravity());
        textView.setTextColor(numberExample.getTextColors());

        TextView docTextView = new TextView(getApplicationContext());
        docTextView.setLayoutParams(nameExample.getLayoutParams());
        docTextView.setTextSize(22);
        docTextView.setTop(nameExample.getTop());
        docTextView.setGravity(nameExample.getGravity());
        docTextView.setTextColor(nameExample.getTextColors());

        textView.setText(redApoyo.getProperty("Telefono").toString());
        docTextView.setText(redApoyo.getProperty("Nombre").toString());

        line.setLayoutParams(lineLayoutParams);
        line.setBackgroundColor(Color.parseColor("#B0B0B0"));
        line.setTop(120);

        linearLayout.addView(docTextView);
        linearLayout.addView(textView);
        linearLayout.addView(line);

        horizontalLayout.addView(imageView);
        horizontalLayout.addView(linearLayout);

        contentLayout.addView(horizontalLayout);
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

                        AddRedApoyo(red);
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

    public AlertDialog AlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RedApoyoActivity.this);

        builder.setTitle("Eliminar Red de Apoyo")
                .setMessage("Se eliminará el registro de la Red de Apoyo")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteRedApoyo().execute();
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

    private class DeleteRedApoyo extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "DeleteNetworkSupport";
            final String SOAP_ACTION = "http://tempuri.org/IService1/DeleteNetworkSupport";
            String Error;
            dialog.show();
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPaciente", idPaciente); // Paso parametros al WS
                request.addProperty("idRedApoyo", idRedApoyo); // Paso parametros al WS

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
                Toast toast = Toast.makeText(RedApoyoActivity.this, "El registro ha sido eliminado", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(RedApoyoActivity.this, "El registro no ha logrado ser eliminado.", Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(result);
        }
    }
}
