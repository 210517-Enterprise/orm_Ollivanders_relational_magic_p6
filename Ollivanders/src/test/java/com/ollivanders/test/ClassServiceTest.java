package com.ollivanders.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ollivanders.repos.GenericClassReposistory;
import com.ollivanders.services.ClassService;

//import com.ollivanders.services.ClassService;/test/java/com.ollivanders.test.PersonTest;
public class ClassServiceTest {

	
	  
	  private ClassService<PersonTest> classService; 
	  
	  private GenericClassReposistory mockRepo;
	  
	  @Before public void setup() { 
		  classService = new ClassService<PersonTest>(PersonTest.class); 
		  mockRepo = mock(GenericClassReposistory.class);
//	  classService.createClassTable().equals(mockRepo);
	  
	  }
	  
	  @After public void teardown() { classService = null; mockRepo = null; }
	  //verify checks whether the create class method is being called or not
	  //since the method is void we cannot check for a value to be returned
	  
	  @Test public void testCreateTable() {
	  doNothing().when(mockRepo).createClassTable();
	  classService.createClassTable();
	  verify(mockRepo,times(1)).createClassTable(); }
	  
	  
	 
//	private static SeshFactory Connection;
//	private SessionManager sess;
//	
//	@BeforeClass
//    public static void setup() {
//        Connection = SeshFactory.getConnection();
//        System.out.println("SessionFactory created");
//    }
//     
//    @AfterClass
//    public static void tearDown() {
//        if (sessionFactory != null) sessionFactory.close();
//        System.out.println("SessionFactory destroyed");
//    }
//     
//    @Test
//    public void testCreate() {
//    }
//     
//    @Test
//    public void testUpdate() {
//    }
//     
//    @Test
//    public void testGet() {
//    }
//     
//    @Test
//    public void testList() {
//    }
//     
//    @Test
//    public void testDelete() {
//    }  
//     
//    @BeforeEach
//    public void openSession() {
//        session = sessionFactory.openSession();
//        System.out.println("Session created");
//    }
//     
//    @AfterEach
//    public void closeSession() {
//        if (session != null) session.close();
//        System.out.println("Session closed\n");
//    }  
//    
    
    
}
	

