package com.CachWeb.Cach.controller;

import com.CachWeb.Cach.dto.UserDto;
import com.CachWeb.Cach.entity.Currency;
import com.CachWeb.Cach.entity.ExchangeRate;
import com.CachWeb.Cach.entity.ExchangeRequest;
import com.CachWeb.Cach.entity.Image;
import com.CachWeb.Cach.repository.ImageRepository;
import com.CachWeb.Cach.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminController {
    private final CurrencyService currencyService;
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;
    private final ContactService contactService;
    private final ExchangeRequestService exchangeRequestService;
    private final ImageRepository imageRepository;

    @Autowired
    public AdminController(CurrencyService currencyService, UserService userService,
                           ExchangeRateService exchangeRateService, ContactService contactService,
                           ExchangeRequestService exchangeRequestService, ImageRepository imageRepository) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.exchangeRateService = exchangeRateService;
        this.contactService = contactService;
        this.exchangeRequestService = exchangeRequestService;
        this.imageRepository = imageRepository;
    }
    @GetMapping("/users")
    public String users(Model model,Principal principal){
          List<UserDto> users = userService.findAllUsers();
           model.addAttribute("users", users);
            boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "admin/users";
    }
    @GetMapping("/list")

    public String listCurrencies(Model model, HttpServletRequest request, Principal principal) {
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        // Check if the user is authenticated
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "admin/currency_list";
    }

    //To add Now Currency
    @GetMapping("/add")
    public String showAddCurrencyForm(Model model, Principal principal) {
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("currency", new Currency());
        return "admin/add_currency_form";
    }
    @PostMapping("/add")
    public String addCurrency(@ModelAttribute Currency currency) {
        currencyService.addCurrency(currency);
        return "redirect:/admin/list";
    }
    @GetMapping("/delete/currency/{id}")
    public String deleteCurrency(@PathVariable Long id) {
        currencyService.remove(id);
        return "redirect:/admin/list";
    }

//exchange
    @GetMapping("/exchange-list")
    public String listExchange(Model model , HttpServletRequest request, Principal principal) {
     List<ExchangeRate> rateList = exchangeRateService.getAllExchangeRates();
        model.addAttribute("rateList", rateList);


        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);


        return "admin/exchange_list";
    }
    @GetMapping("/contacts")
    public String listcontact(Model model , HttpServletRequest request, Principal principal) {

        model.addAttribute("contact", contactService.getAllContacts());

        // Check if the user is authenticated
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "admin/contactreq";
    }
    @GetMapping("/delete/contact/{id}")
    public String deleteContact(@PathVariable Long id){
        contactService.remove(id);
       return  "redirect:/admin/contacts";
    }


    //To add Exchange
    @GetMapping("/addExchange")
    public String addExtchange(Model model, Principal principal){
        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("currencies",currencies);
        model.addAttribute("exchangrate", new ExchangeRate());
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "admin/addExchange";
    }
    @PostMapping("/addExchange")
    public String saveExchange( @ModelAttribute ExchangeRate exchangeRate){
      exchangeRateService.save(exchangeRate);
        return "redirect:/admin/exchange-list";
    }
      @GetMapping("/delete/exchange/{id}")
          public String deleteExchange(@PathVariable Long id) {
        exchangeRateService.remove(id);
        return "redirect:/admin/exchange-list";
    }

    @GetMapping("/exchange-requests")
    public String showEntities(Model model, Principal principal, @RequestParam(defaultValue = "0") int page) {
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);

        // Set the number of items per page
        int pageSize = 4; // Adjust this as needed

        // Create a Pageable object for pagination using PageRequest
        Pageable pageable = PageRequest.of(page, pageSize);

        // Retrieve a Page of ExchangeRequest entities
        Page<ExchangeRequest> exchangePage = exchangeRequestService.getPageOfRequestsWithoutArchived(pageable);


        // Add the page content and pagination information to the model
        model.addAttribute("exchange", exchangePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", exchangePage.getTotalPages());

        return "admin/excahngeRequests"; // Corrected view name
    }
    @GetMapping("/all-requests-including-archived")
    public String getAllRequestsWithArchived(Model model , Principal principal,@RequestParam(defaultValue = "0") int page) {
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        int pageSize = 4; // Adjust this as needed
        Pageable pageable = PageRequest.of(page, pageSize);
        model.addAttribute("currentPage", page);

        // Add a flag to the model to indicate whether the user is authenticated


        Page<ExchangeRequest> allRequests = exchangeRequestService.getAllRequestsIncludingArchived(pageable);

        model.addAttribute("exchange", allRequests.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", allRequests.getTotalPages());
        return "admin/all_requests_including_archived";
    }
    @PostMapping("/update-request")
    public String updateRequest(@RequestParam("requestId") Long requestId,
                                @RequestParam("sendingAmount") BigDecimal sendingAmount,
                                @RequestParam("receivingAmount") BigDecimal receivingAmount,
                                @RequestParam("walletNumber") String walletNumber) {

        exchangeRequestService.updateRequest(requestId, sendingAmount, receivingAmount, walletNumber);

        return "redirect:/admin/exchange-requests";
    }


    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Image image = imageRepository.getReferenceById(id);

        if (image != null && image.getData() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/exchange/delete/{id}")
    public String deleteRequests(@PathVariable("id") Long id) {
        exchangeRequestService.remove(id);
        return "redirect:/admin/all-requests-including-archived";
    }




    @PostMapping("/archive-request")
    public String archiveRequest(@RequestParam Long requestId) {
        exchangeRequestService.archiveRequest(requestId);

        return "redirect:/admin/exchange-requests";
    }

    @GetMapping("/UserReqests")
    public String getUserAndRequests(Model model , Principal principal) {
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("requests",  exchangeRequestService.userRequestCounts());

        return "admin/user-Reqests";
    }

    @GetMapping("/requests-forUser/{email}")
    public String requestsforUser(@PathVariable("email") String email, Model model, Principal principal) {
        boolean isAuthenticated = principal != null;

        // Add a flag to the model to indicate whether the user is authenticated
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("requests",  userService.getExchangeRequestsForUser(email));
        return "admin/user-AllRequests";
    }

}












