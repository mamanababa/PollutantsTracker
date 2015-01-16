package ie.gmit.computing.pollutantstracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * clicked delete node then navigate to this activity
 * show all nodes' names in a listView to select one to delete
 *
 * @author Zeyuan Li
 */
public class delete extends ActionBarActivity {

    private Button delete;
    private ListView listView;
    private ArrayAdapter<String> listViewAdapter;
    private String selectedName = null;
    private Map<String, Node> nodeMap;
    private List<Node> allNodes;
    private TreeModel tree = new TreeModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        allNodes = new ArrayList<Node>();
        allNodes = tree.loadTree();

        nodeMap = new HashMap<String, Node>();
        for (int i = 0; i < allNodes.size(); i++) {
            nodeMap.put(allNodes.get(i).getName(), allNodes.get(i));
        }

        listViewAdapter = new ArrayAdapter<String>(delete.this, android.R.layout.simple_list_item_single_choice, getNodesSource());
        listView = (ListView) this.findViewById(R.id.listView);
        listView.setAdapter(listViewAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        //get the name of selected node
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedName(listView.getItemAtPosition(position).toString());
                Log.d("selected:", getSelectedName());
            }
        });

        delete = (Button) this.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(getSelectedName());
            }
        });
    }

    /**
     * @param selectedName set selected node name
     */
    private void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    /**
     * @return return selected node name
     */
    private String getSelectedName() {
        return this.selectedName;
    }

    /**
     * @return return names of all nodes
     */
    private List<String> getNodesSource() {
        List<String> itemList = new ArrayList<String>();
        int size = allNodes.size();
        Node[] nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = allNodes.get(i);
            itemList.add(nodes[i].getName());
        }
        return itemList;
    }

    // delete method of delete button

    /**
     * @param selected get the selected name from listView , then deserialize the tree and set relationship of nodes,
     *                 then serialize it to update the tree
     */
    private void delete(String selected) {

        if (selected == null || selected.equals("")) {
            Toast.makeText(getApplicationContext(), "select one you want to delete", Toast.LENGTH_SHORT).show();
        } else if (nodeMap.get(selected).isRoot())
            Toast.makeText(getApplicationContext(), "cannot delete root node", Toast.LENGTH_SHORT).show();
        else {
            Node selectedNode = nodeMap.get(selected);
            Node parent = selectedNode.getParent();

            //if selectedNode has children, delete the selectedNode and add its children to its parent
            if (selectedNode.hasChildren()) {
                int length = selectedNode.getChildren().length;
                Node[] children = selectedNode.getChildren();
                for (int i = 0; i < length; i++) {
                    children[i].setParent(parent);
                    parent.addChild(children[i]);
                    parent.removeChild(selectedNode);
                }
            } else { //selectedNode is the leaf node, then just remove by its parent
                parent.removeChild(selectedNode);
            }
            tree.saveTree(selectedNode, false);

            //deleted succeed if cannot find the selectedNode in allNodes listF, then finish this activity
            if (!allNodes.contains(selectedNode)) {
                Log.d("------", "delete done");
                Toast.makeText(getApplicationContext(), "delete DONE", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            } else {
                Log.d("!!!!!", "delete error,selected: " + nodeMap.get(selected).getName());
                Toast.makeText(getApplicationContext(), "delete ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
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
