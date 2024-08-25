package za.co.tyaphile;

public class Product {
    private String id;
    private String product_name;
    private String product_description;
    private double product_old_price;
    private double product_current_price; ;
    private String product_weight;
    private String product_measure;
    private String product_bulk ;
    private String product_image;
    private String product_link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public double getProduct_old_price() {
        return product_old_price;
    }

    public void setProduct_old_price(double product_old_price) {
        this.product_old_price = product_old_price;
    }

    public double getProduct_current_price() {
        return product_current_price;
    }

    public void setProduct_current_price(double product_current_price) {
        this.product_current_price = product_current_price;
    }

    public String getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(String product_weight) {
        this.product_weight = product_weight;
    }

    public String getProduct_measure() {
        return product_measure;
    }

    public void setProduct_measure(String product_measure) {
        this.product_measure = product_measure;
    }

    public String getProduct_bulk() {
        return product_bulk;
    }

    public void setProduct_bulk(String product_bulk) {
        this.product_bulk = product_bulk;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_link() {
        return product_link;
    }

    public void setProduct_link(String product_link) {
        this.product_link = product_link;
    }
}