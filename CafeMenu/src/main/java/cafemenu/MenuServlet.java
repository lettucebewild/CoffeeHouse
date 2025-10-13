package cafemenu;

public class MenuItem implements Comparable<MenuItem> {
    private String name;
    private String category;
    private String subCategory;
    private String imageUrl;
    private int price;
    private int popularity;
    private int caffeineLevel; // 0: Caffeine-Free, 1: Low, 2: Medium, 3: High

    public MenuItem(String name, String category, String subCategory, String imageUrl, int price) {
        this(name, category, subCategory, imageUrl, price, 0, calculateDefaultCaffeineLevel(category, name));
    }

    public MenuItem(String name, String category, String subCategory, String imageUrl, int price, int popularity, int caffeineLevel) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.imageUrl = imageUrl;
        this.price = price;
        this.popularity = popularity;
        this.caffeineLevel = caffeineLevel;
    }

    private static int calculateDefaultCaffeineLevel(String category, String name) {
        switch (category) {
            case "Caffeine-Free Drinks":
                return 0;
            case "Tea":
                return name.toLowerCase().contains("matcha") ? 2 : 1;
            case "Coffee":
                if (name.toLowerCase().contains("espresso") || name.toLowerCase().contains("cold brew")) return 3;
                if (name.toLowerCase().contains("coffee") || name.toLowerCase().contains("americano")) return 2;
                return 1;
            default:
                return 0;
        }
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public int getPopularity() { return popularity; }
    public int getCaffeineLevel() { return caffeineLevel; }
    public String getCaffeineLevelText() {
        switch (caffeineLevel) {
            case 0: return "Caffeine-Free";
            case 1: return "Low";
            case 2: return "Medium";
            case 3: return "High";
            default: return "Unknown";
        }
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    public void setCaffeineLevel(int caffeineLevel) { this.caffeineLevel = caffeineLevel; }
    public void incrementPopularity() { this.popularity++; }

    // Hash code for efficient hashing
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + (subCategory != null ? subCategory.hashCode() : 0);
        result = 31 * result + caffeineLevel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MenuItem other = (MenuItem) obj;
        return name.equals(other.name) && 
               category.equals(other.category) && 
               caffeineLevel == other.caffeineLevel;
    }

    // Natural ordering by name
    @Override
    public int compareTo(MenuItem other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format("MenuItem{name='%s', category='%s', caffeine=%s, price=%d, popularity=%d}", 
                           name, category, getCaffeineLevelText(), price, popularity);
    }
}
