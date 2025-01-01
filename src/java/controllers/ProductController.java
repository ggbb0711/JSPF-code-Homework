/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import dao.CategoryDAO;
import dao.ProductDAO;
import dto.CreateProductDTO;
import dto.SearchProductDTO;
import dto.UpdateProductDTO;
import entities.Category;
import entities.Product;
import exceptions.InvalidDataException;
import exceptions.ValidationException;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author vothimaihoa
 */
public class ProductController extends HttpServlet {

    private final String LIST = "Product";
    private final String PREPARE_CREATE = "Product?action=prepare-add";
    private final String PREPARE_UPDATE = "Product?action=prepare-update";
    private final String LIST_VIEW = "view/product/list.jsp";
    private final String CREATE_VIEW = "view/product/create.jsp";
    private final String UPDATE_VIEW = "view/product/update.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        String action = request.getParameter("action");
        System.out.println(action);
        if (action == null) {
            list(request, response, categoryDAO, productDAO);
        } else {
            switch (action) {
                case "prepare-add":
                    prepareAdd(request, response, categoryDAO);
                    break;
                case "add":
                    add(request, response, categoryDAO, productDAO);
                    break;
                case "prepare-update":
                    prepareUpdate(request,response,categoryDAO,productDAO);
                    break;
                case "update":
                    update(request,response,categoryDAO,productDAO);
                    break;
                case "delete":
                    delete(request,response,productDAO);
                    break;
                default:
                    list(request, response, categoryDAO, productDAO);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void list(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO, ProductDAO productDAO)
            throws ServletException, IOException {
        // get search criterias
        String categoryIdRaw = request.getParameter("category");
        String productName = request.getParameter("productName");
        String productPriceRaw = request.getParameter("productPrice");
        try {
            // get category list for drop down
            List<Category> categories = categoryDAO.list();
            request.setAttribute("categories", categories);



            // validate search fields only when search criterias a string
            Integer categoryId = null;
            Float productPrice = null;
            if ((categoryIdRaw != null && !categoryIdRaw.isEmpty()) || (productPriceRaw!=null&&!productPriceRaw.isEmpty())) {
                SearchProductDTO searchDTO = new SearchProductDTO(categoryIdRaw, productName,productPriceRaw);
                searchDTO.validate();
                if(categoryIdRaw!=null && !categoryIdRaw.isEmpty())categoryId = Integer.parseInt(categoryIdRaw);
                if(productPriceRaw!=null&&!productPriceRaw.isEmpty())productPrice = Float.parseFloat(productPriceRaw);
            }
            // get search data
            List<Product> list = productDAO.list(productName, categoryId,productPrice);
            if (list != null && !list.isEmpty()) {
                request.setAttribute("products", list);
            } else {
                throw new InvalidDataException("No Products Found!");
            }
            
            // hold search criteria on search bar for next request
            request.setAttribute("productName", productName);
            request.setAttribute("category", categoryIdRaw);
            request.setAttribute("productPrice",productPriceRaw);
        }
        catch (ValidationException | InvalidDataException ex) {
            request.setAttribute("msg", ex.getMessage());
        } finally {
            request.getRequestDispatcher(LIST_VIEW).forward(request, response);
        }
    }

    private void prepareAdd(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO) throws ServletException, IOException {
        List<Category> categories = categoryDAO.list();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
    }

    private void add(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO, ProductDAO productDAO) throws ServletException, IOException {
        String name = request.getParameter("name");
        String price = request.getParameter("price");
        String productYear = request.getParameter("productYear");
        String image = request.getParameter("image");
        String categoryId = request.getParameter("category");
        
        CreateProductDTO productDTO = new CreateProductDTO(name, price, productYear, image, categoryId);
        try {
            productDTO.validate();

            Category category = categoryDAO.getById(Integer.parseInt(categoryId));
            if (category == null) {
                throw new InvalidDataException("Category not found!");
            }

            // call DAO
            Product product = new Product(name, Float.parseFloat(price), Integer.parseInt(productYear), image, category);
            boolean isOk = productDAO.create(product);
            if (!isOk) {
                throw new InvalidDataException("Cannot save product to database!");
            } else {
                response.sendRedirect(LIST);
            }
        }

        catch (ValidationException | InvalidDataException ex) {
            request.setAttribute("msg", ex.getMessage());
            request.getRequestDispatcher(PREPARE_CREATE).forward(request, response);
        }
    }
    
    private void prepareUpdate(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO, ProductDAO productDAO) throws ServletException, IOException {
        List<Category> categories = categoryDAO.list();
        request.setAttribute("categories", categories);
        int id = Integer.parseInt(request.getParameter("Id"));
        System.out.println(id);
        Product oldProduct = productDAO.getById(id);
        request.setAttribute("oldProduct", oldProduct);
        RequestDispatcher dispatcher = request.getRequestDispatcher(UPDATE_VIEW);
        dispatcher.forward(request, response);
    }
    
    private void update(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO, ProductDAO productDAO) throws ServletException, IOException{
        String id = request.getParameter("Id");
        String name = request.getParameter("name");
        String price = request.getParameter("price");
        String productYear = request.getParameter("productYear");
        String image = request.getParameter("image");
        String categoryId = request.getParameter("category");
        
        UpdateProductDTO productDTO = new UpdateProductDTO(id,name, price, productYear, image, categoryId);
        try {
            productDTO.validate();

            Category category = categoryDAO.getById(Integer.parseInt(categoryId));
            if (category == null) {
                throw new InvalidDataException("Category not found!");
            }

            // call DAO
            Product product = new Product(Integer.parseInt(id),name, Float.parseFloat(price), Integer.parseInt(productYear), image, category);
            boolean isOk = productDAO.update(product);
            if (!isOk) {
                throw new InvalidDataException("Cannot update product!");
            } else {
                response.sendRedirect(LIST);
            }
        }

        catch (ValidationException | InvalidDataException ex) {
            request.setAttribute("msg", ex.getMessage());
            request.getRequestDispatcher(PREPARE_UPDATE+"&Id="+id).forward(request, response);
        }
    }
    
    private void delete(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws ServletException, IOException{
        int deletedId = Integer.parseInt(request.getParameter("Id"));
        System.out.println(deletedId);
        System.out.println(productDAO.delete(deletedId));
        response.sendRedirect(LIST);
    }
}
