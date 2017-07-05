package mejorandome.mejorandome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

public class PostSOSActivity extends AppCompatActivity {

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private int dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sos);

        dni = getIntent().getIntExtra("dni",0);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);

        Ion.with(image1).load("http://calgaryclosertohome.com/wp-content/uploads/Happy-Family.jpg");
        Ion.with(image2).load("http://www.fcs-midland.org/images/gui/family%20of%20four.jpg");
        Ion.with(image3).load("https://www.tripleoklaw.com/wp-content/uploads/2015/11/Family-Law-Image-800x600.jpg");
        Ion.with(image4).load("http://www.sbmegastudy.com/wp-content/uploads/2016/03/family-trip.jpg");
    }
}
