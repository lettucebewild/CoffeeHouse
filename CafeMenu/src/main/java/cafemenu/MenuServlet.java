package cafemenu;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/MenuServlet")
public class MenuServlet extends HttpServlet {
    
    private List<MenuItem> menu;
    private Map<String, List<MenuItem>> categoryMap;
    private Map<String, MenuItem> itemNameMap;
    private Map<Integer, List<MenuItem>> caffeineLevelMap;
    private List<MenuItem> bestSellers;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        if (!initializeDatabase()) {
            initializeDefaultMenu();
        }
        initializeDataStructures();
    }

    private boolean initializeDatabase() {
        try {
            // Try to load MySQL driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            
            String url = "jdbc:mysql://localhost:3306/coffeemenu";
            String username = "root";
            String password = "Catsarecute!:3";
            
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully!");
            
            loadMenuFromDatabase();
            return true;
            
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }

    private void loadMenuFromDatabase() {
        menu = new ArrayList<>();
        try {
            String sql = "SELECT name, category, sub_category, image_url, price, popularity, caffeine_level FROM menu_items ORDER BY category, name";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String subCategory = resultSet.getString("sub_category");
                String imageUrl = resultSet.getString("image_url");
                int price = resultSet.getInt("price");
                int popularity = resultSet.getInt("popularity");
                int caffeineLevel = resultSet.getInt("caffeine_level");
                
                menu.add(new MenuItem(name, category, subCategory, imageUrl, price, popularity, caffeineLevel));
            }
            
            resultSet.close();
            statement.close();
            System.out.println("Menu loaded from database: " + menu.size() + " items");
            
        } catch (SQLException e) {
            System.err.println("Failed to load menu from database: " + e.getMessage());
            initializeDefaultMenu();
        }
    }

    private void initializeDefaultMenu() {
        menu = new ArrayList<>();
        
        // Coffee - High Caffeine
        menu.add(new MenuItem("Espresso", "Coffee", "Hot", "", 100, 88, 3));
        menu.add(new MenuItem("Americano", "Coffee", "Hot", "", 90, 75, 3));
        menu.add(new MenuItem("Cold Brew Espresso", "Coffee", "Iced", "", 150, 80, 3));
        
        // Coffee - Medium Caffeine
        menu.add(new MenuItem("Latte", "Coffee", "Hot", "", 120, 85, 2));
        menu.add(new MenuItem("Mocha Latte", "Coffee", "Hot", "", 140, 78, 2));
        menu.add(new MenuItem("Cappuccino", "Coffee", "Hot", "", 110, 70, 2));
        menu.add(new MenuItem("Iced Latte", "Coffee", "Iced", "", 130, 95, 2));
        menu.add(new MenuItem("Iced Mocha Latte", "Coffee", "Iced", "", 140, 82, 2));
        menu.add(new MenuItem("Cold Brew", "Coffee", "Iced", "", 140, 78, 2));
        
        // Coffee - Low Caffeine
        menu.add(new MenuItem("Honey Oat Milk Latte", "Coffee", "Hot", "", 130, 92, 1));
        menu.add(new MenuItem("Iced Skinny Vanilla Latte", "Coffee", "Hot", "", 130, 65, 1));
        menu.add(new MenuItem("Hot Coffee", "Coffee", "Hot", "", 80, 60, 1));
        menu.add(new MenuItem("Honey Oat Milk Iced Latte", "Coffee", "Iced", "", 130, 90, 1));
        menu.add(new MenuItem("Iced Vanilla Latte", "Coffee", "Iced", "", 130, 68, 1));
        menu.add(new MenuItem("Iced Cappuccino", "Coffee", "Iced", "", 120, 72, 1));
        menu.add(new MenuItem("Cold Brew Mocha", "Coffee", "Iced", "", 160, 85, 1));

        // Tea - Medium Caffeine
        menu.add(new MenuItem("Matcha Latte", "Tea", "Hot", "", 130, 88, 2));
        menu.add(new MenuItem("Iced Matcha Latte", "Tea", "Iced", "", 140, 95, 2));
        
        // Tea - Low Caffeine
        menu.add(new MenuItem("Chai Tea Latte", "Tea", "Hot", "", 120, 75, 1));
        menu.add(new MenuItem("Iced Chai Tea Latte", "Tea", "Iced", "", 140, 92, 1));
        menu.add(new MenuItem("Hot Tea", "Tea", "Hot", "", 80, 55, 1));
        menu.add(new MenuItem("Iced Tea", "Tea", "Iced", "", 90, 65, 1));

        // Caffeine-Free Drinks
        menu.add(new MenuItem("Hot Chocolate", "Caffeine-Free Drinks", "Hot", "", 95, 85, 0));
        menu.add(new MenuItem("Chocolate Milk", "Caffeine-Free Drinks", "Iced", "", 90, 70, 0));
        menu.add(new MenuItem("Lemonade", "Caffeine-Free Drinks", "Iced", "", 85, 80, 0));

        // Bread and Pastry (Caffeine-Free)
        menu.add(new MenuItem("Ensaymada Bread", "Bread and Pastry", "", "", 70, 75, 0));
        menu.add(new MenuItem("Chocolate Croissant", "Bread and Pastry", "", "", 80, 88, 0));
        menu.add(new MenuItem("Chocolate Chip Muffin", "Bread and Pastry", "", "", 75, 82, 0));
        menu.add(new MenuItem("Banana Nut Muffin", "Bread and Pastry", "", "", 70, 65, 0));
        menu.add(new MenuItem("Cheesecake Slice", "Bread and Pastry", "", "", 100, 90, 0));
        menu.add(new MenuItem("Chocolate Cake Slice", "Bread and Pastry", "", "", 100, 85, 0));

        // Sandwiches (Caffeine-Free)
        menu.add(new MenuItem("Chicken Sandwich", "Sandwiches", "", "", 120, 78, 0));
        menu.add(new MenuItem("Ham and Cheese Sandwich", "Sandwiches", "", "", 130, 82, 0));
        menu.add(new MenuItem("Fried Egg Sandwich", "Sandwiches", "", "", 110, 75, 0));
        menu.add(new MenuItem("Beef Cheese Burger", "Sandwiches", "", "", 150, 88, 0));
    }

    private void initializeDataStructures() {
        // Initialize hash maps
        categoryMap = new HashMap<>();
        itemNameMap = new HashMap<>();
        caffeineLevelMap = new HashMap<>();
        
        // Populate hash maps
        for (MenuItem item : menu) {
            // Category map
            categoryMap.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
            
            // Item name map
            itemNameMap.put(item.getName().toLowerCase(), item);
            
            // Caffeine level map
            caffeineLevelMap.computeIfAbsent(item.getCaffeineLevel(), k -> new ArrayList<>()).add(item);
        }
        
        // Sort items using various algorithms
        sortAllDataStructures();
        
        // Initialize best sellers
        updateBestSellers();
        
        System.out.println("Data structures initialized with " + menu.size() + " items");
    }

    private void sortAllDataStructures() {
        // Sort main menu by name using QuickSort
        quickSortByName(menu, 0, menu.size() - 1);
        
        // Sort category items
        for (List<MenuItem> categoryItems : categoryMap.values()) {
            quickSortByName(categoryItems, 0, categoryItems.size() - 1);
        }
        
        // Sort caffeine level items by caffeine level (using Counting Sort for small range)
        for (List<MenuItem> caffeineItems : caffeineLevelMap.values()) {
            quickSortByName(caffeineItems, 0, caffeineItems.size() - 1);
        }
    }

    // QuickSort by name
    private void quickSortByName(List<MenuItem> items, int low, int high) {
        if (low < high) {
            int pi = partitionByName(items, low, high);
            quickSortByName(items, low, pi - 1);
            quickSortByName(items, pi + 1, high);
        }
    }

    private int partitionByName(List<MenuItem> items, int low, int high) {
        MenuItem pivot = items.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (items.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(items, i, j);
            }
        }
        Collections.swap(items, i + 1, high);
        return i + 1;
    }

    // MergeSort by price (ascending)
    private void mergeSortByPrice(List<MenuItem> items, int left, int right, boolean ascending) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortByPrice(items, left, mid, ascending);
            mergeSortByPrice(items, mid + 1, right, ascending);
            mergeByPrice(items, left, mid, right, ascending);
        }
    }

    private void mergeByPrice(List<MenuItem> items, int left, int mid, int right, boolean ascending) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<MenuItem> leftArray = new ArrayList<>(items.subList(left, left + n1));
        List<MenuItem> rightArray = new ArrayList<>(items.subList(mid + 1, mid + 1 + n2));

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            boolean condition = ascending ? 
                leftArray.get(i).getPrice() <= rightArray.get(j).getPrice() :
                leftArray.get(i).getPrice() >= rightArray.get(j).getPrice();
                
            if (condition) {
                items.set(k, leftArray.get(i));
                i++;
            } else {
                items.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }

        while (i < n1) {
            items.set(k, leftArray.get(i));
            i++;
            k++;
        }

        while (j < n2) {
            items.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }

    // HeapSort by popularity
    private void heapSortByPopularity(List<MenuItem> items) {
        int n = items.size();

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(items, n, i);
        }

        // Extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            Collections.swap(items, 0, i);
            heapify(items, i, 0);
        }
    }

    private void heapify(List<MenuItem> items, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && items.get(left).getPopularity() > items.get(largest).getPopularity()) {
            largest = left;
        }

        if (right < n && items.get(right).getPopularity() > items.get(largest).getPopularity()) {
            largest = right;
        }

        if (largest != i) {
            Collections.swap(items, i, largest);
            heapify(items, n, largest);
        }
    }

    // Counting Sort by caffeine level (efficient for small range 0-3)
    private List<MenuItem> countingSortByCaffeineLevel(List<MenuItem> items) {
        if (items.isEmpty()) return items;

        // Count occurrences of each caffeine level
        int[] count = new int[4]; // 0-3 caffeine levels
        for (MenuItem item : items) {
            count[item.getCaffeineLevel()]++;
        }

        // Calculate cumulative count
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        // Build output array
        List<MenuItem> output = new ArrayList<>(Collections.nCopies(items.size(), null));
        for (int i = items.size() - 1; i >= 0; i--) {
            MenuItem item = items.get(i);
            int index = count[item.getCaffeineLevel()] - 1;
            output.set(index, item);
            count[item.getCaffeineLevel()]--;
        }

        return output;
    }

    private void updateBestSellers() {
        bestSellers = new ArrayList<>(menu);
        heapSortByPopularity(bestSellers);
        Collections.reverse(bestSellers); // Descending order
        
        if (bestSellers.size() > 10) {
            bestSellers = bestSellers.subList(0, 10);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String category = req.getParameter("category");
        String search = req.getParameter("search");
        String sort = req.getParameter("sort");
        String caffeineFilter = req.getParameter("caffeine");

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Coffee House</title>");
        out.println("<link href='https://db.onlinewebfonts.com/c/e02bdd9828bed1e2490bdf94662dbba8?family=Arial+Nova+Cond' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("<style>");
        out.println("body { font-family: 'Arial Nova Cond', sans-serif; background-color: #ffff; margin: 0; padding: 0; }");
        out.println(".item-price { color: #9A3B3B; font-size: 35px; margin-top: 5px; font-weight: bold; }");
        out.println(".item-popularity { color: #666; font-size: 25px; }");
        out.println(".caffeine-badge { background: #322C2B; color: white; padding: 5px 10px; border-radius: 15px; font-size: 25px; margin-top: 5px; display: inline-block; }");
        out.println(".caffeine-high { background: #8B0000; }");
        out.println(".caffeine-medium { background: #B8860B; }");
        out.println(".caffeine-low { background: #006400; }");
        out.println(".caffeine-free { background: #4B0082; }");
        out.println("</style>");
        out.println("</head><body>");

        // Header
        out.println("<div class='header-wrapper'><div class='header'>");
        out.println("<div><a href='index.html'>Main</a></div>");
        out.println("<div class='header-right'>");
        out.println("<a href='index.html#bestsellers'>Best Sellers</a>");
        out.println("</div></div></div>");

        // Category Buttons
        out.println("<div class='category-buttons'>");
        
        // All Items button
        String allClass = (category == null || category.isEmpty()) ? " active" : "";
        out.println("<a href='MenuServlet' class='category-btn" + allClass + "'>");
        out.println("All Items <span class='category-count'>(" + menu.size() + ")</span></a>");
        
        // Coffee button
        int coffeeCount = categoryMap.getOrDefault("Coffee", Collections.emptyList()).size();
        String coffeeClass = "Coffee".equals(category) ? " active" : "";
        out.println("<a href='MenuServlet?category=Coffee' class='category-btn" + coffeeClass + "'>");
        out.println("Coffee <span class='category-count'>(" + coffeeCount + ")</span></a>");
        
        // Tea button
        int teaCount = categoryMap.getOrDefault("Tea", Collections.emptyList()).size();
        String teaClass = "Tea".equals(category) ? " active" : "";
        out.println("<a href='MenuServlet?category=Tea' class='category-btn" + teaClass + "'>");
        out.println("Tea <span class='category-count'>(" + teaCount + ")</span></a>");
        
        // Caffeine-Free Drinks button
        int caffeineFreeDrinksCount = categoryMap.getOrDefault("Caffeine-Free Drinks", Collections.emptyList()).size();
        String caffeineFreeDrinksClass = "Caffeine-Free Drinks".equals(category) ? " active" : "";
        out.println("<a href='MenuServlet?category=Caffeine-Free Drinks' class='category-btn" + caffeineFreeDrinksClass + "'>");
        out.println("Caffeine-Free Drinks <span class='category-count'>(" + caffeineFreeDrinksCount + ")</span></a>");
        
        // Bread and Pastry button
        int breadCount = categoryMap.getOrDefault("Bread and Pastry", Collections.emptyList()).size();
        String breadClass = "Bread and Pastry".equals(category) ? " active" : "";
        out.println("<a href='MenuServlet?category=Bread and Pastry' class='category-btn" + breadClass + "'>");
        out.println("Bread and Pastry <span class='category-count'>(" + breadCount + ")</span></a>");
        
        // Sandwiches button
        int sandwichesCount = categoryMap.getOrDefault("Sandwiches", Collections.emptyList()).size();
        String sandwichesClass = "Sandwiches".equals(category) ? " active" : "";
        out.println("<a href='MenuServlet?category=Sandwiches' class='category-btn" + sandwichesClass + "'>");
        out.println("Sandwiches <span class='category-count'>(" + sandwichesCount + ")</span></a>");
        
        out.println("</div>");

        // Filter Container
        out.println("<div class='filter-container'>");
        out.println("<form method='get' action='MenuServlet' style='display: flex; gap: 15px; align-items: center; flex-wrap: wrap;'>");
        
        // Search
        out.println("<input type='text' name='search' class='search-box' placeholder='Search items...' value='" + (search != null ? search : "") + "'>");
        
        // Caffeine Filter
        out.println("<select name='caffeine' class='filter-select'>");
        out.println("<option value=''>All Caffeine Levels</option>");
        out.println("<option value='0'" + ("0".equals(caffeineFilter) ? " selected" : "") + ">Caffeine-Free</option>");
        out.println("<option value='1'" + ("1".equals(caffeineFilter) ? " selected" : "") + ">Low Caffeine</option>");
        out.println("<option value='2'" + ("2".equals(caffeineFilter) ? " selected" : "") + ">Medium Caffeine</option>");
        out.println("<option value='3'" + ("3".equals(caffeineFilter) ? " selected" : "") + ">High Caffeine</option>");
        out.println("</select>");
        
        // Sort Options
        out.println("<select name='sort' class='filter-select'>");
        out.println("<option value=''>Default Sorting</option>");
        out.println("<option value='caffeine'" + ("caffeine".equals(sort) ? " selected" : "") + ">By Caffeine Level</option>");
        out.println("<option value='price_asc'" + ("price_asc".equals(sort) ? " selected" : "") + ">Price: Low to High</option>");
        out.println("<option value='price_desc'" + ("price_desc".equals(sort) ? " selected" : "") + ">Price: High to Low</option>");
        out.println("<option value='popularity'" + ("popularity".equals(sort) ? " selected" : "") + ">By Popularity</option>");
        out.println("<option value='name'" + ("name".equals(sort) ? " selected" : "") + ">By Name</option>");
        out.println("</select>");
        
        out.println("<input type='submit' value='Apply Filters' class='filter-btn'>");
        if (category != null) {
            out.println("<input type='hidden' name='category' value='" + category + "'>");
        }
        out.println("</form>");
        out.println("</div>");

        List<MenuItem> displayItems = getFilteredItems(category, search, caffeineFilter);
        applySorting(displayItems, sort);

        // Display filtered/sorted items
        out.println("<div class='items-display-area'>");
        for (MenuItem item : displayItems) {
            // Make entire item box clickable by wrapping in <a> tag
            out.println("<a href='ProductDetail?name=" + java.net.URLEncoder.encode(item.getName(), "UTF-8") + "' class='item-box-link'>");
            out.println("<div class='item-box'>");
            String imgFile = getImageFile(item);
            out.println("<img src='images/menu/" + imgFile + "' alt='" + item.getName() + "'>");
            out.println("<div class='item-name'>" + item.getName().toUpperCase() + "</div>");
            out.println("<div class='item-price'>₱" + item.getPrice() + "</div>");
            out.println("<div class='caffeine-badge caffeine-" + getCaffeineClass(item.getCaffeineLevel()) + "'>");
            out.println(item.getCaffeineLevelText() + "</div>");
            out.println("<div class='item-popularity'>★ " + item.getPopularity() + " popularity</div>");
            out.println("</div>");
            out.println("</a>");  // Close the <a> tag
        }
        out.println("</div>");

        out.println("</body></html>");
    }

    private List<MenuItem> getFilteredItems(String category, String search, String caffeineFilter) {
        List<MenuItem> filtered = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            // Search across all items
            filtered = menu.stream()
                .filter(item -> item.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        } else if (category != null && !category.isEmpty()) {
            // Filter by category using hash map
            filtered = new ArrayList<>(categoryMap.getOrDefault(category, Collections.emptyList()));
        } else {
            // Show all items
            filtered = new ArrayList<>(menu);
        }

        // Apply caffeine filter
        if (caffeineFilter != null && !caffeineFilter.isEmpty()) {
            int caffeineLevel = Integer.parseInt(caffeineFilter);
            filtered = filtered.stream()
                .filter(item -> item.getCaffeineLevel() == caffeineLevel)
                .collect(Collectors.toList());
        }

        return filtered;
    }

    private void applySorting(List<MenuItem> items, String sort) {
        if (sort == null || sort.isEmpty()) return;

        switch (sort) {
            case "caffeine":
                List<MenuItem> sortedByCaffeine = countingSortByCaffeineLevel(items);
                items.clear();
                items.addAll(sortedByCaffeine);
                break;
            case "price_asc":
                mergeSortByPrice(items, 0, items.size() - 1, true);
                break;
            case "price_desc":
                mergeSortByPrice(items, 0, items.size() - 1, false);
                break;
            case "popularity":
                heapSortByPopularity(items);
                Collections.reverse(items);
                break;
            case "name":
                quickSortByName(items, 0, items.size() - 1);
                break;
        }
    }

    private String getImageFile(MenuItem item) {
        String categoryFolder = item.getCategory().toLowerCase().replace(" ", "-").replace("&", "and");
        String nameFile = item.getName().toLowerCase().replace(" ", "-") + ".png";
        return categoryFolder + "/" + nameFile;
    }

    private String getCaffeineClass(int caffeineLevel) {
        switch (caffeineLevel) {
            case 3: return "high";
            case 2: return "medium";
            case 1: return "low";
            case 0: return "free";
            default: return "free";
        }
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}