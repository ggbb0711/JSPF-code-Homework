<%-- 
    Document   : update
    Created on : Jan 1, 2025, 9:53:59 AM
    Author     : NGHIA
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entities.Product"%>
<%@page import="java.util.List"%>
<%@page import="entities.Category"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Page</title>
    </head>
    <body>
        <h1>Update product</h1>
        <%
            Product oldProduct = (Product) request.getAttribute("oldProduct");
            if(oldProduct==null){
                String msg = (String) request.getAttribute("msg");
        %>
        <h3><%= msg%></h3>
        <%
            }
            else{
        %>
            <h1>Old product:</h1>
            <table>
                <tr>
                    <th>Product Name</th>
                    <th>Price</th>
                    <th>Product Year</th>
                    <th>Image</th>
                    <th>Category</th>
                </tr>
                <tr>
                    <td><%= oldProduct.getName()%></td>
                    <td><%= oldProduct.getPrice()%></td>
                    <td><%= oldProduct.getProductYear()%></td>
                    <td><img src="<%= oldProduct.getImage()%>" alt="product image" width="100" height="100"></td>
                    <td><%= oldProduct.getCategory().getName()%></td>
                </tr>
            </table>
            <%
                String error = (String) request.getAttribute("msg");
            %>
            <%
                if (error != null) {
            %>
            <p><%=error%></p>
            <%
                }
            %>
            <form action="Product" method="POST">
                <label for="name">Product Name:</label>
                <input type="text" id="name" name="name" required value="<%=oldProduct.getName()%>"><br><br>

                <label for="price">Price:</label>
                <input type="number" id="price" name="price" step="0.01" required value="<%=oldProduct.getPrice()%>"><br><br>

                <label for="productYear">Product Year:</label>
                <input type="number" id="productYear" name="productYear" required value="<%=oldProduct.getProductYear()%>"><br><br>

                <label for="image">Image URL:</label>
                <input type="text" id="image" name="image" value="<%=oldProduct.getImage()%>"><br><br>

                <label for="category">Category:</label>
                <select id="category" name="category" required>
                    <%
                        List<Category> categories = (List<Category>) request.getAttribute("categories");
                        System.out.println(categories);
                        if (categories != null) {
                            for (Category category : categories) {
                                if(oldProduct.getCategory().getId()==category.getId()){
                                %>
                                    <option value="<%= category.getId()%>" selected="selected"><%= category.getName()%></option>
                                <%                                
                                }
                                else{
                                    %>
                                    <option value="<%= category.getId()%>"><%= category.getName()%></option>
                                    <%
                                }
                            }
                        }
                    %>
                </select><br><br>
                <input type="hidden" name="action" value="update">
                <input type='hidden' name='Id' value="<%=oldProduct.getId()%>">
                <button type="submit">Update Product</button>
            </form>
        <%
        }
        %>
    </body>
</html>
