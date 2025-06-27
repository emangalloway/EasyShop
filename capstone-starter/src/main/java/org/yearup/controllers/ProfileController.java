package org.yearup.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {
private ProfileDao profileDao;
private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping("")
    public Profile getProfile(Principal principal){
        String userName = principal.getName();
        int userId = userDao.getByUserName(userName).getId();
        return profileDao.getByUserId(userId);
    }

    @PutMapping("")
    public Profile updateProfile(@RequestBody Profile profile, Principal principal){
            String userName = principal.getName();
            int userId = userDao.getIdByUsername(userName);
            profile.setUserId(userId);//Make sure userId matched logged in person
             return profileDao.update(userId,profile);
    }

   /* @PostMapping("{id}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Profile createProfile(@RequestBody Profile profile, Principal principal){
        try {
            String userName = principal.getName();
            int userId = userDao.getIdByUsername(userName);
            profile.setUserId(userId);
            return profileDao.create(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops.. our bad");
        }
    }*/
}
