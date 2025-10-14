package cafemenu;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/ProductDetail")
public class ProductDetailServlet extends HttpServlet {
    
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            // Load MySQL driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            
            String url = "jdbc:mysql://localhost:3306/coffeemenu";
            String username = "root";
            String password = "Catsarecute!:3";
            
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Product Detail: Database connected successfully!");
            
        } catch (Exception e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productName = req.getParameter("name");
        
        if (productName == null || productName.trim().isEmpty()) {
            resp.sendRedirect("MenuServlet");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String sql = "SELECT item_name, description, price FROM menu_items WHERE item_name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("item_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");

                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>" + name + " - Coffee House</title>");
                out.println("<link href='https://db.onlinewebfonts.com/c/e02bdd9828bed1e2490bdf94662dbba8?family=Arial+Nova+Cond' rel='stylesheet'>");
                out.println("<link rel='stylesheet' type='text/css' href='styles.css'>");
                out.println("<style>");
                out.println("body { font-family: 'Arial Nova Cond', sans-serif; background-color: #fff; margin: 0; padding: 0; text-align: center; }");
                out.println(".product-container { margin-top: 180px; }");
                out.println(".product-name { font-size: 60px; color: #322C2B; font-weight: bold; }");
                out.println(".product-description { font-size: 25px; color: #55463c; margin: 20px auto; width: 60%; }");
                out.println(".product-price { font-size: 40px; color: #9A3B3B; font-weight: bold; margin-top: 20px; }");
                out.println(".back-btn { display: inline-block; margin-top: 30px; padding: 15px 40px; background: #55463c; color: white; text-decoration: none; border-radius: 25px; font-size: 25px; }");
                out.println(".back-btn:hover { background: #322C2B; }");
                out.println("</style></head><body>");

                // HEADER (same as homepage, but without "Menu (35)")
                out.println("<div class='header-wrapper'>");
                out.println("  <div class='header'>");
                out.println("    <div class='header-left'>");
                out.println("      <a href='index.html#main'>Main</a>");
                out.println("    </div>");
                out.println("    <div class='header-right'>");
                out.println("      <a href='index.html#bestsellers'>Best Sellers</a>");
                out.println("    </div>");
                out.println("  </div>");
                out.println("</div>");

                // PRODUCT DETAILS
                out.println("<div class='product-container'>");
                out.println("<h1 class='product-name'>" + name + "</h1>");
                out.println("<p class='product-description'>" + description + "</p>");
                out.println("<div class='product-price'>₱" + price + "</div>");
                out.println("<a href='MenuServlet' class='back-btn'>← Back to Menu</a>");
                out.println("</div>");

                out.println("</body></html>");
            } else {
                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Product Not Found</title></head><body>");
                out.println("<h1>Product Not Found</h1>");
                out.println("<p>The product '" + productName + "' was not found in our menu.</p>");
                out.println("<a href='MenuServlet'>← Back to Menu</a>");
                out.println("</body></html>");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            out.println("<!DOCTYPE html><html><body>");
            out.println("<h1>Error loading product details</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='MenuServlet'>Back to Menu</a>");
            out.println("</body></html>");
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
