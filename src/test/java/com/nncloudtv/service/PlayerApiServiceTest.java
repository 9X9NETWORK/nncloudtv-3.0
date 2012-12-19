package com.nncloudtv.service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //String version = (req.getParameter("v") == null) ? "31" : req.getParameter("v");       
        String version = "32";
        System.out.println("version:" + version);
        service.setVersion(Integer.parseInt(version));        
        System.out.println("@Before - setUp");
    }
	 	 	
	@Test
	public void testBrandInfo() {
		Assert.assertTrue(service.brandInfo(req).contains("SUCCESS")); 
	}

	@Test
	public void testSetProfile() {	    
        String email = req.getParameter("email");	    
	}
	
	@Test
	public void testLogin() {
		String email = "a@a.com";
		String password = "123456";
		String loginStr = service.login(email, password, req, resp);
		Assert.assertTrue(loginStr.contains("SUCCESS")); 
	}

    @Test
    public void testQuickLogin() {
        String email = "a@a.com";
        String password = "123456";        
        String userInfo = service.login(email, password, req, resp);
        System.out.println(userInfo);
        userInfo = "";
        Pattern pattern = Pattern.compile(".*sphere\t((en|zh)).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
        } else {
            System.out.println("not Valid");
        }
//        userInfo = "";
//        int sphereIndex = userInfo.indexOf("sphere");        
//        String sphere = userInfo.length() > 9 : userInfo.substring(sphereIndex+7, sphereIndex+9);
//        if (sphere == null || sphere.contains("\n"))
//            System.out.println("bad data");
//        System.out.println("sphere len:" + sphere.length());
        Assert.assertTrue(userInfo.contains("SUCCESS")); 
    }
	
    @Test
    public void testChannelLineup() {
        String user = "8s12689Ns28RN2992sut";
        String channelStr = service.channelLineup(user, null, null, false, null, false, false, false, req);
        //System.out.println("channelStr:" + channelStr);
        String sections[] = channelStr.split("--");
        System.out.println("sections length:" + sections.length);
        //System.out.println(channelStr);
        String lines[] = sections[1].split("\n");
        System.out.println("---lines size----" + lines.length);
        String newChannelStr = "";
        for (String line : lines) {
            String str = ""; 
            String elm[] = line.split("\t");
            for (int i=0; i<elm.length; i++) {
                //System.out.println( "i=" + i + ":" + elm[i]); 
                if (i == 15)
                    elm[i] = "sub-count";
                else if (i == 16)
                    elm[i] = "view-count"; 
                str += elm[i] + "\t";                            
            }
            newChannelStr += str + "\n";
        }
        String l[] = newChannelStr.split("\n");
        System.out.println("---new lines size----" + l.length);
        System.out.println("channelStr:" + newChannelStr);
        Assert.assertTrue(channelStr.contains("SUCCESS")); 
    }
	
}
