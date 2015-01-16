package ie.gmit.computing.pollutantstracker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeyuan Li on 15/1/6.
 * add all nodes object together in a List as a whole object ,
 * then serialize to tree.ser file into SD card
 *
 * @author Zeyuan Li
 */
public class TreeModel {

    static List<Node> list = new ArrayList<Node>();

    /**
     * @param node  get node then add to list then serialize to tree.ser into SD card
     * @param isAdd a flag of add node or remove node for update the tree
     */
    public void saveTree(Node node, boolean isAdd) {
        //   Log.d("SAVE TREE", "size:" + String.valueOf(list.size()));
        if (isAdd)
            list.add(node);
        else
            list.remove(node);
        //Log.d("SAVE TREE", "size:" + String.valueOf(list.size()));

        ObjectOutputStream objOut = null;
        String sdState = Environment.getExternalStorageState();
        File sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            //       Log.d("dir: ", String.valueOf(sdDir));
            File fileDir = new File(sdDir + "/pollutantTracker");
            if (!fileDir.exists()) fileDir.mkdir();
            File file = new File(fileDir, "tree.ser");
            try {
                if (!file.exists()) file.createNewFile();
                objOut = new ObjectOutputStream(new FileOutputStream(file));
                objOut.writeObject(list);
                objOut.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (objOut != null) {
                    try {
                        objOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        else
//            Log.d("SD card: ", "不可用，存Node失败");
    }


    /**
     * @return serialze tree.ser file from SD card, return nodes List
     */
    public List<Node> loadTree() {
        ObjectInputStream objIn = null;
        String sdState = Environment.getExternalStorageState();
        File sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();

        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            //    Log.d("SD card: ", "sd可用,准备取tree");
            File fileDir = new File(sdDir + "/pollutantTracker");
            File file = new File(fileDir, "tree.ser");
            try {
                if (file.exists()) {
                    objIn = new ObjectInputStream(new FileInputStream(file));
                    list = (List<Node>) objIn.readObject();
                    // Log.d("读tree", "tree的node个数" + String.valueOf(list.size()));
                } else
                    return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (objIn != null) {
                    try {
                        objIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        else
//            Log.d("SD card: ", "sd不可用，不能取");
        return list;
    }
}