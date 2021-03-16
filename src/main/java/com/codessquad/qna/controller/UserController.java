package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session){
        User user = userRepository.findByUserId(userId);

        if (user == null){
            logger.debug("Login Failure!");
            return "redirect:/users/loginForm";
        }

        if(!user.isMatchingPassword(password)){
            logger.debug("Login Failure!");
            return "redirect:/users/loginForm";
        }
        logger.debug("Login Success!");
        session.setAttribute("user",user);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/";
    }

    @PostMapping
    public String create(User user) {
        userRepository.save(user);
        logger.debug("createUser : {}", user.toString());
        return "redirect:/users";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such data"));
        model.addAttribute("user", user);
        return "users/profile";
    }


    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such data"));
        model.addAttribute("user", user);
        return "users/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id, String checkPassword, User updateUserInfo, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such data"));
        if (!user.isMatchingPassword(checkPassword))
            return "redirect:/users/{id}/form";

        if (updateUserInfo.getPassword() == "" || updateUserInfo.getPassword() == " ")
            updateUserInfo.setPassword(user.getPassword());

        user.update(updateUserInfo);
        userRepository.save(user);
        return "redirect:/users";

    }
}
