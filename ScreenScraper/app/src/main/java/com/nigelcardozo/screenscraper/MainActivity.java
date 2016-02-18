package com.nigelcardozo.screenscraper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.nigelcardozo.screenscraper.model.ProductList;


public class MainActivity extends Activity {

    private static String LOG_TAG = "MAIN_ACTIVITY";
    private static final String MESSAGE_PRODUCT_RETRIEVED_TAG = "ProductsRetrieved";
    private static final String MESSAGE_PRODUCT_NO_DATA_RECEIVED = "ProductsNoDataReceived";
    private static final int ERROR_NO_DATA_RECEIVED = 2;
    private boolean bFirstLoad = false;

    private Controller controller;

    private ProgressDialog loadingProgressDialog;
    private ListView productListResponseListView;
    private TextView textViewTotalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise the UI.
        initialise();

        //Ask the controller to retrieve data.
        requestData();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        controller.performCleanup();
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event)
    {
        //This function handles the incoming message from EventBus
        //It is a means of the Model informing the View that something has happened,
        //for example, new data has arrived, or a request to retrieve data has failed.

        if (event.message.equals(MESSAGE_PRODUCT_RETRIEVED_TAG))
        {
            handleMessageProductsRetrieved();
        }
        else if (event.message.equals(MESSAGE_PRODUCT_NO_DATA_RECEIVED))
        {
            displayErrorFeedback(ERROR_NO_DATA_RECEIVED);
        }

    }

    private void initialise()
    {
        bFirstLoad=true;

        //Get an instance of the controller
        controller = Controller.getControllerInstance(getApplicationContext());

        //Setup our UI controls, i.e. the views.
        setupUIControls();
    }

    private void handleMessageProductsRetrieved()
    {
        //This function is a handler for the event that we now have a list of products.
        //It is a trigger that will cause the view to request the updated products list.
        //There is a check to ensure the list is populated and, if so the UI is then updated
        ProductList receivedProductList = controller.getProductList();

        if (receivedProductList != null)
        {
            if (bFirstLoad==true)
            {
                bFirstLoad=false;
                loadingProgressDialog.dismiss();
            }

            updateUI(receivedProductList);
        }
        else
        {
            displayErrorFeedback(ERROR_NO_DATA_RECEIVED);
        }
    }

    private void setupUIControls()
    {
        //Setup the UI Controls
        loadingProgressDialog = new ProgressDialog(this);
        displayProgressDialogBox();

        productListResponseListView = (ListView) findViewById(R.id.listViewGooglePlacesResponse);

        textViewTotalCost = (TextView) findViewById(R.id.textViewTotalCost);
    }

    private void displayProgressDialogBox()
    {
        //Provide feedback to the user so they're not staring at a blank screen.
        loadingProgressDialog.setTitle(getString(R.string.progress_searching_title));
        loadingProgressDialog.setMessage(getString(R.string.progress_searching_text));
        loadingProgressDialog.show();
    }

    private void displayErrorFeedback(int errorCode)
    {
        //Errors occur, this is a very simplistic handler. A simple alert dialog.

        String errorString = mapErrorCode(errorCode);

        if (errorString != null)
        {
            //Provide error feedback to the user.
            //This is a very primitive dialog but it's here purely for completeness.
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.error_title));
            alertDialog.setMessage(errorString);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private String mapErrorCode(int errorCode)
    {
        //Map an errorcode to the appropriate string (from the strings table)
        //Only one for now but this function is here for good practice.
        switch (errorCode)
        {
            case ERROR_NO_DATA_RECEIVED:
            {
                return getString(R.string.error_no_data_received);
            }

            default:
            {
                return null;
            }
        }
    }

    private void requestData()
    {
        //This method will cause a request for new data to occur. It will make the model
        //call the predefined URL and retrieve the data. Once data has been received an
        //event message will be sent to the view. The View will then send another request
        //to retrieve the data.
        productListResponseListView.setAdapter(null);
        controller.requestData();
    }

    private void updateUI(ProductList receivedProductList)
    {
        //This method is called to update the UI.

        //Update the total cost
        textViewTotalCost.setText(getString(R.string.total_cost) + receivedProductList.getTotalCost());

        //Set our CustomAdapter to bridge the data to the view
        productListResponseListView.setAdapter(new CustomProductListAdapter(this, receivedProductList));
    }
}
