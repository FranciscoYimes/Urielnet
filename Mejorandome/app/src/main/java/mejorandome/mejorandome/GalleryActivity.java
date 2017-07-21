package mejorandome.mejorandome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.commons.net.ftp.FTPClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import mejorandome.mejorandome.Adapters.MyFTPClient;

public class GalleryActivity extends AppCompatActivity {

    private Button changeButton1;
    private Button changeButton2;
    private Button changeButton3;
    private Button changeButton4;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;

    private Image image;

    private int imageSelected;

    private Bitmap loadedImage;

    private Toolbar mToolbar;

    private static final int PICK_IMAGE = 100;

    private Context cntx = null;

    MyFTPClient ftpclient = null;
    private static final String TAG = "MyFTPClient";
    public FTPClient mFTPClient = null;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeButton1 = (Button) findViewById(R.id.change_1);
        changeButton2 = (Button) findViewById(R.id.change_2);
        changeButton3 = (Button) findViewById(R.id.change_3);
        changeButton4 = (Button) findViewById(R.id.change_4);

        image1 = (ImageView) findViewById(R.id.image_1);
        image2 = (ImageView) findViewById(R.id.image_2);
        image3 = (ImageView) findViewById(R.id.image_3);
        image4 = (ImageView) findViewById(R.id.image_4);

        ftpclient = new MyFTPClient();
        //LoadImages();
        new GetImage().execute();


        changeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 1;
                openGallery();
            }
        });

        changeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 2;
                openGallery();
            }
        });

        changeButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 3;
                openGallery();
            }
        });

        changeButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 4;
                openGallery();
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();

            if(imageSelected==1) image1.setImageURI(imageUri);
            if(imageSelected==2) image2.setImageURI(imageUri);
            if(imageSelected==3) image3.setImageURI(imageUri);
            if(imageSelected==4) image4.setImageURI(imageUri);
        }
    }

    public void LoadImages()
    {
        new Thread(new Runnable() {
            public void run(){
                boolean status = false;
                final String TEMP_FILENAME1 = "19598868_1549253395095199_5434968270116834748_n.png";
                File TEMP_PHOTO_FILE = new File(Environment.getExternalStorageDirectory(), "19598868_1549253395095199_5434968270116834748_n.png");

                Bitmap thePhoto = BitmapFactory.decodeFile(Uri.fromFile(TEMP_PHOTO_FILE).toString());

                status = ftpclient.ftpConnect("ftp.mejorandome.com", "mejorand", "cymN7W2d63", 21);

                if (status == true)
                {
                    ftpclient.ftpDownload(TEMP_FILENAME1,"ftp://ftp.mejorandome.com/mejorandome.com/Images/19598868_1549253395095199_5434968270116834748_n.png");
                    thePhoto = BitmapFactory.decodeFile(Uri.fromFile(TEMP_PHOTO_FILE).toString());
                    //removing the file from server
                    //ftpclient.ftpRemoveFile(TEMP_FILENAME1);
                    //showing the file in the ImageView
                    new Thread(new Runnable() {
                        public void run(){
                            String s = getFilesDir().toString();
                            s = getFilesDir() + "/" +TEMP_FILENAME1;
                            Bitmap bitmap = BitmapFactory.decodeFile(s);
                            bitmap = BitmapFactory.decodeFile("/mejorandome.com/Images/19598868_1549253395095199_5434968270116834748_n.png");
                            bitmap = BitmapFactory.decodeFile("/mejorand/mejorandome.com/Images/19598868_1549253395095199_5434968270116834748_n.png");
                            bitmap = BitmapFactory.decodeFile("http://mejorandome.com/Images/19598868_1549253395095199_5434968270116834748_n.png");
                            //image1.setImageBitmap(BitmapFactory.decodeFile( "mejorandome.com/Images/19598868_1549253395095199_5434968270116834748_n.png"));
                        }
                    }).start();

                } else {
                   // Toast.makeText(getApplicationContext(), "Connection failed").show();
                }
            }
        }).start();
    }

    private class GetImage extends AsyncTask<Void,Void,Void>
    {
        SoapObject resultado;
        @Override
        protected Void doInBackground(Void... params) {

            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://www.mejorandome.com/servicio/Service1.svc";
            final String METHOD_NAME = "ImagePatient";
            final String SOAP_ACTION = "http://tempuri.org/IService1/ImagePatient";
            String Error;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

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

            }
            else
            {

            }

            super.onPostExecute(result);
        }
    }
    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte= Base64.decode(image,Base64.DEFAULT);

            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
