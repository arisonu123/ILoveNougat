package na.ilovenougat;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import na.ilovenougat.models.productsModel;
//?extends action bar activity
public class searchResultsActivity extends AppCompatActivity {//activity used to handle searches
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<productsModel> arrayList=new ArrayList<productsModel>();
    private String query;
    private StringBuffer buffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);



        Toolbar my_toolbar=(Toolbar)findViewById(R.id.search_results_toolbar);
        setSupportActionBar(my_toolbar);//make sure toolbar is supported by all versions
        getSupportActionBar().setHomeButtonEnabled(true);//create back button to go to main activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set toolbar title and icons

        getSupportActionBar().setSubtitle(R.string.app_subtitle);
        getSupportActionBar().setIcon(R.drawable.ic_action_name);

        query=new String();
        Intent searchIntent=getIntent();
        if(Intent.ACTION_SEARCH.equals(searchIntent.getAction())){//get search query with intent
            query=searchIntent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query);
            Toast.makeText(searchResultsActivity.this,query,Toast.LENGTH_SHORT).show();
            String urlProduct = "https://api.zappos.com/Search?term=" + query + "&key=b743e26728e16b81da139182bb2094357c31d331";
            new JSONTask().execute(urlProduct);
        }


        //create recycler view

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view_search);
        layoutManager=new LinearLayoutManager(this);

    }


public class JSONTask extends AsyncTask<String, String, List<productsModel>> {

    @Override
    protected List<productsModel> doInBackground(String... params) {
        //Get json from url connection
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {//try to connect to url
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            //get text from json
            reader = new BufferedReader(new InputStreamReader(stream));
            buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson=buffer.toString();

            JSONObject parentObject=new JSONObject(finalJson);

            JSONArray  parentArray=parentObject.getJSONArray("results");

            List<productsModel>productsModelList=new ArrayList<>();
            System.out.print("Json length is : "+parentArray.length());
            for(int i=0;i<parentArray.length();i++) {//get important data from json

                JSONObject finalObject = parentArray.getJSONObject(i);
                productsModel productsTemplate=new productsModel();

                productsTemplate.setProductName(finalObject.getString("productName"));
                productsTemplate.setPrice(finalObject.getString("price"));
                productsTemplate.setThumbNailUrl(finalObject.getString("thumbnailImageUrl"));
                productsTemplate.setProuductID(finalObject.getString("productId"));



                Bitmap bmImg = null;
                URL myfileurl = null;
                HttpURLConnection conn=null;
                try {//get image
                    myfileurl = new URL(productsTemplate.getThumbNailUrl());
                    conn = (HttpURLConnection) myfileurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    int length = conn.getContentLength();
                    if (length > 0) {
                        int[] bitmapData = new int[length];
                        byte[] bitmapData2 = new byte[length];
                        InputStream is = conn.getInputStream();
                        bmImg = BitmapFactory.decodeStream(is);

                    } else {

                    }

                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {

                    e.printStackTrace();
                }

                finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

               productsTemplate.setImage(bmImg);



                productsModelList.add(productsTemplate);
            }

            return productsModelList;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<productsModel> result) {
        super.onPostExecute(result);


        for(productsModel product: result){//add all products to arrayList


            arrayList.add(product);


        }

        adapter=new recyclerAdapter(arrayList,getApplicationContext());//set up recyclerview adapter
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



    }

  }
}
