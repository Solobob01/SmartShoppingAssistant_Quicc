package com.example.myapplication.ui.shoppingList;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.customAdapter.CustomAdapter;
import com.example.myapplication.databinding.FragmentShoppingListBinding;
import com.example.myapplication.graph.Graph;
import com.example.myapplication.graph.Node;
import com.example.myapplication.product.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ShoppingListFragment extends Fragment {

    private FragmentShoppingListBinding binding;
    AutoCompleteTextView input;
    ArrayList<String> items,recommended_items,items_db;
    ArrayList<Product> all_items_db;
    CustomAdapter adapter,adapter2, inputAdapter;

    Button btn2;
    ListView listView,listView2;
    ImageView enter;
    Graph graph;
    JSONArray jArray;
    ArrayList<Product> prodList;
    ShoppingListViewModel vm;
    public static volatile ArrayList<Node> path = new ArrayList<>();

    @Override
    public void onSaveInstanceState(@NonNull Bundle state) {
        System.out.println("Am salvat date!");

        state.putStringArrayList("items",items);
        state.putStringArrayList("recommended_items",recommended_items);
        super.onSaveInstanceState(state);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(ShoppingListViewModel.class);

        binding = FragmentShoppingListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        items_db = new ArrayList<>();
        makeGetReqItemList();
        Collections.sort(items_db);

        listView = binding.listview;
        listView2 = binding.listview2;
        input = binding.input;
        enter = binding.add;

        input.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,items_db));
        input.setThreshold(1);

        System.out.println("SAVEDINSTANCE: " + savedInstanceState);
        try {
            graph = Graph.createGraph(getContext().getAssets(), "graphData.in");
            getAllItemsAndIntegrate();
        } catch (IOException e) {
            e.printStackTrace();
        }

        items = vm.getItemsList().getValue();
        recommended_items = vm.getRecommandationList().getValue();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = items.get(i);
                makeToast("Removed: " + name);
                removeItem(name);
                updatePath();
                return false;
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = recommended_items.get(i);
                removeItem2(name);
                addItem(name);
                updatePath();
                makeToast("Added: " + name);
            }
        });

        adapter = new CustomAdapter(items);
        listView.setAdapter(adapter);

        adapter2 = new CustomAdapter(recommended_items);
        listView2.setAdapter(adapter2);

        enter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                String text = input.getText().toString();
                if (text.length() == 0)
                    makeToast("Enter an item.");
                else if (items.contains(text))
                    makeToast("Item already added!");
                else if(recommended_items.contains(text))
                {
                    addItem(text);
                    input.setText("");
                    makeToast("Added: " + text);
                    updatePath();
                    recommended_items.remove(text);
                }
                else if (!items_db.contains(text)){
                    makeToast("Item is not available!");
                }
                else
                {
                    addItem(text);
                    input.setText("");
                    makeToast("Added: " + text);

                    //Update path
                    updatePath();
//                    for(Node n: path){
//                        System.out.println(String.valueOf(n.getIndex()) + " X:" + String.valueOf(n.getX()) + " Y:" + String.valueOf(n.getY()));
//                    }

                    makeGetReqRecommendations();
                    adapter2.notifyDataSetChanged();
                }
            }
        });

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updatePath(){
        prodList = new ArrayList<>();
        for(String itemName : items) {
            //Search it through the JSONarray
            for(int i=0;i<jArray.length();i++) {
                JSONObject obj = null;
                try {
                    obj = jArray.getJSONObject(i);
                    String objName = obj.getString("name");
                    if(objName.equals(itemName)){
                        Product product = new Product(
                                objName,
                                Integer.parseInt(obj.getString("from")),
                                Integer.parseInt(obj.getString("to")),
                                Float.parseFloat(obj.getString("percent")));
                        prodList.add(product);
                    }
                } catch (JSONException e) {
                    System.out.println("SAD: ERROR WHILE PARSING JSON xd");
                    e.printStackTrace();
                }
            }
        }
        ArrayList<Node> temp = graph.calc(prodList);
        for(Node n : temp){
            System.out.println("node: " + n.getIndex());
        }
        path = temp;
    }

    public void addItem(String item){
        items.add(item);
        adapter.notifyDataSetChanged();
    }

    public void removeItem2(String remove){
        recommended_items.remove(remove);
        adapter2.notifyDataSetChanged();
    }

    public void removeItem(String remove){
        items.remove(remove);
        adapter.notifyDataSetChanged();
    }

    Toast t;
    private void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(getContext(),s,Toast.LENGTH_SHORT);
        t.show();
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void makeGetReqItemList(){
        RequestQueue request = Volley.newRequestQueue(getContext());
        String url = "http://130.89.169.123:3000/items";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                    jArray = jObject.getJSONArray("items");
                    System.out.println(jArray);
                    for (int i=0; i < jArray.length(); i++)
                    {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            String name = oneObject.getString("name");
                            items_db.add(name);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
        request.add(stringRequest);
    }

    private void makeGetReqRecommendations(){
        RequestQueue request = Volley.newRequestQueue(getContext());
        String url = "http://130.89.169.123:3000/recommendations";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo1 = new JSONObject(response);
                    System.out.println(response);
                    JSONArray ja1 = jo1.getJSONArray("recommendations");
                    ArrayList<Product> arr = graph.getProductsOnRoute(path);
                    for(int i = 0;i < ja1.length();i++){
                        JSONObject jo2 = ja1.getJSONObject(i);
                        String priItemName = jo2.getString("item");
                        String s = items.get(items.size()-1);
                        if (!priItemName.equals(s))
                            continue;
                        JSONArray ja2 = jo2.getJSONArray("recommendation_list");
                        for(int j = 0;j < ja2.length(); j++){
                            String name = ja2.getString(j);
                            if (recommended_items.size() < items.size() * 2 && !recommended_items.contains(name) && !items.contains(name)) {
                                Product product = null;
                                for (Product p : all_items_db) {
                                    if (p.getName().equals(name)) {
                                        product = p;
                                        break;
                                    }
                                }
                                if (arr.contains(product))
                                {
                                    recommended_items.add(name);
                                    adapter2.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
        request.add(stringRequest);
    }

    //private void makeGetReqFastestLane(){
    //    RequestQueue request = Volley.newRequestQueue(this);
    //    String url = "http://130.89.234.24:3000/fastestLane";
    //    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
    //        @Override
    //        public void onResponse(String response) {
    //            try {
    //                JSONObject jObject = new JSONObject(response);
    //                JSONArray jArray = jObject.getJSONArray("filter");
    //                for (int i=0; i < jArray.length(); i++)
    //                {
    //                    try {
    //                        JSONObject oneObject = jArray.getJSONObject(i);
    //                        String name = oneObject.getString("name");
    //                        items_db.add(name);
    //                    } catch (JSONException e) {
    //                        // Oops
    //                    }
    //                }
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
    //        @Override
    //        public void onErrorResponse(VolleyError error) {
    //            System.out.println(error.toString());
    //        }
    //    });
    //    request.add(stringRequest);
    //}

    private void postDataUsingVolley(ArrayList<String> shopping_list){
        String url = "http://130.89.169.123:3000/purchase";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject postData = new JSONObject();
        try {
            JSONArray array=new JSONArray();
            for(int i=0;i<items.size();i++){
                array.put(items.get(i));
            }
            postData.put("purchasedItems",array);
            System.out.println(items + items.getClass().toString());
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                makeToast("Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void getAllItemsAndIntegrate(){
        all_items_db = new ArrayList<>();
        RequestQueue request = Volley.newRequestQueue(getContext());
        String url = "http://130.89.169.123:3000/items";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                    jArray = jObject.getJSONArray("items");
                    System.out.println(jArray);
                    for (int i=0; i < jArray.length(); i++)
                    {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            String name = oneObject.getString("name");
                            int from = Integer.parseInt(oneObject.getString("from"));
                            int to = Integer.parseInt(oneObject.getString("to"));
                            float percent = Float.parseFloat(oneObject.getString("percent"));
                            all_items_db.add(new Product(name,from,to,percent));
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                    graph.integrateProducts(all_items_db);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
        request.add(stringRequest);
        System.out.println("============" + all_items_db+ "==============");

    }

    @Override
    public void onStart() {
        input.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,items_db));
        input.setThreshold(1);
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        vm.setmItemsList(items);
        super.onDestroyView();
        binding = null;
    }
}