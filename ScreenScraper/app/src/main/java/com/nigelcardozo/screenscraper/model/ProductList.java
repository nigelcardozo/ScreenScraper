package com.nigelcardozo.screenscraper.model;

import java.util.ArrayList;
import java.util.List;

public class ProductList {

    private String totalCost;

    public List<Product> productList = new ArrayList<>();

    public void setTotalCost(String totalCost)
    {
        this.totalCost = totalCost;
    }

    public String getTotalCost()
    {
        return this.totalCost;
    }
}
