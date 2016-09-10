package na.ilovenougat.models;

import android.graphics.Bitmap;

/**
 * Created by arisonu on 9/7/2016.
 */
public class productsModel {

    private String thumbNailUrl;
    private String price;
    private String productName;
    private String productID;
    private Bitmap image;


    //get and set functions



    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public void setThumbNailUrl(String thumbNailUrl) {
        this.thumbNailUrl = thumbNailUrl;
    }
    public String getProductID(){
        return productID;
    }
    public void setProuductID(String id){
        this.productID=id;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImage(Bitmap imageDisplay){
        this.image=imageDisplay;
    }

    public Bitmap getImage(){
        return image;
    }

}
