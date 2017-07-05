package mejorandome.mejorandome;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        utils = new Utils();

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,new DashboardFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new DashboardFragment()).commit();
        } else if (id == R.id.mood) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MoodFragment()).commit();
        } else if (id == R.id.settings) {

        }
        else if (id == R.id.logout) {
            new logout().execute();
        }
        else if (id == R.id.citas) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MeetingFragment()).commit();
        }
        else if (id == R.id.objetivos) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new GoalsFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class logout extends AsyncTask<Void,Void,Void>
    {
        SoapPrimitive resultado;
        @Override
        protected Void doInBackground(Void... params) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "logout";
            final String SOAP_ACTION = "http://tempuri.org/IService1/logout";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("macAddress",utils.getMACAddress("wlan0"));

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
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
