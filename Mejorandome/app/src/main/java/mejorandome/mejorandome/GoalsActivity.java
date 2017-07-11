package mejorandome.mejorandome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoalsActivity extends AppCompatActivity {

    private LinearLayout goalContent;
    private LinearLayout goalLayout;
    private LinearLayout textLayout;
    private ImageView imageViewStar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goalContent = (LinearLayout) findViewById(R.id.goal_content);
        goalLayout = (LinearLayout) findViewById(R.id.goal_layout);
        textLayout = (LinearLayout) findViewById(R.id.text_layout);
        imageViewStar = (ImageView) findViewById(R.id.goal_star);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AddGoals(0);
        AddGoals(0);
        AddGoals(1);
    }

    public void AddGoals(int grey)
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

        if(grey==1)
            newImageView.setBackground(getResources().getDrawable(R.drawable.grey_star_icon));
        else
            newImageView.setBackground(getResources().getDrawable(R.drawable.star_icon));

        newImageView.setBackground(getResources().getDrawable(R.drawable.star_icon));

        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTextLayout.setOrientation(LinearLayout.VERTICAL);

        line.setLayoutParams(lineLayoutParams);
        line.setBackgroundColor(Color.parseColor("#B0B0B0"));

        title.setTextSize(30);
        description.setTextSize(20);

        title.setLeft(20);
        description.setLeft(20);

        title.setText("Bajar de Peso");
        description.setText("Debes bajar 4 kilos en 2 semanas");

        newTextLayout.addView(title);
        newTextLayout.addView(description);

        contentLayout.addView(newTextLayout);
        contentLayout.addView(newImageView);

        goalContent.addView(contentLayout);
        goalContent.addView(line);

    }
}
