package com.fpt.petstore.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.petstore.entities.Customer;
import com.fpt.petstore.entities.Order;
import com.fpt.petstore.entities.OrderItem;
import com.fpt.petstore.entities.Product;
import com.fpt.petstore.services.CookieService;
import com.fpt.petstore.services.PetStoreService;
import com.fpt.petstore.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fpt.petstore.entities.ConstVariable.*;

@Controller
public class AppController {

    @Autowired
    private PetStoreService petStoreService;

    @Autowired
    private CookieService cookieService;

    //get cookie
    private void getCookie(HttpSession session) {
        String listCartAsJson = cookieService.getValue("listCart", "");
        //if json list cart is empty
        if (!StringUtil.isEmpty(listCartAsJson) && session.getAttribute("listCart") == null) {
            Map<String, OrderItem> listCart = null;
            try {
                //decode json cookie and set to name session
                listCartAsJson = URLDecoder.decode(listCartAsJson, StandardCharsets.UTF_8.toString());
                //doc value json
                listCart = new ObjectMapper().readValue(listCartAsJson, new TypeReference<Map<String, OrderItem>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            //set attribute session
            session.setAttribute("listCart", listCart);
        }
    }

    @GetMapping(value = {"/"})
    public String viewHome() {

        return redirect + "trang-chu";
    }

    @GetMapping("/trang-chu")
    public String viewHome2(HttpSession session) {

        getCookie(session);
        return "index";
    }

    @GetMapping("/ve-chung-toi")
    public String viewAbout(HttpSession session) {
        getCookie(session);
        return "about";
    }

    @GetMapping("/blog")
    public String viewBlog(HttpSession session) {
        getCookie(session);
        return "blog";
    }

    @GetMapping("/information")
    public String viewInfo(HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return "information";
        } else {
            redirectAttributes.addFlashAttribute(messageNotification, "Chức năng chỉ dành cho người đăng nhập");
            redirectAttributes.addFlashAttribute(themeNotification, "warning");
            redirectAttributes.addFlashAttribute(titleNotification, "Cảnh cáo");
            return redirect + "trang-chu";
        }
    }

    @GetMapping("/chi-tiet-blog")
    public String viewBlogDetails(HttpSession session) {
        getCookie(session);
        return "blog-detail";
    }

    @GetMapping("/thanh-toan")
    public String viewCheckout(HttpServletRequest request,HttpSession session, ModelMap modelMap,RedirectAttributes redirectAttributes) {
        Customer customer = (Customer) session.getAttribute("customer");
        String referer = request.getHeader("Referer");
        if (customer != null) {
            getCookie(session);
            Map<String, OrderItem> listCart = (Map<String, OrderItem>) session.getAttribute("listCart");
            int totalPrice = 0;
            for (OrderItem orderItem : listCart.values()) {
                totalPrice += orderItem.getTotal();
            }
            modelMap.addAttribute("totalPrice", totalPrice);
            return "checkout";
        }else{
            redirectAttributes.addFlashAttribute(messageNotification, "Chức năng chỉ dành cho người đăng nhập");
            redirectAttributes.addFlashAttribute(themeNotification, "warning");
            redirectAttributes.addFlashAttribute(titleNotification, "Cảnh cáo");
            return redirectRefer+referer;
        }
    }

    @GetMapping("/gio-hang")
    public String viewsCart(HttpSession session, ModelMap modelMap) {
        getCookie(session);
        Map<String, OrderItem> listCart = (Map<String, OrderItem>) session.getAttribute("listCart");
        int totalPrice = 0;
        for (OrderItem orderItem : listCart.values()) {
            totalPrice += orderItem.getTotal();
        }
        modelMap.addAttribute("totalPrice", totalPrice);

        return "cart";
    }

    @GetMapping("/lien-he")
    public String viewContact(HttpSession session) {
        getCookie(session);
        //session.removeAttribute("listCart");
        return "contact";
    }

    @GetMapping("/lich-su-giao-dich")
    public String viewsHistory(HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return "history";
        } else {
            redirectAttributes.addFlashAttribute(messageNotification, "Chức năng chỉ dành cho người đăng nhập");
            redirectAttributes.addFlashAttribute(themeNotification, "warning");
            redirectAttributes.addFlashAttribute(titleNotification, "Cảnh cáo");
            return redirect + "trang-chu";
        }
    }
}
