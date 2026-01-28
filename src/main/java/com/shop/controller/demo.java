package com.shop.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class demo {

    @RequestMapping ("/Read/unAuth/Pro/xxx")
    public void demo() throws IOException {

    }


    @PreAuthorize("hasRole('User')")
    @RequestMapping ("/seller")
    public String seller(){
        log.info("進來");
        return "Seller ";
    }
}
