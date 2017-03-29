package com.nigelcardozo.screenscraper;

import android.app.Application;
import android.content.Context;

import com.nigelcardozo.screenscraper.model.ProductList;

//The controller is used to decouple the UI from the data. Note, it's one way.
//Since data retrieval is an asynchronous task a message will be sent to the view
//when there is data to be retrieved. The view will then retrieve the data via the
//controller for display to the user.

public class Controller extends Application{

    private ProductDataModel productDataModel;
    private static Controller controllerInstance = null;

    public static Controller getControllerInstance(Context context)
    {
        if (controllerInstance == null)
        {
            controllerInstance = new Controller(context);
        }

        return controllerInstance;
    }

    private Controller(Context context)
    {
        productDataModel = new ProductDataModel(context);
    }

    public void requestData()
    {
        //Causes ProductDataModel to retrieve new data
        productDataModel.requestData();
    }

    public ProductList getProductList()
    {
        //Returns a list of places
        return productDataModel.getProductList();
    }

    public void performCleanup()
    {
        //Allows any cleanup operations to happen
        productDataModel.performCleanup();
    }
}