<!-- Harry Potter font obtained open sourced from: https://www.fontspace.com/category/harry-potter -->
[![Harry Potter fonts](https://see.fontimg.com/api/renderfont4/MVZ6w/eyJyIjoiZnMiLCJoIjoxMzAsInciOjIwMDAsImZzIjo2NSwiZmdjIjoiIzAwMDAwMCIsImJnYyI6IiNGRkZGRkYiLCJ0IjoxfQ/T2xsaXZhbmRlcidzIFJlbGF0aW9uYWwgTWFnaWM/harry-p.png)](https://www.fontspace.com/category/harry-potter)


<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6">
    <img src="http://www.webweaver.nu/clipart/img/fantasy/wizards/lightning-bolts.gif" alt="zipZapImAWizard" width="120" height="120">
  </a>

  <h3 align="center">Enterprise Custom ORM Project 1</h3>

  <p align="center">
    Olllvanders is a basic Java ORM that works through inheritance. Currently works with just postgresql, but this may expand in the future.

Currently Ollivanders takes in an ollivanders.xml to configure database connection.
    <br />
    <a href="https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6"><strong>Explore the ORM »</strong></a>
    <br />
    <br />
    <a href="https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6/WizardWanderland">View Demo</a>
    ·
    <a href="https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6/issues">Report Bug</a>
    ·
    <a href="https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#technologies-used">Technologies Used</a></li>
      </ul>
    </li>
    <li>
      <a href="#features">Features</a>
    </li>
    <li><a href="#setup">SETUP</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Ollivanders finds its inspiration from Aestivate and Hibernate in our intial planning. We built off the general crud repo and class service models from Aestivate and used annotations to help parse SQL strings. Stretch goals include adding a join read methods functionality and supporting many to many relations through creating a reference table. An additional stretch goal would be to alter tables within the database as well.



### Technologies Used
* JDBC
* JAVA
* Git
* Github
* Reflection
* log4j 
* junit
* postgresql
* commons-dbcp
* mockito



<!-- GETTING STARTED -->
## Features

* Ability to perform CRUD operations based on the model class
* Find entries in the class database that satisify conditions on given columns with given values
* Check if an entry exists of an object with the same primary key
* Establish a foreign key constraint to parent class tables




<!-- USAGE EXAMPLES -->
## SETUP

For Ollivanders to work, it requires an ollivander.xml file in the resources directory. This file informs Ollivander of the database type it is connecting to (eg. postgresql), the url, login, and password. In addition it takes arguments for the minimum number of connections to idle, the max number of connections to idle, and the max open prepared statements there can be.


~~~ xml
<?xml version="1.0" encoding="UTF-8" ?>
<Configuration>

    <!-- Database connection info -->
    <Database name = "postgresql">
        <Url url="jdbc:postgresql://ollivandersdb.c9wbv7ktss7f.us-east-2.rds.amazonaws.com:5432/postgres?currentSchema=public"/>
        <Login login="postgres"/>
        <Password password="ollivanderspassword"/>
        <MinIdle minIdle="5"/>
        <MaxIdle maxIdle="100"/>
        <maxOpenPreparedStatements maxOpen="10000"/>
    </Database>

</Configuration>
~~~


<!-- ROADMAP -->
## Usage

The way Ollivander's connects to a database is by parsing a .xml file in the XMLReader class and creates a session using session manager which initializes the sessionfactory class in the util package. 

Ollivander's uses the annotations Column and Entity to specify the columns within a postgresql database. below is the code snippet for the column annotation: 

~~~ java
package com.ollivanders.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ollivanders.model.SQLConstraints;
import com.ollivanders.repos.SQLType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Column {
	String columnName();
	SQLType columnType();
	SQLConstraints columnConstraint();
}
~~~

Within the GeneralClassRepository class, we are able to create a class table and add a constraint for a foreign key.<br>
	 * The foreign key will always reference the primary key of a separate table if
	 * it exists. If no primary key exists in the separate table the class table
	 * will not be created. Also if not foreign key constraint is found in the
	 * current class table the constraint will not be added and the table class will
	 * not be made. Shown below is our example of the create table method:
   
 ~~~ java
 public void createClassTable(GenericClassReposistory parentTable) {

		// Ensure that the foreignTable Repo exists.
		assert parentTable != null : "The foreign table repository does not exist";

		// Ensure that the foreignTable has a class table
		assert parentTable.hasClassTable != false : "The foreign table does not exist";

		// Get the column fields of the class table.
		ColumnField[] columns = getColumnFields();

		// Get the fields of the foreign key of the tClass and the Primary key of the
		// foreignTable
		try {
			Field tClassFK = getFKField();
			Field tForeignPK = parentTable.getPKField();

			// Ensure that the SQL Types match
			//Note Integer is equal to serial.
			if ((tClassFK.getAnnotation(Column.class).columnType().equals(tForeignPK.getAnnotation(Column.class).columnType())) || 
					(tClassFK.getAnnotation(Column.class).columnType().equals(SQLType.INTEGER) 
							&& tForeignPK.getAnnotation(Column.class).columnType().equals(SQLType.SERIAL))) {
				// Create the class table
				createClassTable();
				setParentTables(parentTable);

			} else {
				throw new SQLException("The foreign key and primary key data types do not match");
			}
		} catch (NoSuchFieldException | SQLException e) {
			e.printStackTrace();
		}

	}

 
 ~~~ 

sql type constraints allowed within Ollies RM include INT, BOOL, DATE, VARCHAR, SERIAL, DECIMAL and the column constraints allowed include all except DEFAULT and CHECK as check and default has not been fully implemented. 

WizardWanderland is a demo project which showcases the general functionality of Ollies and uses its own driver to manipulate data and create its own database.

<!-- CONTACT -->
## Contact

### Kyle Castillo - kylea.castillo1999@gmail.com or kylea.castillo@revature.net

### Jake Geiser - jake.geiser@revature.net

### Victor Knight - victor.issayknight@revature.net
### Project Link: [https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6](https://github.com/210517-Enterprise/orm_Ollivanders_relational_magic_p6)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements

* Kyle Castillo
* Jake Geiser
* Victor Knight

* Special thanks to Nick Gianino with lockpicking magic ✨

