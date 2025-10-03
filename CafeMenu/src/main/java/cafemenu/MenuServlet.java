package cafemenu;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.*;

@WebServlet("/MenuServlet")
public class MenuServlet extends HttpServlet {

    private List<MenuItem> menu;

    @Override
    public void init() throws ServletException {
        menu = new ArrayList<>();
        
        // Coffee - Hot
        menu.add(new MenuItem("Latte", "Coffee", "Hot", "", 120));
        menu.add(new MenuItem("Honey Oat Milk Latte", "Coffee", "Hot", "", 130));
        menu.add(new MenuItem("Mocha Latte", "Coffee", "Hot", "", 140));
        menu.add(new MenuItem("Vanilla Latte", "Coffee", "Hot", "", 130));
        menu.add(new MenuItem("Cappuccino", "Coffee", "Hot", "", 110));
        menu.add(new MenuItem("Espresso", "Coffee", "Hot", "", 100));
        menu.add(new MenuItem("Americano", "Coffee", "Hot", "", 90));
        menu.add(new MenuItem("Hot Coffee", "Coffee", "Hot", "", 80));

        // Coffee - Iced
        menu.add(new MenuItem("Iced Latte", "Coffee", "Iced", "", 130));
        menu.add(new MenuItem("Iced Mocha Latte", "Coffee", "Iced", "", 140));
        menu.add(new MenuItem("Honey Oat Milk Iced Latte", "Coffee", "Iced", "", 130));
        menu.add(new MenuItem("Iced Vanilla Latte", "Coffee", "Iced", "", 130));
        menu.add(new MenuItem("Iced Cappuccino", "Coffee", "Iced", "", 120));
        menu.add(new MenuItem("Cold Brew Espresso", "Coffee", "Iced", "", 150));
        menu.add(new MenuItem("Cold Brew Mocha", "Coffee", "Iced", "", 160));
        menu.add(new MenuItem("Cold Brew", "Coffee", "Iced", "", 140));
        
        // Tea - Hot
        menu.add(new MenuItem("Hot Tea", "Tea", "Hot", "", 80));
        menu.add(new MenuItem("Chai Tea Latte", "Tea", "Hot", "", 120));
        menu.add(new MenuItem("Matcha Latte", "Tea", "Hot", "", 130));

        // Tea - Iced
        menu.add(new MenuItem("Iced Chai Tea Latte", "Tea", "Iced", "", 140));
        menu.add(new MenuItem("Iced Matcha Latte", "Tea", "Iced", "", 140));
        menu.add(new MenuItem("Iced Tea", "Tea", "Iced", "", 90));

        // Caffeine-Free Drinks - Hot
        menu.add(new MenuItem("Hot Chocolate", "Caffeine-Free Drinks", "Hot", "", 95));
        
        // Caffeine-Free Drinks - Iced
        menu.add(new MenuItem("Chocolate Milk", "Caffeine-Free Drinks", "Iced", "", 90));
        menu.add(new MenuItem("Lemonade", "Caffeine-Free Drinks", "Iced", "", 85));

        // Bread and Pastry
        menu.add(new MenuItem("Ensaymada Bread", "Bread and Pastry", "", "", 70));
        menu.add(new MenuItem("Chocolate Croissant", "Bread and Pastry", "", "", 80));
        menu.add(new MenuItem("Chocolate Chip Muffin", "Bread and Pastry", "", "", 75));
        menu.add(new MenuItem("Banana Nut Muffin", "Bread and Pastry", "", "", 70));
        menu.add(new MenuItem("Cheesecake Slice", "Bread and Pastry", "", "", 100));
        menu.add(new MenuItem("Chocolate Cake Slice", "Bread and Pastry", "", "", 100));

        // Sandwiches
        menu.add(new MenuItem("Chicken Sandwich", "Sandwiches", "", "", 120));
        menu.add(new MenuItem("Ham & Cheese Sandwich", "Sandwiches", "", "", 130));
        menu.add(new MenuItem("Egg Sandwich", "Sandwiches", "", "", 110));
        menu.add(new MenuItem("Beef Cheese Burger", "Sandwiches", "", "", 150));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String category = req.getParameter("category");

        resp.setContentType("text/html");
        resp.getWriter().println("<!DOCTYPE html>");
        resp.getWriter().println("<html><head><title>Coffee House</title>");
        resp.getWriter().println("<link href='https://db.onlinewebfonts.com/c/e02bdd9828bed1e2490bdf94662dbba8?family=Arial+Nova+Cond' rel='stylesheet'>");
        resp.getWriter().println("<link rel='stylesheet' href='styles.css'>");
        resp.getWriter().println("</head><body>");

        // Header
        resp.getWriter().println("<div class='header-wrapper'><div class='header'>");
        resp.getWriter().println("<div><a href='index.html'>Main</a></div>");
        resp.getWriter().println("<div class='header-right'>");
        resp.getWriter().println("<a href='index.html#bestsellers'>Best Sellers</a>");
        resp.getWriter().println("</div></div></div>");

        // Categories bar
        resp.getWriter().println("<div class='categories-bar'>");
        String[] allCategories = {"Coffee", "Tea", "Caffeine-Free Drinks", "Bread and Pastry", "Sandwiches"};
        for (int i = 0; i < allCategories.length; i++) {
            String c = allCategories[i];
            String activeClass = c.equalsIgnoreCase(category != null ? category : "") ? "active" : "";

            long count = menu.stream().filter(m -> m.getCategory().equalsIgnoreCase(c)).count();

            resp.getWriter().print("<a class='category-link " + activeClass + "' href='MenuServlet?category=" + c + "'>" 
                                    + c + " (" + count + ")</a>");

            if (i < allCategories.length - 1) {
                if (i == 2) resp.getWriter().print(" <br> "); 
                else resp.getWriter().print(" / ");
            }
        }

        // Filter items by category
        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem m : menu) {
            if (category == null || m.getCategory().equalsIgnoreCase(category)) {
                filtered.add(m);
            }
        }

        // Display items
        if (category == null) {
            resp.getWriter().println("<div class='menu-container'>");
            String[] categories = {"Coffee", "Tea", "Caffeine-Free Drinks", "Bread and Pastry", "Sandwiches"};
            for (String c : categories) {
                resp.getWriter().println("<div class='menu-box'>");
                resp.getWriter().println("<div class='menu-text'>" + c + "</div>");
                resp.getWriter().println("</div>");
            }
            resp.getWriter().println("</div>");
        } else {
            resp.getWriter().println("<div class='items-display-area'>");
            for (MenuItem m : filtered) {
                resp.getWriter().println("<div class='item-box'>");
                String imgFile = m.getCategory().equalsIgnoreCase("Bread and Pastry") ? "Bread.png"
                               : m.getCategory().replace(" & ", "") + ".png";
                resp.getWriter().println("<img src='images/menu/" + imgFile + "' alt='" + m.getName() + "'>");
                resp.getWriter().println("<div class='item-name'>" + m.getName().toUpperCase() + "</div>");
                resp.getWriter().println("</div>");
            }
            resp.getWriter().println("</div>");
        }
    }
}
