package com.nigelcardozo.screenscraper;

//This class is responsible for retrieving data pertaining to the Sainsbury's query and storing it
//in appropriate objects to allow the View to access it.

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import com.android.volley.toolbox.StringRequest;
import com.nigelcardozo.screenscraper.model.Product;
import com.nigelcardozo.screenscraper.model.ProductList;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ProductDataModel {

    private static final String REQUEST_TAG = "PlacesDataModelRequest";
    private static final String MESSAGE_PRODUCT_RETRIEVED_TAG = "ProductsRetrieved";
    private static final String MESSAGE_PRODUCT_NO_DATA_RECEIVED = "ProductsNoDataReceived";

    private ProductList productList;
    private RequestQueue mVolleyQueue;
    private Context context;

    public ProductDataModel(Context appContext){

        context = appContext;

        initialise();
    }

    public void requestData()
    {
        //This method is called by the controller after it receives a request for new data
        //Clear old data then call the JSON request method to make a new request via volley
        //In this app it won't be used more than once, i.e. as there's only a single query.
        productList.productList.clear();

        //Under normal circumstances a URL such as this would be built up.
        //Since I have been provided with a single hardcoded URL and no request to support
        //multiple URLs, a simple string assignment should suffice.
        String url = "http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/5_products.html";
        makeAndSendVolleyStringRequest(url);
    }

    public ProductList getProductList()
    {
        //This method is called by the controller after it receives a request to retrieve the
        //productList. This will occur after an indication has been sent to the View informing it
        //that we have new data for it to 'read'
        return productList;
    }

    public void performCleanup()
    {
        performVolleyCleanUp();
    }

    private void initialise()
    {
        //Perform any initialisation routines here.

        productList = new ProductList();

        mVolleyQueue = VolleyRequestQueueHelper.getInstance(context)
                .getRequestQueueInstance();
    }


    private void makeAndSendVolleyStringRequest(String url)
    {
        //Populate Volley String Request
        StringRequest req = new StringRequest(Request.Method.GET,
                url,
                volleyStringReqSuccessCallback(),
                volleyStringReqErrorCallback());

        //This tag is only useful for cancelling request, we can't seem to check it when getting
        //a response
        req.setTag(REQUEST_TAG);

        //Add our request to the Volley queue
        addRequestToVolleyQueue(req);
    }


    /* VOLLEY FUNCTIONS */

    private void addRequestToVolleyQueue(StringRequest req)
    {
        //Add the request to the volley queue
        mVolleyQueue.add(req);
    }

    private void performVolleyCleanUp()
    {
        // Clear down if required
        if (mVolleyQueue != null) {
            mVolleyQueue.cancelAll(REQUEST_TAG);
        }
    }

    private Response.Listener<String> volleyStringReqSuccessCallback() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //We've got a response. Pass this to our parser. For this app only one response
                //is handled. If this were an app with multiple queries it would be handled
                //differently.
                parseStringResponse(response);
            }
        };
    }

    private Response.ErrorListener volleyStringReqErrorCallback() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //We should handle errors here, for now as this is a demo, simply indicate an error
                //to the view. This will then be reported to the user.
                EventBus.getDefault().post(new MessageEvent(MESSAGE_PRODUCT_NO_DATA_RECEIVED));
            }
        };
    }

    private void parseStringResponse(String response)
    {
        int numProducts;
        int arrayCounter = 0;
        double totalCost=0;

        //Use JSOUP to parse the string response.
        Document doc = Jsoup.parse(response);
        doc.outputSettings().escapeMode(Entities.EscapeMode.extended);

        //Use the productInfo tag to find the products
        Elements elements = doc.getElementsByClass("productInfo");
        numProducts = elements.size();

        //We extract data and write it to a series of local arrays.
        //Ideally we could write it straight to the class, however
        //some 'divs' can be read as a total amount of contained items
        //and others cannot. It will make parsing and writing very messy
        //if not handled in this way.
        String[] productNames = new String[numProducts];
        String[] productImageUrl = new String[numProducts];
        String[] productCost = new String[numProducts];
        String[] productMeasureCost = new String[numProducts];

        //Extract Product name and Image URL
        Elements productNameElements = doc.getElementsByTag("h3");

        for (Element productNameElement : productNameElements) {

            //Extract all the product names and write them to our temporary array
            Elements links = productNameElement.getElementsByTag("a");
            for (Element link : links) {
                productNames[arrayCounter] = link.text();
            }

            //Extract all the image URLs and write them to our temporary array
            Elements imageTags = productNameElement.getElementsByTag("img");
            for (Element imageTag : imageTags) {
                productImageUrl[arrayCounter] = imageTag.attr("src");
            }

            arrayCounter++;
        }



        //Extract Product Cost
        Elements productCostElements = doc.getElementsByClass("pricePerUnit");

        arrayCounter = 0;

        for (Element productCostElement : productCostElements) {
            String cost = productCostElement.text();
            //We need to remove the &pound tag
            cost = cost.replaceAll("&pound","");
            productCost[arrayCounter] = "£" + cost;

            //Need to remove this to get numeric representation
            cost = cost.replaceAll("unit","");
            cost = cost.replaceAll("/","");

            //Increment the totalCost
            Double dCost = Double.valueOf(cost);
            totalCost += dCost;
            arrayCounter++;
        }


        //Extract Unit Cost Element
        Elements productMeasureCostElements = doc.getElementsByClass("pricePerMeasure");

        arrayCounter = 0;

        for (Element productMeasureCostElement : productMeasureCostElements) {
            String cost = productMeasureCostElement.text();
            cost = cost.replaceAll("&pound","");

            productMeasureCost[arrayCounter] = "£" + cost;
            arrayCounter++;
        }


        //Copy the data from our temporary arrays to the product List
        for (int j=0; j<numProducts; j++)
        {
            Product product = new Product();
            product.setProductName(productNames[j]);
            product.setProductCost(productCost[j]);
            product.setProductMeasureCost(productMeasureCost[j]);
            product.setProductImageUrl(productImageUrl[j]);
            productList.productList.add(product);
        }

        //Ensure the cost is handled as 2 decimal places.
        DecimalFormat precision = new DecimalFormat("0.00");
        productList.setTotalCost(precision.format(totalCost));

        //Inform the view that there is now data for it to retrieve
        EventBus.getDefault().post(new MessageEvent(MESSAGE_PRODUCT_RETRIEVED_TAG));
    }
}
