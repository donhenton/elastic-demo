package com.dhenton9000.elastic.demo.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    //private final ErrorAttributes errorAttributes;
    @Override
    public String getErrorPath() {

        return ERROR_PATH;
    }

    @RequestMapping("/404")
    public String pageNotFound(Model model, HttpServletRequest request) {
        model.addAttribute("error", getErrorAttributes(request, true));
        return "404";
    }

}
