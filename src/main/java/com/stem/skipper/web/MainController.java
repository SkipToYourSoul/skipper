package com.stem.skipper.web;

import com.stem.skipper.domain.SensorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
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
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model, @RequestParam Map<String, String> queryParameters) throws ParseException {
        model.addAttribute("name", account);
        List<String> result = service.findSensorStartTime();
        String start = result.get(0);
        String status = result.get(1);

        model.addAttribute("start", start);
        model.addAttribute("status", status);

        return "index";
    }

    @GetMapping("/monitor")
    public String monitor(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model){
        model.addAttribute("name", account);

        List<SensorStatus> sensorStatuses = service.findOnlineSensorStatus();
        int sensorCount = sensorStatuses.size();

        model.addAttribute("count", sensorCount);
        model.addAttribute("status", sensorStatuses);

        return "monitor";
    }

    @GetMapping("/config")
    public String config(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model){
        model.addAttribute("name", account);

        return "config";
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
