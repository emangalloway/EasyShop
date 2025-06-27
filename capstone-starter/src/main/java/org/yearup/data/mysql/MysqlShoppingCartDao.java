package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

@Component
public class MysqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private final ProductDao productDao;
    private int userId;

    @Autowired
    public MysqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String getCartQry = """
                SELECT *
                FROM shopping_Cart sc
                JOIN products p ON sc.product_id = p.product_id
                WHERE user_id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement getCartStatement = connection.prepareStatement(getCartQry)) {
            getCartStatement.setInt(1, userId);
            try (ResultSet resultSet = getCartStatement.executeQuery()) {
                if (resultSet.next()) {
                    do {
                        int product_id = resultSet.getInt(2);
                        int quantity = resultSet.getInt(3);
                        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                        shoppingCartItem.setProduct(productDao.getById(product_id));
                        shoppingCartItem.setQuantity(quantity);
                        shoppingCart.add(shoppingCartItem);
                    } while (resultSet.next());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }//join product and shopping cart

    @Override
   public ShoppingCartItem getItemInsideCart(int userId, int productId) {
        ShoppingCartItem item = new ShoppingCartItem();
        String getItemInCartQry = """
                SELECT u.username,sc.product_id,sc.quantity,p.name,p.price,p.description
                FROM shopping_cart sc
                JOIN products p ON sc.product_id = p.product_id
                JOIN users u ON sc.user_id = u.user_id
                WHERE sc.user_id = ? AND product_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement getItemsInCartStatement = connection.prepareStatement(getItemInCartQry)) {
            getItemsInCartStatement.setInt(1,userId);
            getItemsInCartStatement.setInt(2,productId);
            try (ResultSet resultSet = getItemsInCartStatement.executeQuery()){
                if (resultSet.next()){
                        String username = resultSet.getString("username");
                        int product_ID = resultSet.getInt("product_id");
                        int amount = resultSet.getInt("quantity");
                        String productName = resultSet.getString("name");
                        BigDecimal productPrice = resultSet.getBigDecimal("price");
                        String productDescription = resultSet.getString("description");

                        User user = new User();
                        Product newProduct = new Product();

                        user.setUsername(username);
                        newProduct.setProductId(product_ID);
                        newProduct.setName(productName);
                        newProduct.setPrice(productPrice);
                        newProduct.setDescription(productDescription);
                        item.setProduct(newProduct);
                        item.setQuantity(amount);
                        return item;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ShoppingCart addProduct(int productId, int userId) {
        String addProductToCartQry = """
                    INSERT INTO shopping_cart (user_id,product_id,quantity) VALUES (?,?,1)
                    """;
        try (Connection connection = getConnection();
             PreparedStatement addProductToCartStatement = connection.prepareStatement(addProductToCartQry)) {
            addProductToCartStatement.setInt(1, userId);
            addProductToCartStatement.setInt(2, productId);

            int affectedRows = addProductToCartStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding item to cart fail, no rows affected");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getByUserId(userId);
    }

    @Override//Switch ShoppingCart item param to productId
    public ShoppingCart addProductWithQty(int productId, int userId,int quantity) {
        //Check if item is in cart already if exist update quantity, and if does not exist add product to cart
        ShoppingCartItem existingItem = getItemInsideCart(userId, productId);
        if (existingItem != null) {
            String qry = """
                    UPDATE shopping_cart SET quantity = quantity + ? WHERE user_id =? AND product_id = ?
                    """;
            try(Connection connection = getConnection();
            PreparedStatement updateQtyIfExistQryStatement = connection.prepareStatement(qry) ){
                updateQtyIfExistQryStatement.setInt(1,quantity);
                updateQtyIfExistQryStatement.setInt(2,userId);
                updateQtyIfExistQryStatement.setInt(3,productId);

                int affectedRows = updateQtyIfExistQryStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Adding item to cart fail, no rows affected");
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        } else {
            String addProductToCartQry = """
                    INSERT INTO shopping_cart (user_id,product_id,quantity) VALUES (?,?,?)
                    """;
            try (Connection connection = getConnection();
                 PreparedStatement addProductToCartStatement = connection.prepareStatement(addProductToCartQry)) {
                addProductToCartStatement.setInt(1, userId);
                addProductToCartStatement.setInt(2, productId);
                addProductToCartStatement.setInt(3, quantity);

                int affectedRows = addProductToCartStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Adding item to cart fail, no rows affected");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return getByUserId(userId);
    }

    @Override
   public ShoppingCart updateQuantity(int productId, int newQuantity, int userId) {
        String qtyUpdateQry = """
                 UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(qtyUpdateQry)) {
            updateStatement.setInt(1,newQuantity);
            updateStatement.setInt(2,userId);
            updateStatement.setInt(3,productId);
            updateStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return getByUserId(userId);

    }

    @Override
    public ShoppingCart deleteItem(int productId, int userId) {
        String removeFromCartQry = """
                 DELETE FROM shopping_cart WHERE product_id = ? AND user_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(removeFromCartQry)) {
            deleteStatement.setInt(1,productId);
            deleteStatement.setInt(2,userId);
            deleteStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return getByUserId(userId);
    }

    @Override
    public ShoppingCart clearOutCart(int userId) {
        String clearAllItemsQry = """
                DELETE FROM shopping_cart WHERE user_id = ?
                """;
        try(Connection connection = getConnection();
            PreparedStatement clearStatement = connection.prepareStatement(clearAllItemsQry)) {
            clearStatement.setInt(1,userId);
            clearStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return getByUserId(userId);
    }


}
