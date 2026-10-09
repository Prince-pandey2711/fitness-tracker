package com.college.fitnesstracker.controller;

import com.college.fitnesstracker.model.User;
import com.college.fitnesstracker.service.SystemSettingService;
import com.college.fitnesstracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final SystemSettingService systemSettingService;

    public AuthController(UserService userService, SystemSettingService systemSettingService) {
        this.userService = userService;
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "msg", required = false) String msg,
                            @RequestParam(value = "error", required = false) String error,
                            Model model,
                            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }

        String appName = systemSettingService.getSettingValue("app_name", "FitTrack Pro");
        String announcement = systemSettingService.getSettingValue("system_announcement", "");
        model.addAttribute("appName", appName);
        model.addAttribute("announcement", announcement);
        model.addAttribute("msg", msg);
        model.addAttribute("error", error);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user);
            return user.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        } else {
            model.addAttribute("error", "Invalid email address or password.");
            model.addAttribute("appName", systemSettingService.getSettingValue("app_name", "FitTrack Pro"));
            model.addAttribute("email", email);
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }

        String allowReg = systemSettingService.getSettingValue("allow_registrations", "true");
        if ("false".equalsIgnoreCase(allowReg)) {
            return "redirect:/login?error=New+user+registrations+are+currently+closed+by+administrator.";
        }

        model.addAttribute("appName", systemSettingService.getSettingValue("app_name", "FitTrack Pro"));
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam(value = "role", defaultValue = "USER") String role,
                                  Model model) {
        try {
            userService.register(name, email, password, role);
            return "redirect:/login?msg=Account+created+successfully!+Please+log+in.";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("appName", systemSettingService.getSettingValue("app_name", "FitTrack Pro"));
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?msg=You+have+been+logged+out+successfully.";
    }
}
