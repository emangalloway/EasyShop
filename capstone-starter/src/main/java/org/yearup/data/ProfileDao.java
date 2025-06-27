package org.yearup.data;


import org.yearup.models.Profile;
import org.yearup.models.User;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getByUserId(int userId);
    Profile update(int userId, Profile profile);
}
