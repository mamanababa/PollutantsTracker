package ie.gmit.computing.pollutantstracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * clicked SEARCH button on last activity then navigate to this activity
 * to view and search the tree or save new photo.
 *
 * @author Zeyuan Li
 */
public class Activity_start extends ActionBarActivity implements View.OnClickListener {


    private ImageButton add;
    private ImageButton delete;
    private ImageButton settings;
    private RelativeLayout parent_layout;
    private LinearLayout start_lay;
    private Map<String, Node> mapNodes;
    private List<Node> listNodes;
    private TreeModel tree = new TreeModel();

    //information of photo
    private String dateInfo;
    private String gpsInfo;
    private Bitmap bitmapInfo;
    private static int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        add = (ImageButton) this.findViewById(R.id.add);
        delete = (ImageButton) this.findViewById(R.id.delete);
        settings = (ImageButton) this.findViewById(R.id.settings);
        parent_layout = (RelativeLayout) this.findViewById(R.id.layout);
        start_lay = (LinearLayout) this.findViewById(R.id.layout_start);

        parent_layout.setBackgroundColor(Color.WHITE);

        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        settings.setOnClickListener(this);

        /**
         *load the tree, the tree is empty if return null, then create the root node
         */
        if (tree.loadTree() == null) {
            Node rootNode = new CompositeNode("start", null);
            tree.saveTree(rootNode, true);
        }

        /**
         *deserialize the tree, get all nodes
         */
        listNodes = new ArrayList<Node>();
        listNodes = tree.loadTree();

        Node root = listNodes.get(0);
        Button startButton = new Button(getApplicationContext());
        startButton.setText(root.getName());
        startButton.setId(0);
        start_lay.addView(startButton);
        startButton.setOnClickListener(this);

