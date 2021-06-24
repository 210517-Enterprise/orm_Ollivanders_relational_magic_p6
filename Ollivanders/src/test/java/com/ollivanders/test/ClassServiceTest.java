package com.ollivanders.test;

import static org.mockito.Mockito.mock;

import org.junit.Before;

import com.ollivanders.repos.CrudRepository;
import com.ollivanders.services.ClassService;

//import com.ollivanders.services.ClassService;/test/java/com.ollivanders.test.PersonTest;
public class ClassServiceTest {

	
	
	private ClassService<PersonTest> classService;
	private CrudRepository mockRepo;
	
	@Before
	public void setup() {
		classService = new ClassService<PersonTest>(PersonTest.class);
		mockRepo = mock(CrudRepository.class);
	//	classService.createClassTable(Person.class) = mockRepo;
		
	}
}
