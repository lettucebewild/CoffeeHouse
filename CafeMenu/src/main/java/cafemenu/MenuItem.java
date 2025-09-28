package cafemenu;

public class MenuItem {
    private String name;
    private String category;
    private String subCategory;
    private String imageUrl;
    private int price;

    public MenuItem(String name, String category, String subCategory, String imageUrl, int price) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
}
