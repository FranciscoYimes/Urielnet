package mejorandome.mejorandome;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;

import mejorandome.mejorandome.Adapters.Utils;

public class SplashActivity extends AppCompatActivity {

    private boolean active = true;
    private int splashTime = 2000;
    private ImageView splashImage;
    private AlphaAnimation appearAnimation;
    private int status = 0;
    private Utils utils;
    private int dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        utils = new Utils();

        splashImage = (ImageView) findViewById(R.id.splashImage);
        appearAnimation = new AlphaAnimation(0.0f, 1.0f);
        appearAnimation.setDuration(2000);
        appearAnimation.setFillAfter(true);
        new GetSessionStatus().execute();

        Thread splashThread = new Thread()
        {
            @Override
            public void run()
            {
                splashImage.startAnimation(appearAnimation);
                try
                {
                    int waited = 0;
                    while (active && (waited < splashTime))
                    {
                        sleep(100);
                        if (active)
                        {
                            waited += 100;
                        }

                    }
                }
                catch (InterruptedException e)
                {

                }
                finally
                {
                    do
                    {
                        if(status==2)
                        {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("id",dni);
                            startActivity(intent);
                        }
                        if(status==1)
                        {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }while (status==0);

                    finish();
                }

            }
        };
        splashThread.start();

    }

    private class GetSessionStatus extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "getSessionSatus";
            final String SOAP_ACTION = "http://tempuri.org/IService1/getSessionSatus";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("macAddress", utils.getMACAddress("wlan0")); // Paso parametros al WS

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL);

                transporte.call(SOAP_ACTION, sobre);

                resultado = (SoapPrimitive) sobre.getResponse();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                status = 1;
                Error = e.toString();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                status = 1;
                Error = soapFault.toString();

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                status = 1;
                Error = e.toString();

            } catch (IOException e) {
                e.printStackTrace();
                status = 1;
                Error = e.toString();
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(resultado != null)
            {
                String response = resultado.toString();
                dni = Integer.parseInt(response);


                if(dni==0)
                {
                    status = 1;
                }
                else
                {
                    status = 2;
                }
            }
            else status = 1;

            super.onPostExecute(result);
        }
    }
}
