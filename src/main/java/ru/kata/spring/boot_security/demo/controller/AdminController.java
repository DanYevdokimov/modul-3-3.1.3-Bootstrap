package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public ModelAndView getUserAddForm() {
        ModelAndView modelAndView = new ModelAndView("admin/adminPanel");
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.addObject("roles", roleService.getAllRoles());
        modelAndView.addObject("newUser", new User());
        return modelAndView;
    }

    @GetMapping("/updateUser")
    public ModelAndView getUserUpdateForm(@RequestParam(value = "editUserId") Long editUserId) {
        ModelAndView modelAndView = new ModelAndView("admin/adminPanel");
        modelAndView.addObject("existingUser", userService.getUserById(editUserId));
        return modelAndView;
    }

    @PostMapping("/saveUser")
    public ModelAndView saveUser(@ModelAttribute("newUser") User user,
                                 @RequestParam(value = "newRoles") String[] newRoles) {
        userService.saveUser(user, newRoles);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/updateUser")
    public ModelAndView updateUser(@RequestParam("userId") long id,
                                   @ModelAttribute("existingUser") User user,
                                   @RequestParam(value = "selectedRoles", required = false) String[] selectedRoles) {
        userService.updateUser(id, user, selectedRoles);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/deleteUser")
    public ModelAndView deleteUser(@RequestParam("userId") long id) {
        userService.deleteUser(id);
        return new ModelAndView("redirect:/admin");
    }
}