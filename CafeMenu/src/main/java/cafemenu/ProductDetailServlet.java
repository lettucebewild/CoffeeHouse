package cafemenu;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/ProductDetail")
public class ProductDetailServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/coffee_house_db";
            String username = "root";
            String password = "Wsqk@2jej76";

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Product Detail: Database connected successfully!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String productName = req.getParameter("name");

        if (productName == null || productName.trim().isEmpty()) {
            resp.sendRedirect("MenuServlet");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String sql = "SELECT item_name, description, price, category_id, subcategory_id, image_url, caffeine_level, popularity " +
                         "FROM menu_items WHERE item_name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("item_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int categoryId = rs.getInt("category_id");
                int subCategoryId = rs.getInt("subcategory_id");
                String imageUrl = rs.getString("image_url");
                int caffeineLevel = rs.getInt("caffeine_level"); 
                int popularity = rs.getInt("popularity");        

                String categoryName = getCategoryName(categoryId);
                String subCategoryName = getSubCategoryName(subCategoryId);
                String caffeineText = getCaffeineText(caffeineLevel);

                String imagePath = req.getContextPath() + "/images/menu/" + imageUrl;

                // Convert popularity to stars
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < popularity; i++) stars.append("★");
                for (int i = popularity; i < 5; i++) stars.append("☆");

                // HTML Output
                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'><head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                out.println("<title>" + name + " - Coffee House</title>");
                out.println("<link href='https://db.onlinewebfonts.com/c/e02bdd9828bed1e2490bdf94662dbba8?family=Arial+Nova+Cond' rel='stylesheet'>");
                out.println("<link rel='stylesheet' href='styles.css'>");
                out.println("</head><body>");

                // Header
                out.println("<div class='header-wrapper'><div class='header'>");
                out.println("<div class='header-left'><a href='index.html#main'>≡</a></div>");
                out.println("<div class='header-right'><a href='index.html#bestsellers'>Best Sellers</a></div>");
                out.println("</div></div>");

                // Product Detail Section
                out.println("<section class='product-detail-container'><div class='product-card'>");
                out.println("<img src='" + imagePath + "' alt='" + name + "'>");
                out.println("<div class='product-info'>");
                out.println("<h2>" + name.toUpperCase() + "</h2>");
                
                // Rating & caffeine
                out.println("<div class='product-rating'>");
                out.println("<div class='rating-stars'>" + stars + "</div>");
                out.println("<div class='rating-score'>" + popularity + ".0</div>");
                out.println("<div class='caffeine-level' data-level='" + caffeineLevel + "'>" + caffeineText + "</div>");                out.println("</div>");

                // Description
                out.println("<p class='product-description'>" + description + "</p>");

                // Price table
                out.println("<table class='product-sizes'>");
                out.println("<tr><th>Small</th><td>₱" + (int) price + "</td></tr>");
                out.println("<tr><th>Medium</th><td>₱" + (int)(price + 20) + "</td></tr>");
                out.println("<tr><th>Large</th><td>₱" + (int)(price + 40) + "</td></tr>");
                out.println("</table>");
                out.println("</div>"); // .product-info

                // Price circle
                out.println("<div class='price-circle'><span>Price starts</span><p>₱" + price + "</p></div>");
                out.println("</div></section>");

                // Back button
                out.println("<div style='text-align:center; margin-top:50px;'>");
                out.println("<a href='MenuServlet' class='back-button'>← Back to Menu</a>");
                out.println("</div>");

                out.println("</body></html>");

            } else {
                out.println("<!DOCTYPE html><html><head><title>Product Not Found</title></head><body>");
                out.println("<h1>Product Not Found</h1>");
                out.println("<p>The product '" + productName + "' was not found.</p>");
                out.println("<a href='MenuServlet'>← Back to Menu</a>");
                out.println("</body></html>");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<!DOCTYPE html><html><body>");
            out.println("<h1>Error loading product details</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='MenuServlet'>Back to Menu</a>");
            out.println("</body></html>");
        }
    }

    private String getCategoryName(int categoryId) {
        return switch (categoryId) {
            case 1 -> "Coffee";
            case 2 -> "Tea";
            case 3 -> "Caffeine-Free Drinks";
            case 4 -> "Bread and Pastry";
            case 5 -> "Sandwiches";
            default -> "Unknown";
        };
    }

    private String getSubCategoryName(int subCategoryId) {
        return switch (subCategoryId) {
            case 1 -> "Hot";
            case 2 -> "Iced";
            default -> "";
        };
    }

    private String getCaffeineText(int caffeineLevel) {
        return switch (caffeineLevel) {
            case 0 -> "Caffeine-Free";
            case 1 -> "Low";
            case 2 -> "Medium";
            case 3 -> "High";
            default -> "Unknown";
        };
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try { 
                connection.close(); 
                System.out.println("🔒 Database connection closed."); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
