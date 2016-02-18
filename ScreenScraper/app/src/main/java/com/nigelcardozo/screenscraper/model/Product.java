package com.nigelcardozo.screenscraper.model;

import java.io.Serializable;

//This is implemented as serializable as we need to pass the object back to the view
public class Product implements Serializable{

    private String productName;
    private String productCost;
    private String productMeasureCost;
    private String productImageUrl;

    public Product()
    {

    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public void setProductCost(String productCost)
    {
        this.productCost = productCost;
    }

    public void setProductMeasureCost(String productMeasureCost)
    {
        this.productMeasureCost = productMeasureCost;
    }

    public void setProductImageUrl(String productImageUrl)
    {
        this.productImageUrl = productImageUrl;
    }

    public String getProductName()
    {
        return this.productName;
    }

    public String getProductCost()
    {
        return this.productCost;
    }

    public String getProductMeasureCost()
    {
        return this.productMeasureCost;
    }

    public String getProductImageUrl()
    {
        return this.productImageUrl;
    }

}
