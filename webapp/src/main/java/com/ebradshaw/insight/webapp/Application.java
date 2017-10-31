package com.ebradshaw.insight.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@ServletComponentScan
@Controller
public class Application {

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return "Hello world!";
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        System.out.println(Application.class.getClassLoader());
        SpringApplication.run(Application.class);
    }

}
