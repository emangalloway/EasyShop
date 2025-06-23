package org.yearup.data.mysql;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        String query = """
                SELECT * FROM categories
                """;
        try(Connection connection = getConnection();
            PreparedStatement getAllStatement = connection.prepareStatement(query);
            ResultSet resultSet = getAllStatement.executeQuery()) {

            if (resultSet.next()){
                do {
                   int category_id = resultSet.getInt("categoryId");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    Category category = new Category(category_id,name,description);
                    categories.add(category);
                }while (resultSet.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // get all categories
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
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
                    Category category = new Category(category_id,name,description);
                    return category;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // get category by id
        return null;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
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
