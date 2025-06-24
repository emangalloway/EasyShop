package org.yearup.data.mysql;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{

    @Autowired
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String query = """
                SELECT * FROM categories
                """;
        try(Connection connection = getConnection();
            PreparedStatement getAllStatement = connection.prepareStatement(query);
            ResultSet resultSet = getAllStatement.executeQuery()) {

            if (resultSet.next()){
                do {
//                   int category_id = resultSet.getInt("category_id");
//                    String name = resultSet.getString("name");
//                    String description = resultSet.getString("description");

                    Category category = mapRow(resultSet);

                    categories.add(category);
                }while (resultSet.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String getByIdQuery = """
                SELECT * FROM categories WHERE category_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement getByIdStatement = connection.prepareStatement(getByIdQuery)) {
            getByIdStatement.setInt(1,categoryId);
            try(ResultSet resultSet = getByIdStatement.executeQuery()) {
                if (resultSet.next()){
                    int category_id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    String description = resultSet.getString(3);
                    Category createdCategory = new Category(category_id,name,description);
                    return createdCategory;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        String createQuery = """
                INSERT INTO categories (name,description) VALUES (?,?)
                """;
        try(Connection connection = getConnection();
        PreparedStatement createStatement = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, category.getName());
            createStatement.setString(2, category.getDescription());
            int affectedRows = createStatement.executeUpdate();
            if (affectedRows == 0){
                throw new SQLException("Category creation failed, no rows affected");
            }
            try(ResultSet genKeys = createStatement.getGeneratedKeys()) {
                if (genKeys.next()){
                    //note genKeys is
                  int generatedID = genKeys.getInt("category_id");
                  category.setCategoryId(generatedID);
                }else {
                    throw new SQLException("Creation of category failed, no ID found");
                }
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category) {
        String updateQuery = """
                UPDATE categories SET name = ?,description = ? WHERE category_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, category.getName());
            updateStatement.setString(2, category.getDescription());
            updateStatement.setInt(3, categoryId);
            updateStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int categoryId) {
        String deleteQuery= """
                DELETE FROM categories WHERE category_id = ?
                """;

        try(Connection connection = getConnection();
        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1,categoryId);
            deleteStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
