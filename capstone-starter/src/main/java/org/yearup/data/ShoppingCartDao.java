/*package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);//Read
    ShoppingCartItem getItemInsideCart(int userId, int productId);//Read Single Item
    void addCartItem(ShoppingCartItem item, int userId);//Create
    void updateQuantity(int productId, int newQuantity, int userId);//Update
    void deleteItem(int productId, int userId);//Delete

    // add additional method signatures here
}
