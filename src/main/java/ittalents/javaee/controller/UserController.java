package ittalents.javaee.controller;

import ittalents.javaee.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class UserController {

    private static ArrayList<User> list = new ArrayList<>();
//    static{
//        list.add(new User("Pesho", 13, "petyr@abv.bg", "CSKArulz"));
//        list.add(new User("Tosho", 23, "toshetyyy@abv.bg", "levskiilismyrt"));
//    }

    @GetMapping(value = "/hi")
    public String sayHi(){
        return "Hi from Spring";
    }

    @GetMapping(value = "/users/all")
    public ArrayList<User> getUsers(){
        return list;
    }

    @PostMapping(value = "/users/add")
    public void saveUser(@RequestBody User user){
        System.out.println(user.toString());
    }

    @GetMapping(value = "/users/{name}")
    public String getUserByName(@PathVariable("name") String name){
        return "Hi, " + name;
//        for(User u : list){
//            if(u.getName().equals(name)){
//                return u;
//            }
//        }
//        return null;
    }
}
