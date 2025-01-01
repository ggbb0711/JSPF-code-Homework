/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entities.Category;
import entities.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

/**
 *
 * @author vothimaihoa
 */
public class ProductDAO {

    private String constructSearchQuery(String productName, Integer categoryId, Float price) {
        String sql = " SELECT p.id, p.name, p.price, p.product_year, p.image, p.category_id, c.name as category_name "
                + " from Product p join Category c on p.category_id = c.id where 1 = 1 ";
        if (categoryId != null) {
            sql += " and p.category_id = ? ";
        }
        if (productName != null && !productName.trim().isEmpty()) {
            sql += " and p.name like ? ";
        }
        if(price!=null){
            sql += " and p.price = ? ";
        }

        return sql;
    }
  
    public List<Product> list(String productName, Integer categoryId, Float price) {
        List<Product> list = null;
        String sql = constructSearchQuery(productName, categoryId, price);
        try {
            Connection con = DBUtils.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            // set params value to query
            int paramIndex = 1;
            if (categoryId != null) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (productName != null
                    && !productName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + productName + "%");
            }
            if(price!=null){
                //Using setFloat would create number with incorrect precision
                ps.setBigDecimal(paramIndex++, BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP));
            } 

            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                list = new ArrayList<>();
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getFloat("price"));
                    p.setProductYear(rs.getInt("product_year"));
                    p.setImage(rs.getString("image"));

                    Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"));
                    p.setCategory(category);
                    list.add(p);
                }
            }
            con.close();
        } catch (ClassNotFoundException ex) {
            System.out.println("DBUtils not found.");
        } catch (SQLException ex) {
            System.out.println("SQL Exception in getting list of products. Details: ");
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<Product> list(String productName, Integer categoryId){
        return list(productName,categoryId,null);
    }

    public Product getById(int id) {
        Product p = null;
        String sql = " SELECT p.id, p.name, price, product_year, image, p.category_id, c.name as category_name "
                + " from Product p join Category c on p.category_id = c.id "
                + " where p.id = ?";
        try {
            Connection con = DBUtils.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getFloat("price"));
                    p.setProductYear(rs.getInt("product_year"));
                    p.setImage(rs.getString("image"));

                    Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"));
                    p.setCategory(category);
                }
            }
            con.close();
        } catch (ClassNotFoundException ex) {
            System.out.println("DBUtils not found.");
        } catch (SQLException ex) {
            System.out.println("SQL Exception in getting product by id. Details: ");
            ex.printStackTrace();
        }
        return p;
    }

    public boolean create(Product newProduct) {
        boolean status = false;
        String sql = "INSERT INTO Product(name, price, product_year, image, category_id) "
                + " VALUES(?, ROUND(?, 2), ?, ?, ?)";

        try {
            Connection con = DBUtils.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newProduct.getName());
            ps.setFloat(2, newProduct.getPrice());
            ps.setInt(3, newProduct.getProductYear());
            ps.setString(4, newProduct.getImage());
            ps.setInt(5, newProduct.getCategory().getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                status = true;
            }
            con.close();
        } catch (ClassNotFoundException ex) {
            System.out.println("DBUtils not found!");
        } catch (SQLException ex) {
            System.out.println("SQL Exception in inserting new product. Details: ");
            ex.printStackTrace();
        }
        return status;
    }
    
    public boolean delete(int id){
        boolean status = false;
        String sql = "DELETE FROM Product WHERE id=?";
        
        try{
            Connection con = DBUtils.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            status = ps.executeUpdate()>0;
            con.close();
        }
        catch (ClassNotFoundException ex) {
            System.out.println("DBUtils not found!");
        } catch (SQLException ex) {
            System.out.println("SQL Exception in deleting product with id="+id+". Details: ");
            ex.printStackTrace();
        }
        return status;
    }
    
    public boolean update(Product updatedProduct){
        boolean status = false;
        String sql = "UPDATE Product SET name=?,price=ROUND(?, 2),image=?,product_year=?,category_id=? WHERE id=?";
        try{
            Connection con = DBUtils.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, updatedProduct.getName());
            ps.setFloat(2, updatedProduct.getPrice());
            ps.setString(3, updatedProduct.getImage());
            ps.setInt(4, updatedProduct.getProductYear());
            ps.setInt(5, updatedProduct.getCategory().getId());
            ps.setInt(6, updatedProduct.getId());
            status = ps.executeUpdate()>0;
            con.close();
        }
        catch (ClassNotFoundException ex) {
            System.out.println("DBUtils not found!");
        } catch (SQLException ex) {
            System.out.println("SQL Exception in deleting product with id="+updatedProduct.getId()+". Details: ");
            ex.printStackTrace();
        }
        return status;
    }
}
