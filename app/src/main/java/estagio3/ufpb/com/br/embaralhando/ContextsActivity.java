package estagio3.ufpb.com.br.embaralhando;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ContextsActivity extends AppCompatActivity{

    private ListView listView;
    private ImageButton soundbt;
    private ImageButton addContextbt;
    private DataBase dataBase;
    private static final int MAX_COUNT_CONTEXTS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contexts);
        if(BackgroundSoundService.PLAYING)
            startService(new Intent(this,BackgroundSoundService.class));
        this.dataBase = new DataBase(this);
        this.soundbt = (ImageButton) findViewById(R.id.soundButton);
        this.addContextbt = (ImageButton) findViewById(R.id.add_wordbt);
        if(!BackgroundSoundService.PLAYING)
            this.soundbt.setBackgroundResource(R.drawable.ic_volume_mute_white_24dp);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.main_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp,getTheme()));
        }else{
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContextsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listViewWords);
        listView.setAdapter(new ContextAdapter(this));
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContextAdapter contextAdapter = new ContextAdapter(ContextsActivity.this);
                Context context = (Context) contextAdapter.getItem(position);
                String nameContext = context.getName();
                Bundle bundle = new Bundle();
                bundle.putString("nameContext",nameContext);
                Intent intent = new Intent(ContextsActivity.this,WordsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        this.soundbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlMusic(v);

            }
        });
        this.addContextbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listView.getAdapter().getCount() <= MAX_COUNT_CONTEXTS) {
                    Intent adicionarContext = new Intent(ContextsActivity.this, InsertNewContextActivity.class);
                    startActivity(adicionarContext);
                    finish();
                }else{
                    Toast.makeText(ContextsActivity.this,"Memoria Cheia!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                ContextAdapter contextAdapter = new ContextAdapter(this);
                Context context = (Context) contextAdapter.getItem(info.position);
                dataBase.deleteContext(context);
                Toast.makeText(this,"Contexto Removido",Toast.LENGTH_LONG).show();
                recreate();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void controlMusic(View v) {
        if(BackgroundSoundService.PLAYING){
            this.soundbt.setBackgroundResource(R.drawable.ic_volume_mute_white_24dp);
            stopService(new Intent(ContextsActivity.this, BackgroundSoundService.class));
            BackgroundSoundService.PLAYING = false;
        }else{
            this.soundbt.setBackgroundResource(R.drawable.ic_volume_up_white_24dp);
            startService(new Intent(ContextsActivity.this, BackgroundSoundService.class));
            BackgroundSoundService.PLAYING = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.op1:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.op2:
                return true;
            case R.id.op3:
                intent = new Intent(this,ScoreActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.op4:
                finish();
                System.exit(0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.moveTaskToBack(true);
    }
}