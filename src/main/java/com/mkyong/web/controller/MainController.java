package com.mkyong.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mkyong.users.model.Contact;
import com.mkyong.users.model.ContactForm;
import com.mkyong.users.model.User;
import com.mkyong.users.model.UserRole;
import com.mkyong.users.model.Users;

@Controller
@Transactional(propagation=Propagation.SUPPORTS)
public class MainController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private static List<Contact> contacts = new ArrayList<Contact>();
	 
    static {
        contacts.add(new Contact("Barack", "Obama", "barack.o@whitehouse.com", "147-852-965"));
        contacts.add(new Contact("George", "Bush", "george.b@whitehouse.com", "785-985-652"));
        contacts.add(new Contact("Bill", "Clinton", "bill.c@whitehouse.com", "236-587-412"));
        contacts.add(new Contact("Ronald", "Reagan", "ronald.r@whitehouse.com", "369-852-452"));
    }

	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security + Hibernate Example");
		model.addObject("message", "This is default page!");
		model.setViewName("hello");
		return model;

	}

	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security + Hibernate Example");
		model.addObject("message", "This page is for ROLE_ADMIN only!");
		model.setViewName("admin");
       model.addObject("user", new Users());
		return model;

	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public ModelAndView addUser(@ModelAttribute("user") Users user,BindingResult results,ModelMap map){
		ModelAndView model=new ModelAndView();
		Session session=sessionFactory.openSession();
		Transaction txn=session.beginTransaction();
		User u=new User();
		u.setEnabled(true);
		u.setUsername(user.getUserName());
		u.setPassword(user.getPassword());
		HashSet<UserRole> hs=new HashSet<UserRole>();
		UserRole ur=new UserRole();
		System.out.println("USER:-->"+user.getRole());
		ur.setRole(user.getRole());
		ur.setUser(u);
		hs.add(ur);
		u.setUserRole(hs);
		session.save(u);
		txn.commit();
		
		model.addObject("message", "User Has Been Added..!"+user.getUserName());
		model.setViewName("admin");
		
		
		return model;
		
		
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
    public ModelAndView get() {
         
        ContactForm contactForm = new ContactForm();
        contactForm.setContacts(contacts);
       ModelAndView mv= new ModelAndView("add_contact" , "contactForm", contactForm);
       mv.addObject("user", new Users());
         
        return mv;
    }
     
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView save(@ModelAttribute("contactForm") ContactForm contactForm) {
        System.out.println(contactForm);
        System.out.println(contactForm.getContacts());
        List<Contact> contacts = contactForm.getContacts();
         
        if(null != contacts && contacts.size() > 0) {
            this.contacts = contacts;
            for (Contact contact : contacts) {
                System.out.printf("%s \t %s \n", contact.getFirstname(), contact.getLastname());
            }
        }
         
        return new ModelAndView("show_contact", "contactForm", contactForm);
    }

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("login");

		return model;

	}

	// customize the error message
	private String getErrorMessage(HttpServletRequest request, String key) {

		Exception exception = (Exception) request.getSession().getAttribute(key);

		String error = "";
		if (exception instanceof BadCredentialsException) {
			error = "Invalid username and password!";
		} else if (exception instanceof LockedException) {
			error = exception.getMessage();
		} else {
			error = "Invalid username and password!";
		}

		return error;
	}

	// for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {

		ModelAndView model = new ModelAndView();

		// check if user is login
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			System.out.println(userDetail);

			model.addObject("username", userDetail.getUsername());

		}

		model.setViewName("403");
		return model;

	}

}