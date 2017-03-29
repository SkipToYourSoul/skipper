package com.stem.skipper.web;

import com.stem.skipper.domain.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liye on 2017/3/25.
 */

@Controller
public class MainController {
    @Autowired
    DataService service;

    @GetMapping("/")
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model){
        model.addAttribute("name", account);

        List<SensorStatus> sensorStatuses = service.findOnlineSensorStatus();
        int sensorCount = sensorStatuses.size();

        model.addAttribute("count", sensorCount);
        model.addAttribute("status", sensorStatuses);

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/loginPost")
    public @ResponseBody
    Map<String, Object> loginPost(String account, String password, HttpSession session){
        Map<String, Object> map = new HashMap<String, Object>();
        if (!LoginConfig.password.equals(password)) {
            map.put("success", false);
            return map;
        }

        // 设置session
        session.setAttribute(WebSecurityConfig.SESSION_KEY, account);

        map.put("success", true);
        return map;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 移除session
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/login";
    }
}
