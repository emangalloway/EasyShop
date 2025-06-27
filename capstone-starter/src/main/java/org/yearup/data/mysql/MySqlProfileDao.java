package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;
import org.yearup.models.User;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId) {
        String qry = """
                SELECT * FROM profiles WHERE user_id = ?
                """;
        try(Connection connection = getConnection();
        PreparedStatement getUserStatement = connection.prepareStatement(qry)) {
            getUserStatement.setInt(1,userId);
            try (ResultSet resultSet = getUserStatement.executeQuery()){
                if (resultSet.next()){
                    int user_Id = resultSet.getInt(1);
                    String firstName = resultSet.getString(2);
                    String lastName = resultSet.getString(3);
                    String phone = resultSet.getString(4);
                    String email = resultSet.getString(5);
                    String address = resultSet.getString(6);
                    String city = resultSet.getString(7);
                    String state = resultSet.getString(8);
                    String zip = resultSet.getString(9);

                    Profile newProfile = new Profile(user_Id,firstName,lastName,phone,email,address,city,state,zip);
                    return newProfile;
                }

            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    public Profile update(int userId, Profile profile){
        String sql = """
                UPDATE profiles SET first_name = ?, last_name = ?, phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ? WHERE user_id = ?
                """;
        try (Connection connection = getConnection())    {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, profile.getFirstName());
            ps.setString(2, profile.getLastName());
            ps.setString(3, profile.getPhone());
            ps.setString(4, profile.getEmail());
            ps.setString(5, profile.getAddress());
            ps.setString(6, profile.getCity());
            ps.setString(7, profile.getState());
            ps.setString(8, profile.getZip());
            ps.setInt(9, userId);
            ps.executeUpdate();

            //need to get profile create and get attributes of profile so i can return them in controller
            try (ResultSet resultSet = ps.executeQuery()){
                if (resultSet.next()){
                    int id = resultSet.getInt(1);
                    String firstName = resultSet.getString(2);
                    String lastName = resultSet.getString(3);
                    String phone = resultSet.getString(4);
                    String email = resultSet.getString(5);
                    String address = resultSet.getString(6);
                    String city = resultSet.getString(7);
                    String state = resultSet.getString(8);
                    String zip = resultSet.getString(9);
                    Profile newProfile = new Profile(id,firstName,lastName,phone,email,address,city,state,zip);
                    return newProfile;
                }
            }
            return null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
