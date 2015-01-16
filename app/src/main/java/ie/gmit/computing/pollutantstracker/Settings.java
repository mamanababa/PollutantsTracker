package ie.gmit.computing.pollutantstracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Settings activity to save generic information such as
 * ship name, scientist name and email to the internal storage
 *
 * @author Zeyuan Li
 */
public class Settings extends ActionBarActivity {

    private Button change;
    private EditText shipName;
    private EditText name;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        shipName = (EditText) this.findViewById(R.id.shipname);
        name = (EditText) this.findViewById(R.id.name);
        email = (EditText) this.findViewById(R.id.email);
        change = (Button) this.findViewById(R.id.change);

        String[] s = read();
        if (s == null) {
            shipName.setHint("none, please enter");
            name.setHint("none, please enter");
            email.setHint("none, please enter");
        } else {

            shipName.setText(s[0]);
            name.setText(s[1]);
            email.setText(s[2]);
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(shipName.getText().toString(), name.getText().toString(), email.getText().toString());
                Toast.makeText(getApplicationContext(), "CHANGE SUCCEED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param shipname
     * @param name
     * @param email
     */
    private void change(String shipname, String name, String email) {
        BufferedWriter b = null;
        try {
            b = new BufferedWriter(new OutputStreamWriter(getApplicationContext().openFileOutput("genericInfo.txt", MODE_MULTI_PROCESS)));
            b.write(shipname);
            b.newLine();
            b.write(name);
            b.newLine();
            b.write(email);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return
     */
    private String[] read() {
        BufferedReader bb = null;
        String[] s = new String[3];
        try {
            if (getApplicationContext().openFileInput("genericInfo.txt") == null) return null;
            else {
                bb = new BufferedReader(new InputStreamReader(getApplicationContext().openFileInput("genericInfo.txt")));
                //   String[] s = new String[3];
                s[0] = bb.readLine();
                s[1] = bb.readLine();
                s[2] = bb.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bb != null) {
                try {
                    bb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return s;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
