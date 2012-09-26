package com.nncloudtv.service;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.nncloudtv.model.Mso;

public class PlayerApiServiceTest {

	private PlayerApiService service;
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;    
	
    @Before
    public void setUp() {
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();
        
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(60);
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findNNMso();
        Locale locale = Locale.ENGLISH;
        service = new PlayerApiService();
        service.setLocale(locale);
        service.setMso(mso);
        String version = (req.getParameter("v") == null) ? "31" : req.getParameter("v");        
        service.setVersion(Integer.parseInt(version));        
        System.out.println("@Before - setUp");
    }
	 	 	
	@Test
	public void testBrandInfo() {
		Assert.assertTrue(service.brandInfo(req).contains("SUCCESS")); 
	}

	@Test
	public void testLogin() {
		String email = "a@a.com";
		String password = "123456";
		String loginStr = service.login(email, password, req, resp);
		Assert.assertTrue(loginStr.contains("SUCCESS")); 
	}
	
}
