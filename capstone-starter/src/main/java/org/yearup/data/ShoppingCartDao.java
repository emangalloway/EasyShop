package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);//Read
    ShoppingCartItem getItemInsideCart(int userId, int productId);//Read Single Item
    ShoppingCart addProduct(int productId, int userId);//Create
    ShoppingCart addProductWithQty(int productId, int userId, int quantity);//Create
    ShoppingCart updateQuantity(int productId, int newQuantity, int userId);//Update
    ShoppingCart deleteItem(int productId, int userId);//Delete
    ShoppingCart clearOutCart(int userId);//Removes all items
//clear cart
    //another add product same param just add qty
    //check cart for product param prod id and user id
    //check qty for certain product
}
