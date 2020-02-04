package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import service.WebSocketServer;

import java.io.IOException;

@Controller
public class MController {
    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid){
        ModelAndView modelAndView = new ModelAndView("/socket");
        modelAndView.addObject("cid",cid);
        return  modelAndView;
    }

    @ResponseBody
    @RequestMapping("/socket/push/{cid}")
    public String pushToWeb(@PathVariable String cid,String message){
        try {
            WebSocketServer.sendInfo(message,cid);
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

}
