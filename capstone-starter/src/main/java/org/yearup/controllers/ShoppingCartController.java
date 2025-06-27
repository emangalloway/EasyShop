package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping("")
    @PreAuthorize(value = "isAuthenticated()")
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping("/products/{productId}")
    @PreAuthorize(value = "isAuthenticated()")
    public ShoppingCart updateProductInCart(@PathVariable int productId, Principal principal){
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            if (user == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User does not exist");
            return shoppingCartDao.addProduct(productId,userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops.. our bad.");
        }
    }



    @PutMapping("/products/{productId}")
    @PreAuthorize(value = "isAuthenticated()")
    public ShoppingCart addProductToCart(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal){
        try {
            String userName = principal.getName();
            int userId = userDao.getByUserName(userName).getId();
            ShoppingCartItem existingItem = shoppingCartDao.getItemInsideCart(userId,productId);
            if (existingItem == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product in cart");
            }
            shoppingCartDao.updateQuantity(productId, item.getQuantity(), userId);
            return shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops.. our bad");
        }
    }


    @DeleteMapping("")
    @PreAuthorize(value = "isAuthenticated()")
    public ShoppingCart clearCart(Principal principal){
        try {
            String userName = principal.getName();
            int userId = userDao.getByUserName(userName).getId();
            return shoppingCartDao.clearOutCart(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops.. our bad");
        }
    }

}