        int size = listNodes.size();
        mapNodes = new HashMap<String, Node>();
        for (int i = 0; i < size; i++) {
            mapNodes.put(listNodes.get(i).getName(), listNodes.get(i));
        }
    }


    /**
     * on click listener of add, delete and settings buttons
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                Intent addIntent = new Intent(Activity_start.this, add.class);
                startActivity(addIntent);
                break;
            case R.id.delete:
                Intent deleteIntent = new Intent(Activity_start.this, delete.class);
                startActivity(deleteIntent);
                break;
            case R.id.settings:
                Intent settingIn = new Intent(Activity_start.this, Settings.class);
                startActivity(settingIn);
                break;
            default:
                check(v.getId());
                break;
        }
    }

    /**
     * when click button, pass its id , then get the node, check if it has children then get them,
     * then generate children buttons dynamically
     *
     * @param id
     */
    private void check(int id) {
        Button button = (Button) this.findViewById(id);
        String buttonText = button.getText().toString();
        //Log.d("clicked button: ", buttonText);
        final Node node = mapNodes.get(buttonText);
        //Log.d("clicked node: ", node.getName());

        /**
         *if just has root node without any children in this tree, have to add children
         */
        if (node.isRoot() && !node.hasChildren())
            Toast.makeText(getApplicationContext(), "please create children nodes", Toast.LENGTH_SHORT).show();

        /**
         * else if is not leaf get children , generate buttons display then on a new linear
         */
        else if (!node.isLeaf()) {
            final LinearLayout layout1 = new LinearLayout(Activity_start.this);
            layout1.setOrientation(LinearLayout.VERTICAL);
            layout1.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp1.addRule(RelativeLayout.ALIGN_TOP, R.id.layout_start);
            lp1.addRule(RelativeLayout.ABOVE, R.id.modify2);
            parent_layout.addView(layout1, lp1);

            Node[] children = node.getChildren();

            int childrenNo = children.length;
            for (int i = 0; i < childrenNo; i++) {
                Log.d("children:", children[i].getName());
            }
            Button[] childButton = new Button[childrenNo];

            for (int i = 0; i < childrenNo; i++) {
                childButton[i] = new Button(getApplicationContext());
                childButton[i].setText(children[i].getName());
                childButton[i].setId(count);
                layout1.addView(childButton[i]);
                childButton[i].setOnClickListener(this);
                count++;
            }

            Button back = new Button(getApplicationContext());
            back.setText("BACK");
            layout1.addView(back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent_layout.removeView(layout1);
                }
            });

            /**
             *else if this is leaf node, check if is has img, show the button text "NO PHOTO,BACK TO TAKE A NEW PHOTO"
             * or "Back to take a new photo"
             */
        } else {
            final RelativeLayout layoutFinal = new RelativeLayout(Activity_start.this);
            layoutFinal.setBackgroundColor(Color.WHITE);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.addRule(RelativeLayout.ABOVE, R.id.modify2);
            parent_layout.addView(layoutFinal, lp);

            Button button1 = new Button(getApplicationContext());
            button1.setText("Back to take a new photo");
            button1.setId(111);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            });
            layoutFinal.addView(button1);

            /**
             * if node doesn't has a photo, change button text
             */
            if (!node.hasImg())
                button1.setText("NO PHOTO,BACK TO TAKE A NEW PHOTO");

            /**
             *else if node has a photo, show it, then create select button
             */
            else {

                ImageView imageView = new ImageView(getApplicationContext());
                Bitmap b = Bytes2Bimap(node.getImg());
                Matrix matrix = new Matrix();
                matrix.postScale(2.5f, 2.5f);
                Bitmap bit = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                imageView.setImageBitmap(bit);
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
                lp1.addRule(RelativeLayout.BELOW, button1.getId());
                layoutFinal.addView(imageView, lp1);
            }

            /**
             * if take a new photo to save or cover the old photo,
             * then set the img of the save the leaf node and save the tree again
             */
            if (getIntent().getParcelableExtra("img") != null) {
                Button select = new Button(getApplicationContext());
                select.setId(1001);
                select.setText("SELECT");
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                layoutFinal.addView(select, lp2);

                /**
                 *
                 */
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bb = getIntent().getParcelableExtra("img");
                        //set new img for leaf node, and update the tree
                        tree.saveTree(node, false);
                        node.setImg(Bitmap2Bytes(bb));
                        tree.saveTree(node, true);
                        confirmInfo();
                    }
                });
            }

            Button back = new Button(getApplicationContext());
            back.setText("BACK");

            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_END);

            layoutFinal.addView(back, lp3);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent_layout.removeView(layoutFinal);
                }
            });
        }
    }

    /**
     * @param bm
     * @return get bitmap then convert to bytes then return it for serialize
     */
    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * @param b
     * @return after deserialzing tree, get bytes of img of a node,
     * then convert to bitmap for show it in imgView
     */
    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * set information of photo
     *
     * @param date
     * @param gps
     */
    private void setInfo(String date, String gps) {
        this.dateInfo = date;
        this.gpsInfo = gps;
    }

    /**
     * @return return information of photo
     */
    private String[] getInfo() {
        String[] s = new String[]{this.dateInfo, this.gpsInfo};
        return s;
    }

    /**
     * set bitmap of photo
     *
     * @param bitmap
     */
    private void setBitmapInfo(Bitmap bitmap) {
        this.bitmapInfo = bitmap;
    }

    /**
     * @return return bitmap of photo
     */
    private Bitmap getBitmapInfo() {
        return this.bitmapInfo;
    }

    /**
     * get the data and information of photo passed from last activity,
     * then show then in textView on a new layout,
     * then enter volume and color in editView,
     * then call save() method to store them to SD card
     */
    private void confirmInfo() {

        final LinearLayout layoutSelect = new LinearLayout(Activity_start.this);
        layoutSelect.setBackgroundColor(Color.WHITE);
        layoutSelect.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp4.addRule(RelativeLayout.ABOVE, R.id.modify2);
        parent_layout.addView(layoutSelect, lp4);

        Intent intent = getIntent();

        String date = intent.getStringExtra("date");
        String gps = intent.getStringExtra("gps");
        Bitmap img = intent.getParcelableExtra("img");

        TextView textView = new TextView(getApplicationContext());
        textView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large);
        textView.setTextColor(Color.BLACK);
        textView.setText("PHOTO INFORMATION");

        TextView dateText = new TextView(getApplicationContext());
        TextView gpsText = new TextView(getApplicationContext());
        dateText.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);
        gpsText.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);
        dateText.setTextColor(Color.BLUE);
        gpsText.setTextColor(Color.BLUE);

        dateText.setText(date);
        gpsText.setText(gps);

        final EditText volumeText = new EditText(getApplicationContext());
        final EditText colorText = new EditText(getApplicationContext());
        volumeText.setHint("enter volume");
        volumeText.setTextColor(Color.BLUE);
        colorText.setHint("enter color");
        colorText.setTextColor(Color.BLUE);

        layoutSelect.addView(textView);
        layoutSelect.addView(dateText);
        layoutSelect.addView(gpsText);
        layoutSelect.addView(volumeText);
        layoutSelect.addView(colorText);

        setInfo(date, gps);
        setBitmapInfo(img);

        Button save = new Button(getApplicationContext());
        Button back = new Button(getApplicationContext());
        save.setText("SAVE to SD card");
        back.setText("BACK");

        layoutSelect.addView(save);
        layoutSelect.addView(back);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * get state of SD card, if can use SD card, then call saveInfo to store information and photo
                 */
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String[] s = getInfo();
                    saveInfo(s[0], s[1], volumeText.getText().toString(), colorText.getText().toString(), getBitmapInfo());

                    Toast.makeText(getApplicationContext(), "SAVE DONE", Toast.LENGTH_SHORT).show();
                    Intent done = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(done);
                } else {
                    Toast.makeText(getApplicationContext(), "cannot use SD card", Toast.LENGTH_SHORT).show();
                    //  Log.d("SD card: ", "cannot use SD card");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent_layout.removeView(layoutSelect);
            }
        });

    }

    /**
     * get information and data of photo
     * then store then in SD card
     * information of photo saved as CSV file
     * photo saved as PNG file
     *
     * @param date   date of photo
     * @param gps    gps of photo
     * @param volume volume of sample
     * @param color  color of sample
     * @param bitmap photo data
     */
    private void saveInfo(String date, String gps, String volume, String color, Bitmap bitmap) {
        BufferedWriter writer = null;
        FileOutputStream fos = null;
        File sdRootDir = Environment.getExternalStorageDirectory();
        //      Log.d("save in directory:", String.valueOf(sdRootDir));
        File fileDir = new File(sdRootDir.getAbsoluteFile() + "/pollutantTracker");

        if (!fileDir.exists()) fileDir.mkdir();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String fileName = dateFormat.format(new Date());
        File file = new File(fileDir, "info_" + fileName + ".csv");
        File img = new File(fileDir, "img_" + fileName + ".png");
        try {
            if (!file.exists()) file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.append("DATE," + date);
            writer.newLine();
            writer.append("GPS INFO," + gps);
            writer.newLine();
            writer.append("VOLUME," + volume);
            writer.newLine();
            writer.append("COLOR," + color);
            writer.newLine();
            writer.append("REFERENCE PHOTO," + fileName + ".png");
            writer.flush();

            if (!img.exists()) img.createNewFile();
            fos = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_start, menu);
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