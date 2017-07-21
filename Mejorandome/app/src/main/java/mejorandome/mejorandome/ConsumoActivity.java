package mejorandome.mejorandome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;

public class ConsumoActivity extends AppCompatActivity {

    private int idPaciente;
    private Toolbar mToolbar;
    private LinearLayout contentConsumo;
    private LinearLayout consumoLayout;
    private LinearLayout textLayout;
    private ImageView imageView;
    private TextView titleExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idPaciente = getIntent().getIntExtra("idPaciente",0);

        consumoLayout = (LinearLayout) findViewById(R.id.consumo_layout);
        contentConsumo = (LinearLayout) findViewById(R.id.consumo_content);
        imageView = (ImageView) findViewById(R.id.consumo_image);
        titleExample = (TextView) findViewById(R.id.title_consumo);
        textLayout = (LinearLayout) findViewById(R.id.text_layout);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddConsumo2();
        AddConsumo2();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsumoActivity.this,NewConsumoActivity.class);
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
    }

    public void AddConsumo(SoapObject newConsumo)
    {

        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        LinearLayout newTextLayout = new LinearLayout(getApplicationContext());
        ImageView newImageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);

        contentLayout.setLayoutParams(consumoLayout.getLayoutParams());
        newTextLayout.setLayoutParams(textLayout.getLayoutParams());
        newImageView.setLayoutParams(imageView.getLayoutParams());

        contentLayout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(getApplicationContext());
        TextView description = new TextView(getApplicationContext());

        title.setLayoutParams(titleExample.getLayoutParams());
        description.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        title.setTextColor(Color.BLACK);
        description.setTextColor(Color.parseColor("#757b80"));

        newImageView.setBackground(getResources().getDrawable(R.drawable.medicine_color_icon));

        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTextLayout.setOrientation(LinearLayout.VERTICAL);

        title.setTextSize(20);
        description.setTextSize(15);

        //title.setLeft(20);
        description.setLeft(20);

        if(newConsumo.getProperty("TipoConsumo")!=null) title.setText(newConsumo.getProperty("TipoConsumo").toString());
        if(newConsumo.getProperty("Cantidad")!=null && newConsumo.getProperty("FechaCreacion")!=null) description.setText(newConsumo.getProperty("Cantidad").toString()+GetTipo(newConsumo.getProperty("TipoConsumo").toString())+GetDate(newConsumo.getProperty("FechaCreacion").toString()));

        newTextLayout.addView(title);
        newTextLayout.addView(description);

        contentLayout.addView(newImageView);
        contentLayout.addView(newTextLayout);

        contentConsumo.addView(contentLayout);

    }
    public void AddConsumo2()
    {

        LinearLayout contentLayout = new LinearLayout(getApplicationContext());
        LinearLayout newTextLayout = new LinearLayout(getApplicationContext());
        ImageView newImageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);

        contentLayout.setLayoutParams(consumoLayout.getLayoutParams());
        newTextLayout.setLayoutParams(textLayout.getLayoutParams());
        newImageView.setLayoutParams(imageView.getLayoutParams());

        contentLayout.setBackgroundColor(Color.WHITE);

        newImageView.setBackground(getResources().getDrawable(R.drawable.medicine_color_icon));

        TextView title = new TextView(getApplicationContext());
        TextView description = new TextView(getApplicationContext());

        title.setLayoutParams(titleExample.getLayoutParams());
        description.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        title.setTextColor(Color.BLACK);
        description.setTextColor(Color.parseColor("#757b80"));

        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTextLayout.setOrientation(LinearLayout.VERTICAL);

        title.setTextSize(20);
        description.setTextSize(15);

        //title.setLeft(20);
        description.setLeft(20);

        title.setText("Coca");
        description.setText("12 grs. 12 de Agosto 2017");

        newTextLayout.addView(title);
        newTextLayout.addView(description);

        contentLayout.addView(newImageView);
        contentLayout.addView(newTextLayout);

        contentConsumo.addView(contentLayout);

    }

    public String GetDate(String date)
    {
        int i = 0;
        String fechaFinal;
        String month="";
        String day="";
        String hora="";
        String meses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre","Octubre","Noviembre","Diciemrbre"};
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

    public String GetTipo(String consumo)
    {
        switch (consumo)
        {
            case "Cocaina":
                return "grs";
            case "Heroina":
                return "grs";
            case "Alcohol":
                return "lts";
        }
        return "";
    }

}
