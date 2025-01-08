import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

public class NewServlet2 extends HttpServlet {

    // Process request method
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Retrieve user credentials from the request parameters
        String user = request.getParameter("t1");
        String psw = request.getParameter("t2");

        try (PrintWriter out = response.getWriter()) {
            // Validate the input parameters
            if (user == null || user.trim().isEmpty() || psw == null || psw.trim().isEmpty()) {
                out.println("<h2>Error: Username and Password must not be empty!</h2>");
                return;
            }

            // Establish database connection
            String dbUrl = "jdbc:mysql://localhost:3306/cms"; // Replace with your actual DB URL
            String dbUser = "root"; // Replace with your actual DB username
            String dbPassword = ""; // Replace with your actual DB password

            try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 PreparedStatement ps = con.prepareStatement("DELETE FROM register WHERE user = ? AND password = ?")) {

                // Set parameters for the prepared statement
                ps.setString(1, user);
                ps.setString(2, psw); // In production, you should hash the password!

                // Execute the DELETE operation
                int result = ps.executeUpdate();

                // Check if the account was deleted and respond accordingly
                if (result > 0) {
                    out.println("<h2>Account successfully deleted.</h2>");
                    // Optionally redirect to another page, e.g., a success page
                    // response.sendRedirect("success.html");
                } else {
                    out.println("<h2>Error: Account not found or incorrect password!</h2>");
                }
            } catch (SQLException e) {
                // Log the error and send a generic error response
                log("Database error occurred", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred.");
            }
        } catch (Exception e) {
            // Catch unexpected exceptions and log them
            log("Unexpected error occurred", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    // Handle GET requests (same as POST)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Handle POST requests (same as GET)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet for deleting a user account from the database.";
    }
}
