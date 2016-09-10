package na.ilovenougat;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
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


import na.ilovenougat.models.productsModel;

/**
 * Created by arisonu on 9/6/2016.
 */
public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.recyclerViewHolder> {
   private ArrayList<productsModel> arrayList =new ArrayList<productsModel>();
    public Context activityContext;
    private double zapposPrice;
    private String sixPmPrice;
    private StringBuffer buffer;
    private String currentZapposId;
    public recyclerAdapter(ArrayList<productsModel> arrayList, Context activity){
        this.arrayList=arrayList;
        this.activityContext=activity;
     }
    @Override
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.rows_layout,parent,false);
       recyclerViewHolder recyclerViewHolderObj= new recyclerViewHolder(view);
        return recyclerViewHolderObj;
    }

    @Override
    public void onBindViewHolder(recyclerViewHolder holder, int position) {//bind data to view,set text and images
      final productsModel productsData =arrayList.get(position);
        holder.imageView.setImageBitmap(productsData.getImage());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceCheckUrl="https://api.6pm.com/Search?term="+productsData.getProductID()+"&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb";
                currentZapposId=productsData.getProductID();//get product id of currently touched product

                //get price on zappos
                zapposPrice=Double.parseDouble(productsData.getPrice().substring(productsData.getPrice().indexOf("$")+1));
                new JSONTask().execute(priceCheckUrl);//search for product match on 6pm
            }
        });

       holder.product_name.setText(productsData.getProductName());
        holder.price_text.setTag(position);
        holder.price_text.setText("Zappos Price: "+productsData.getPrice());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public class JSONTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //Get json from url connection
            sixPmPrice=null;
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

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);

                JSONArray parentArray = parentObject.getJSONArray("results");

                for(int i=0;i<parentArray.length();i++) {//get important data from json
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String sixPmProductId=finalObject.getString("productId");

                    if(sixPmProductId.equals(currentZapposId)) {
                       //if zappos product is equal to an existing sixPmProduct then set sixPmPrice
                        sixPmPrice = finalObject.getString("price");
                        break;
                    }

                }





            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
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
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(sixPmPrice!=null) {//if product exists on six pm
                double sixPrice = Double.parseDouble(sixPmPrice.substring(sixPmPrice.indexOf("$") + 1));

                if (sixPrice < zapposPrice) {//if product is cheaper on six pm


                    Toast.makeText(activityContext, "Product is cheaper on 6pm. Price: $" + sixPrice, Toast.LENGTH_SHORT).show();//display message
                }
                else{
                    Toast.makeText(activityContext, "Product is cheaper on Zappos. Price: $" + zapposPrice, Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(activityContext, "Product does not exist on 6pm. Zappos Price: $" + zapposPrice, Toast.LENGTH_SHORT).show();
            }

        }


    }

        public static class recyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView product_name,price_text;
        public recyclerViewHolder(View view){//objects to later inflate
            super(view);
            imageView=(ImageView)view.findViewById(R.id.img);
            product_name=(TextView)view.findViewById(R.id.product_name);
            price_text=(TextView)view.findViewById(R.id.price);
        }
    }



}
