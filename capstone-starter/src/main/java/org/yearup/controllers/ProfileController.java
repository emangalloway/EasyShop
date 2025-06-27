package org.yearup.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {
private ProfileDao profileDao;

    @Autowired
    public ProfileController(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @GetMapping("")
    public Profile getProfile(int userId){
        return profileDao.getByUserId(userId);
    }

    @PutMapping("{userId}")
    public Profile addProfile(@PathVariable int userId, @RequestBody Profile profile){
        try {
             return profileDao.create(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops.. our bad");
        }

    }

    @PostMapping("{id}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Profile updateProfile(@PathVariable int id, @RequestBody Profile profile){
        try {
            return profileDao.update(id,profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops.. our bad");
        }
    }
}
