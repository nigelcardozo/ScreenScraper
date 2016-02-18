package com.nigelcardozo.screenscraper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nigelcardozo.screenscraper.model.Product;
import com.nigelcardozo.screenscraper.model.ProductList;
import com.squareup.picasso.Picasso;

public class CustomProductListAdapter extends BaseAdapter {

    Context context;
    ProductList storedProductList;
    private static LayoutInflater inflater=null;

    public CustomProductListAdapter(Context receivedContext, ProductList productList) {


        storedProductList = productList;
        context=receivedContext;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        //The amount of entries is equivalent to the number of items in the productList
        //We could store the value and then return it when required but for this application this
        //should suffice, caching is unnecessary.

        if (storedProductList != null)
        {
            return storedProductList.productList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {

        //Return a place object.
        Product product = storedProductList.productList.get(position);
        return product;
    }

    @Override
    public long getItemId(int position) {

        //Return the item id, in this case position.
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class Holder
    {
        //Our view holder.
        ImageView imageViewProductImage;
        TextView textViewProductName;
        TextView textViewProductCost;
        TextView textViewProductMeasureCost;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //This is an implementation of the viewHolder pattern. The purpose of this is to save the costly
        //parts of listview usage when we scroll. Thus, this pattern allows us to not
        //keep calling findViewById and instead recycle the views.

        //We start by inflating the layout that we are associating with the listview.

        Holder viewHolder;

        if (convertView == null)
        {
            //Assign the views to the holder. When we can reuse these we will save time and help
            //to ensure smoother scrolling.
            convertView = inflater.inflate(R.layout.productlist, parent, false);
            viewHolder = new Holder();

            viewHolder.textViewProductName=(TextView) convertView.findViewById(R.id.textViewProductName);
            viewHolder.textViewProductCost=(TextView) convertView.findViewById(R.id.textViewProductCost);
            viewHolder.textViewProductMeasureCost=(TextView) convertView.findViewById(R.id.textViewProductMeasureCost);
            viewHolder.imageViewProductImage=(ImageView) convertView.findViewById(R.id.imageViewProductImage);

            convertView.setTag(viewHolder);
        }
        else
        {
            //Recycling the view, which means we don't have to call findViewById again.
            viewHolder = (Holder) convertView.getTag();
        }

        final Product product = (Product) getItem(position);

        if (product != null)
        {
            //Populate the views
            viewHolder.textViewProductName.setText(product.getProductName());
            viewHolder.textViewProductCost.setText(product.getProductCost());
            viewHolder.textViewProductMeasureCost.setText(product.getProductMeasureCost());

            //Utilise picasso to load in the images. Utilise a placeholder so the user has something
            //to see.
            Picasso.with(context).load(product.getProductImageUrl()).placeholder(R.drawable.placeholder).into(viewHolder.imageViewProductImage);
        }

        return convertView;
    }
}