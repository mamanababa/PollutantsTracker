package ie.gmit.computing.pollutantstracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * clicked add button the navigate to this activity,
 * enter name of new node, then select parent and child in a spinner,
 * then click ok to insert node or add a new leaf node
 *
 * @author Zeyuan Li
 */
public class add extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private Spinner parentSpinner;
    private Spinner childrenSpinner;
    private EditText editText;
    private Button ok;

    private ArrayAdapter<String> adapterP;
    private ArrayAdapter<String> adapterC;

    private String parentName = null;
    private String childName = null;

    private Map<String, Node> nodeMap;
    private List<Node> allNodes;
    private TreeModel tree = new TreeModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        /**
         *deserialize the tree, get all nodes
         */
        allNodes = new ArrayList<Node>();
        allNodes = tree.loadTree();
//        Log.d("add", "nodes size" + allNodes.size());

        int size = allNodes.size();
        nodeMap = new HashMap<String, Node>();
        for (int i = 0; i < size; i++) {
            nodeMap.put(allNodes.get(i).getName(), allNodes.get(i));
        }

        parentSpinner = (Spinner) this.findViewById(R.id.parent);
        childrenSpinner = (Spinner) this.findViewById(R.id.children);
        editText = (EditText) this.findViewById(R.id.editText);
        ok = (Button) this.findViewById(R.id.ok);

        adapterP = new ArrayAdapter<String>(add.this, android.R.layout.simple_spinner_item, setParentNodes());
        parentSpinner.setAdapter(adapterP);
        parentSpinner.setOnItemSelectedListener(this);

        /**
         *  call addNewNode() method to add new node
         */
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNode(getParentName(), getChildName());
            }
        });

    }

    /**
     * @return return names of all nodes in the tree for showing them in parent node spinner
     */
    private List<String> setParentNodes() {
        List<String> parentNodesList = new ArrayList<String>();
        //获取所有有子节点的父节点
        for (int i = 0; i < allNodes.size(); i++) {
            //把该节点获取名字添加到list并显示到parent下拉框
            parentNodesList.add(allNodes.get(i).getName());
        }
        return parentNodesList;
    }

    /**
     * set the selected parent node name in the parent node spinner
     *
     * @param parentName
     */
    private void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return return the selected parent node name in the parent node spinner
     */
    private String getParentName() {
        return parentName;
    }

    /**
     * set the selected child node name in the children node spinner
     *
     * @param childName
     */
    private void setChildName(String childName) {
        this.childName = childName;
    }

    /**
     * @return return the selected child node name in the children node spinner
     */
    private String getChildName() {
        return childName;
    }


    /**
     * set parent nodes names show them in the parent node spinner
     * <p/>
     * get the selected parent name,
     * then call setChildrenNodes() method,
     * to set children names show them in the children node spinner
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parentName = parentSpinner.getItemAtPosition(position).toString();
        setParentName(parentName);
        List<String> l = setChildrenNodes(getParentName());
        adapterC = new ArrayAdapter<String>(add.this, android.R.layout.simple_spinner_item, l);
        childrenSpinner.setAdapter(adapterC);
        childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                childName = childrenSpinner.getItemAtPosition(position).toString();
                setChildName(childName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * get the selected parent name,
     * then return children names show them in the children node spinner
     *
     * @param nameP
     * @return
     */
    private List<String> setChildrenNodes(String nameP) {
        List<String> childrenNodesList = new ArrayList<String>();

        Node nodeP = nodeMap.get(nameP);
        Node[] nodesC = nodeP.getChildren();
        //获取选中的父节点的所有子节点，并显示到下拉框
        for (int i = 0; i < nodesC.length; i++) {
            childrenNodesList.add(nodesC[i].getName());
        }
        childrenNodesList.add("NONE");
        return childrenNodesList;
    }

    /**
     * invoked in onClick listener of ok button
     * get selected parent node name and child node name
     * then set the relationship of nodes
     * then call serialize methods to update the tree
     *
     * @param parent
     * @param child
     */
    private void addNewNode(String parent, String child) {
        String newName = editText.getText().toString();
        //if editText is empty, then setError
        if (newName == null || newName.equals("")) {
            editText.setError("cannot empty");
            return;
        } else {
            Log.d("selected parent:", parent);
            Log.d("selected child:", child);
            Node parentNode = nodeMap.get(parent);

            //compare the name of new node and all nodes
            String newNameLower = newName.trim().toLowerCase();
            int size = allNodes.size();
            String[] names = new String[size];
            boolean isSame = true;
            for (int i = 0; i < size; i++) {
                names[i] = allNodes.get(i).getName().trim().toLowerCase();
                if (newNameLower.equals(names[i])) {
                    isSame = true;
                    break;
                } else {
                    isSame = false;
                }
            }
            Log.d("isSame: ", String.valueOf(isSame));

            if (isSame) {
                editText.setError("existed");
                return;
            } else {
                Node newNode = new CompositeNode(newName, parentNode);

                //if just add a leaf node ,not insert, then just set parent
                if (child == "NONE" || child.equals("NONE")) {
                    newNode.setParent(parentNode);
                    parentNode.addChild(newNode);
                    parentNode.setImg(null);
                    Log.d("new node is leaf", newNode.isLeaf() + "");
                } else {
                    Node childNode = nodeMap.get(child);
                    parentNode.insertChild(newNode, childNode);
                    Log.d("insert node:", newName);
                }

                //call serialize method to save new node
                tree.saveTree(newNode, true);
                Log.d("加了后的文件node数:", String.valueOf(tree.loadTree().size()));

                // add succeed if all nodes list contains the new node then finish this activity
                if (allNodes.contains(newNode)) {
                    Toast.makeText(getApplicationContext(), "add succeed", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else
                    Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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