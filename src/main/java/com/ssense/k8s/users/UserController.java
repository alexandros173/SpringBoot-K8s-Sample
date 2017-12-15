package com.ssense.k8s.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/userApp")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/save")
    public void addUser(@RequestBody User user){
        userRepository.save(user);
    }

    @RequestMapping("/findByName/{name}")
    public List<User> findByName(@PathVariable("name") String name){
        return userRepository.findByName(name);
    }


    @RequestMapping("/findAll")
    public List<User> findAll(){
        return userRepository.findAll();
    }

}
