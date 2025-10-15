package cafemenu;

public class MenuItem implements Comparable<MenuItem> {
    private String name;          // maps to item_name
    private String category;      // maps to category_name or derived from category_id
    private String subCategory;   // optional, can be null
    private String imageUrl;      // maps to image_url
    private int price;            // maps to price
    private int popularity;       // 1–5 stars
    private int caffeineLevel;    // 0–3

    // === Constructors ===
    public MenuItem(String name, String category, String subCategory, String imageUrl, int price) {
        this(name, category, subCategory, imageUrl, price, 0, calculateDefaultCaffeineLevel(category, name));
    }

    public MenuItem(String name, String category, String subCategory, String imageUrl,
                    int price, int popularity, int caffeineLevel) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.imageUrl = imageUrl;
        this.price = price;
        this.popularity = popularity;
        this.caffeineLevel = caffeineLevel;
    }

    // === Automatic caffeine level assignment based on category ===
    private static int calculateDefaultCaffeineLevel(String category, String name) {
        if (category == null || name == null) return 0;
        String lowerCategory = category.toLowerCase();
        String lowerName = name.toLowerCase();

        if (lowerCategory.contains("caffeine-free")) return 0;
        if (lowerCategory.contains("tea")) return lowerName.contains("matcha") ? 2 : 1;
        if (lowerCategory.contains("coffee")) {
            if (lowerName.contains("espresso") || lowerName.contains("cold brew")) return 3;
            if (lowerName.contains("coffee") || lowerName.contains("americano")) return 2;
            return 1;
        }
        return 0;
    }

    // === Getters ===
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getImageUrl() { return imageUrl; }
    public int getPrice() { return price; }
    public int getPopularity() { return popularity; }
    public int getCaffeineLevel() { return caffeineLevel; }

    public String getCaffeineLevelText() {
        return switch (caffeineLevel) {
            case 0 -> "Caffeine-Free";
            case 1 -> "Low";
            case 2 -> "Medium";
            case 3 -> "High";
            default -> "Unknown";
        };
    }

    // === Popularity Stars ===
    public String getPopularityStars() {
        StringBuilder stars = new StringBuilder();
        int cappedPopularity = Math.max(0, Math.min(popularity, 5)); // Ensure 0–5
        for (int i = 0; i < cappedPopularity; i++) stars.append("★");
        for (int i = cappedPopularity; i < 5; i++) stars.append("☆");
        return stars.toString();
    }

    // === Setters ===
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(int price) { this.price = price; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    public void setCaffeineLevel(int caffeineLevel) { this.caffeineLevel = caffeineLevel; }
    public void incrementPopularity() { this.popularity++; }

    // === Equality & Hashing ===
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (subCategory != null ? subCategory.hashCode() : 0);
        result = 31 * result + caffeineLevel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem other = (MenuItem) obj;
        return name.equals(other.name)
                && category.equals(other.category)
                && caffeineLevel == other.caffeineLevel;
    }

    // === Natural ordering by name ===
    @Override
    public int compareTo(MenuItem other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format(
                "MenuItem{name='%s', category='%s', caffeine=%s, price=%d, popularity=%d, stars='%s', imageUrl='%s'}",
                name, category, getCaffeineLevelText(), price, popularity, getPopularityStars(), imageUrl
        );
    }
}
